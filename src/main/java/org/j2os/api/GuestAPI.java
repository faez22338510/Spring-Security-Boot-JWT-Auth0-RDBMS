package org.j2os.api;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.j2os.common.TokenProvider;
import org.j2os.model.UserEntity;
import org.j2os.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GuestAPI {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public GuestAPI(UserService userService, TokenProvider tokenProvider, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/create-user")//change method type, after learning (@    )
    public void createUser(@ModelAttribute UserEntity userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userService.save(userEntity);
    }

    @GetMapping("/login")//change method type, after learning (@PostMapping)
    public Map<String, String> login(@ModelAttribute UserEntity userEntity) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userEntity.getUsername(), userEntity.getPassword()));
        return tokenProvider.getToken(userEntity.getUsername());
    }

    @PostMapping("/refresh-token")
    public Map<String, String> refreshTokens(HttpServletRequest request) throws Exception {
        Map<String, String> tokens = new HashMap<>();
        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
            try {
                // Verify the refresh token and get the username
                String username = tokenProvider.verifyToken(refreshToken);
                // Generate a new access and refresh token and return them
                tokens = tokenProvider.getToken(username);
            } catch (JWTVerificationException e) {
                // Token is not valid
                throw new Exception("Invalid refresh token");
            }
        }
        return tokens;
    }
}
