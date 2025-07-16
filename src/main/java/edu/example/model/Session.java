package edu.example.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Session {

    private String sessionId;

    private int userId;

    private LocalDateTime expireDate;

    private int maxAge;

    public Session(int userId) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.maxAge = 200;
    }

}
