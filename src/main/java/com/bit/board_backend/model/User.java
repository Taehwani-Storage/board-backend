package com.bit.board_backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@Data
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String role;
}
