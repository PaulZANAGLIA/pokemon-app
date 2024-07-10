package com.paulz.user.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trainer") 
public class User {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private int elo;

    @ManyToMany 
    private Set<Role> roles;

    @ManyToMany
    @JsonManagedReference
    private Set<User> friends = new HashSet<>();

    @ManyToMany(mappedBy = "friends")
    @JsonBackReference
    private Set<User> friendsOf;
}
