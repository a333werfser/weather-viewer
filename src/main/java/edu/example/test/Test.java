package edu.example.test;

import edu.example.model.Session;

public class Test {

    public static void main(String[] args) {
        String str = new Session(1).getExpireDate().toString();

        System.out.println("test breakpoint");
    }
}
