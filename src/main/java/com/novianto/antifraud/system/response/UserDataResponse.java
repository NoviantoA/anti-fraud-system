package com.novianto.antifraud.system.response;

import com.novianto.antifraud.system.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDataResponse {

    private Long id;
    private final String name;
    private final String username;
    private final String role;

    // return user data tanpa sensitive informasi
    public static UserDataResponse createUserDataResponse(User user){
        return new UserDataResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getRoleWithoutPrefix()
        );
    }
}
