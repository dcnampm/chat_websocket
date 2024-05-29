package com.nampd.chat.user;

import com.nampd.chat.jwt.JwtService;
import com.nampd.chat.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public User login(User user) {
        Optional<User> existingUser = userRepository.findByNickName(user.getNickName());

        if (existingUser.isPresent()) {
            User foundUser = existingUser.get();

            System.out.println(foundUser);

            foundUser.setStatus(Status.ONLINE);
            userRepository.save(foundUser);
            return foundUser;
        } else {
            user.setStatus(Status.ONLINE);
            userRepository.save(user);
            return user;
        }
    }

    public void disconnect(User user) {
        Optional<User> storedUser = userRepository.findByNickName(user.getNickName());

        System.out.println(storedUser);

        storedUser.ifPresent(savedUser -> {
            savedUser.setStatus(Status.OFFLINE);
            userRepository.save(savedUser);
        });
    }


    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }

    public User authenticate(User user) {
        Optional<User> foundUser = userRepository.findByNickName(user.getNickName());

        if (foundUser.isPresent()) {
//            if (passwordEncoder.matches(loginForm.getPassword(), foundUser.getPassword()))
            if (user.getPassword().equals(foundUser.get().getPassword())) {
//                System.out.println(foundUser);
                return foundUser.get();
            } else {
                throw new IllegalArgumentException("Wrong password");
            }
        } else {
            throw new IllegalArgumentException("User does not exist");
        }
    }
}
