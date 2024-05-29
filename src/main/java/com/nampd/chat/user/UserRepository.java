package com.nampd.chat.user;

import com.nampd.chat.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findAllByStatus(Status status);

    Optional<User> findByNickName(String nickName);

//    User findByUsername(String username);
//
    boolean existsByNickName(String username);
}
