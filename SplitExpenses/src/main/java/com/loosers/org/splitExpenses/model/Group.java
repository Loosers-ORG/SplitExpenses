package com.loosers.org.splitExpenses.model;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Group {
    @NonNull
    private String groupId;
    @NonNull
    private String name;
    private String description;
    private String createdBy;
    private List<String> users;
    private List<String> expenses;

}
