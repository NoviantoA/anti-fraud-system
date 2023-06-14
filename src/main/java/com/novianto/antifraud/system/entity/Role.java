package com.novianto.antifraud.system.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Role {
    ADMINISTRATOR("ROLE_ADMINISTRATOR"),
    SUPPORT("ROLE_SUPPORT"),
    MERCHANT("ROLE_MERCHANT");

    public final String stringWithRolePrefix;

    Role(String stringWithRolePrefix) {
        this.stringWithRolePrefix = stringWithRolePrefix;
    }

    public static List<String> getRoleAsString(){
        return Arrays.stream(Role.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
