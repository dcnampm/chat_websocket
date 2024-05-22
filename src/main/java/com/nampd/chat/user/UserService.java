package com.nampd.chat.user;

import com.nampd.chat.jwt.JwtService;
import com.nampd.chat.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void saveUser(User user) {

        if (userRepository.existsByNickName(user.getNickName())) {
            throw new IllegalArgumentException("NickName already exists");
        } else {

//            String encodedPassword = passwordEncoder.encode(user.getPassword());
//            user.setPassword(encodedPassword);

            userRepository.save(user);
        }
    }

    public void login(User user) {
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
    }

    public void disconnect(User user) {
        User storedUser = userRepository.findByNickName(user.getNickName());
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            userRepository.save(storedUser);
        }
    }

    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }

    public User authenticate(User user) {
        User foundUser = userRepository.findByNickName(user.getNickName());

        if (foundUser != null) {
//            if (passwordEncoder.matches(loginForm.getPassword(), foundUser.getPassword()))
            if (user.getPassword().equals(foundUser.getPassword())) {
                System.out.println(foundUser);
                return foundUser;
            } else {
                throw new IllegalArgumentException("Wrong password");
            }
        } else {
            throw new IllegalArgumentException("User does not exist");
        }
    }

//    public void setUserOnline(LoginForm loginForm, boolean online) {
//        if (loginForm.getUsername() == null || loginForm.getPassword() == null) {
//            throw new IllegalArgumentException("Missed information");
//        }
//        User foundUser = userRepository.findByUsername(loginForm.getUsername());
//        if (foundUser != null) {
//            if (passwordEncoder.matches(loginForm.getPassword(), foundUser.getPassword())) {
//                foundUser.setOnline(online);
//                userRepository.save(foundUser);
//            } else {
//                throw new IllegalArgumentException("Wrong password");
//            }
//        } else {
//            throw new IllegalArgumentException("User does not exist");
//        }
//    }
//
//    public void setUserOffline(String username, boolean online) {
//        User user = userRepository.findByUsername(username);
//        if (user != null) {
//            user.setStatus(online);
//            userRepository.save(user);
//        } else {
//            throw new IllegalArgumentException("User not exist");
//        }
//    }
}
