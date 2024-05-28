package com.nampd.chat.jwt;

import com.nampd.chat.user.User;
import com.nampd.chat.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody User user
    ) {
        User connectedUser = userService.authenticate(user);
//        return ResponseEntity.ok(jwtService.generateToken(connectedUser));

        if (connectedUser != null) {
            String token = jwtService.generateToken(connectedUser);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

}
