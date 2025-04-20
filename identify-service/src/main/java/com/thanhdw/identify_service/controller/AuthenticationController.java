package com.thanhdw.identify_service.controller;

import com.nimbusds.jose.JOSEException;
import com.thanhdw.identify_service.dto.request.ApiResponse;
import com.thanhdw.identify_service.dto.request.AuthenticationRequest;
import com.thanhdw.identify_service.dto.request.IntrospectRequest;
import com.thanhdw.identify_service.dto.request.LogoutRequest;
import com.thanhdw.identify_service.dto.response.AuthenticationResponse;
import com.thanhdw.identify_service.dto.response.IntrospectResponse;
import com.thanhdw.identify_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
     var  result = authenticationService.authenticate(authenticationRequest);
     return ApiResponse.<AuthenticationResponse>builder()
             .result(result)
             .build();
     
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest authenticationRequest) throws ParseException, JOSEException {
        var result = authenticationService.introspect(authenticationRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
    @PostMapping("logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return  ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

}
