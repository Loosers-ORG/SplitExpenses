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
@Table(name = "Groups")
public class Group {

    @Id
    private String groupId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "created_by")
    private String createdBy;

    @ManyToMany
    @JoinTable(
            name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "email")
    )
    private List<User> users;

    @Transient
    private  List<String> userIds;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;
}
