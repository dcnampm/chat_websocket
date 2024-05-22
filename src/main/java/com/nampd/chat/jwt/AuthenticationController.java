package com.nampd.chat.jwt;

import com.nampd.chat.user.User;
import com.nampd.chat.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(jwtService.generateToken(connectedUser));
    }
}
