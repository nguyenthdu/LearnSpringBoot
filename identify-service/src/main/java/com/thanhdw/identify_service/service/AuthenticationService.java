package com.thanhdw.identify_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.thanhdw.identify_service.dto.request.AuthenticationRequest;
import com.thanhdw.identify_service.dto.request.IntrospectRequest;
import com.thanhdw.identify_service.dto.request.LogoutRequest;
import com.thanhdw.identify_service.dto.request.RefreshRequest;
import com.thanhdw.identify_service.dto.response.AuthenticationResponse;
import com.thanhdw.identify_service.dto.response.IntrospectResponse;
import com.thanhdw.identify_service.entity.InvalidatedToken;
import com.thanhdw.identify_service.entity.User;
import com.thanhdw.identify_service.exception.AppException;
import com.thanhdw.identify_service.exception.ErrorCode;
import com.thanhdw.identify_service.repository.InvalidatedTokenRepository;
import com.thanhdw.identify_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    
    UserRepository userRepository;
    
    InvalidatedTokenRepository invalidatedTokenRepository;
    
    @NonFinal
    @Value("${jwt.singing-key}")
    protected String KEY;
    
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;
    
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;
    
    //    protected static final String KEY="2iEh1Qn9aEk3jbTIodkPyibsooGNr0dFK4W9+LA9hTndjkd3sjm2KgJ4NkB/2AJe";
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("sign-key", KEY);
        var user = userRepository.findByUsername(request.getUsername())
                                 .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
    
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getUsername())//dai dien user dang nhap
                                                              .issuer("http://localhost:8080")//dia chi server
                                                              .issueTime(new Date()).expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))//thoi gian ton tai token
                                                              //de kiem tra token het han thi chi luu id nhung token da het han ma khong can luu tat ca token
                                                              .jwtID(UUID.randomUUID().toString())
                                                              .claim("scope", buildScope(user))//thong tin khac
                                                              .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot sign token", e);
            throw new RuntimeException(e);
        }
    }
    
    //Check token
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        //tra ve token true or false thay vi exception
        boolean isValid = true;
        
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        
        return IntrospectResponse.builder().valid(isValid).build();
    }
    
    //Get roles of user
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");//define delimiter is " " for all roles
        //add role into scope
        if(!CollectionUtils.isEmpty(user.getRoles())) user.getRoles().forEach(role -> {
            stringJoiner.add("ROLE_" + role.getName());
            if(!CollectionUtils.isEmpty(role.getPermissions()))
                role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
        });
        return stringJoiner.toString();
    }
    
    public void logout(LogoutRequest token) throws ParseException, JOSEException {
        try {
            var signedJWT = verifyToken(token.getToken(), true);
            // lay token id va thoi gian het han
            
            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date exprirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(exprirationTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token is invalid");
        }
        
    }
    
    private SignedJWT verifyToken(String token, boolean isRefresh
            //true is verify refresh token else if false is verify access token or instrospect token
    ) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date exprirationTime = (isRefresh) ? new Date(
                signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                         .toEpochMilli()) :
                
                signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if(!verified || exprirationTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        //neu token ton tai trong bang InvalidatedToken thi khong cho phep
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }
    
    //Khi token het han thi client se gui request den server goi refresh token
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expirationTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
        // lay thong tin user tu token
        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // tao token moi
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
        
    }
}
