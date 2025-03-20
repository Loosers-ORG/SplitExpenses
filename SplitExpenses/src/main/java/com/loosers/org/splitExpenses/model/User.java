package com.loosers.org.splitExpenses.model;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
public class User {
    @NonNull
    private String name;
    @NonNull
    private String email;
    private String phoneNumber;
}
