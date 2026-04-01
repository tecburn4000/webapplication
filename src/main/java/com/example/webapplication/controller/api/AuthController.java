package com.example.webapplication.controller.api;

import com.example.webapplication.dto.jwt.JwtLoginRequest;
import com.example.webapplication.dto.jwt.JwtResponse;
import com.example.webapplication.security.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth/token")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody @Valid JwtLoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));

        String token = jwtService.generateToken(auth.getName());

        return ResponseEntity.ok(new JwtResponse(token));
    }
}
