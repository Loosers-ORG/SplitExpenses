package com.loosers.org.splitExpenses.utils;

import org.springframework.stereotype.Component;

@Component
public class IdGenerator {
    public String generteId(String ... ids) {
        return String.join("_", ids);
    }
}
