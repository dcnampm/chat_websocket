package com.nampd.chat.user;

import com.nampd.chat.model.Status;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickName;
    private String fullName;
    private String password;
    private Status status;

//    @PrePersist
//    protected void onCreate() {
//        this.id = UUID.randomUUID().toString();
//    }

    public User(String nickName, String password) {
        this.nickName = nickName;
        this.password = password;
    }
}
