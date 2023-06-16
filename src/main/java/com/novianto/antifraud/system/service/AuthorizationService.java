package com.novianto.antifraud.system.service;

import com.novianto.antifraud.system.entity.Role;
import com.novianto.antifraud.system.entity.user.User;
import com.novianto.antifraud.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@SuppressWarnings("unused")
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    // update user role sesuai dengan role yang diberikan
    public User updateRole(String username, String role){
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!Role.getRoleAsString().contains(role) || role.equals("ADMINISTRATOR")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (user.getRoleWithoutPrefix().equals(role)){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // assign role
        user.setRole("ROLE_" + role);
        // save update user ke database
        userRepository.save(user);
        return user;
    }

    // update user access level sesuai dengan akses yang diberikan
    public Map<String, String> updateAccess(String username, String access){
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // tidak dapat update ke level admin
        if (user.getRole().equals("ADMINISTRATOR")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (access.equals("LOCK")){
            user.setAccountNonLocked(false);
            userRepository.save(user);
            return Map.of("status", "User" + user.getUsername() + " locked!");
        } else if (access.equals("UNLOCK")) {
            user.setAccountNonLocked(true);
            userRepository.save(user);
            return Map.of("status", "User" + user.getUsername() + " unlocked!");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
