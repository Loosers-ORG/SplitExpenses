package com.loosers.org.splitExpenses.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "email")
    )
    private List<User> users;

    @Transient
    private  List<String> userIds;

    @OneToMany(mappedBy = "group", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserOutstandingBalances> userOutstandingBalances;
}
