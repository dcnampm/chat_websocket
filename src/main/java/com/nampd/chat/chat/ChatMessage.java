package com.nampd.chat.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.awt.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessage {
    @Id
    private String id;
    private String chatId;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timestamp;
}
