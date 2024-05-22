package com.nampd.chat.user;

import com.nampd.chat.model.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    private String id;
    private String nickName;
    private String fullName;
    private String password;
    private Status status;
}
