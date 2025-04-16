package com.thanhdw.identify_service.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.thanhdw.identify_service.dto.request.AuthenticationRequest;
import com.thanhdw.identify_service.dto.request.IntrospectRequest;
import com.thanhdw.identify_service.dto.response.AuthenticationResponse;
import com.thanhdw.identify_service.dto.response.IntrospectResponse;
import com.thanhdw.identify_service.entity.User;
import com.thanhdw.identify_service.exception.AppException;
import com.thanhdw.identify_service.exception.ErrorCode;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    @NonFinal
    @Value("${jwt.singing-key}")
    protected String KEY;
    
    //    protected static final String KEY="2iEh1Qn9aEk3jbTIodkPyibsooGNr0dFK4W9+LA9hTndjkd3sjm2KgJ4NkB/2AJe";
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
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
                .issueTime(new Date()).expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))//thoi gian ton tai token
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
        JWSVerifier verifier = new MACVerifier(KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date exprirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder().valid(verified && exprirationTime.after(new Date())).build();
    }
    //Get roles of user
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");//define delimiter is " " for all roles
//        if(!CollectionUtils.isEmpty(user.getRoles()))
//            user.getRoles().forEach(stringJoiner::add);
        return stringJoiner.toString();
    }
}
