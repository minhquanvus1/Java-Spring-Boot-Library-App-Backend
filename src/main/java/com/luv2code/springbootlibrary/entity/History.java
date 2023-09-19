package com.luv2code.springbootlibrary.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "checkout_date")
    private String checkoutDate;

    @Column(name = "returned_date")
    private String returnedDate;

    private String title;

    private String author;

    private String description;

    private String img;

    public History(String userEmail, String checkoutDate, String returnedDate, String title, String author, String description, String img) {
        this.userEmail = userEmail;
        this.checkoutDate = checkoutDate;
        this.returnedDate = returnedDate;
        this.title = title;
        this.author = author;
        this.description = description;
        this.img = img;
    }
}
