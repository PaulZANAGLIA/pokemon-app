package com.paulz.user.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
@Table(name = "trainer") 
public class User {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) @Setter(value = AccessLevel.NONE)
    long id;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false, length = 20)
    @NotBlank
    private String username;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 8)
    private String password;

    @Column(nullable = false)
    private int elo = 0;

    @ManyToMany 
    private Set<Role> roles;
}
