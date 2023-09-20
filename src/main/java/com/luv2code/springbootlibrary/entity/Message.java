package com.luv2code.springbootlibrary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    private String title;

    private String question;

    @Column(name = "admin_email")
    private String adminEmail;

    private String response;

    private boolean closed;

    public Message(String title, String question) {
        this.title = title;
        this.question = question;
    }
}

