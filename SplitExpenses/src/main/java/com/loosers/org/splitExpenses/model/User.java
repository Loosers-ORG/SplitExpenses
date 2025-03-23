package com.loosers.org.splitExpenses.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, name = "phone_number")
    private String phoneNumber;

    @ManyToMany(mappedBy = "users")
    private List<Group> group;
}
