package com.nampd.chat.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

//    @GetMapping("/online")
//    public List<User> getOnlineUsers() {
//        return userService.findConnectedUsers();
//    }
//
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.ok("Register successfully");
    }
//
//    @PostMapping("/login")
//    public ResponseEntity<GenericResponse<?>> login(@RequestBody LoginForm loginForm) {
//        userService.setUserOnline(loginForm, true);
//        return ResponseEntity.ok(new GenericResponse<>(null));
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<GenericResponse<?>> logout(@RequestBody String username) {
//        userService.setUserOffline(username, false);
//        return ResponseEntity.ok(new GenericResponse<>(null));
//    }

    @MessageMapping("/user.addUser")
    @SendTo("/user/public")
    public User addUser(
            @Payload User user
    ) {
        userService.addUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public User disconnectUser(
            @Payload User user
    ) {
        userService.disconnect(user);
        return user;
    }

    @GetMapping("/online")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }
}
