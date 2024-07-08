package com.paulz.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
public class Role {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE) 
    @Setter(value = AccessLevel.NONE)
    private long id;
    private String name;
}