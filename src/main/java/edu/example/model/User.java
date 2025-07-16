package edu.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private static int idSequence = 0;

    private int id;

    private String username;

    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;

        idSequence++;
        this.id = idSequence;
    }
}
