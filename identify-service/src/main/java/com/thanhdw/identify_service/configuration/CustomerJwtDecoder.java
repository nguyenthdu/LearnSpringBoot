package com.thanhdw.identify_service.configuration;

import com.nimbusds.jose.JOSEException;
import com.thanhdw.identify_service.dto.request.IntrospectRequest;
import com.thanhdw.identify_service.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
public class CustomerJwtDecoder implements JwtDecoder {
    
    @Value("${jwt.singing-key}")
    private String signingKey;
    
    @Autowired
    private AuthenticationService authenticationService;
    
    private NimbusJwtDecoder nimbusJwtDecoder;
    
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // Check if the token is valid
         var response =    authenticationService.introspect(IntrospectRequest.builder().token(token).build());
         if(!response.isValid())
             throw new JwtException("Token invalid");
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e.getMessage());
        }
        // Decode the token
        if(Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signingKey.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
            
        }
        System.out.println(nimbusJwtDecoder);
        return nimbusJwtDecoder.decode(token);
    }
}
