package com.novianto.antifraud.system.controller;

import com.novianto.antifraud.system.entity.user.User;
import com.novianto.antifraud.system.logger.Logger;
import com.novianto.antifraud.system.request.LoginRequest;
import com.novianto.antifraud.system.request.UserDTO;
import com.novianto.antifraud.system.response.UserDataResponse;
import com.novianto.antifraud.system.service.AuthorizationService;
import com.novianto.antifraud.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@SuppressWarnings("unused")
public class AuthorizationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorizationService authorizationService;

    // endpoint login untuk user yang sudah terdaftar
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        String endpoint = "api/auth/login";
        Logger.logRequest(HttpMethod.POST, endpoint, loginRequest);

        UserDataResponse userDataResponse = userService.login(loginRequest);
        Logger.logResponse(HttpMethod.POST, endpoint, userDataResponse);

        return new ResponseEntity<>(userDataResponse, HttpStatus.OK);
    }

    // signup new user
    @PostMapping("/user")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDTO userDTO){
        String endpoint = "/api/auth/user";
        Logger.logRequest(HttpMethod.POST, endpoint, userDTO);

        User user = userService.signup(userDTO);
        UserDataResponse userDataResponse = UserDataResponse.createUserDataResponse(user);

        Logger.logResponse(HttpMethod.POST, endpoint, userDataResponse);
        return new ResponseEntity<>(userDataResponse, HttpStatus.CREATED);
    }

    // get list all user
    @GetMapping("/list")
    public ResponseEntity<?> getUserList(){
        String endpoint = "/api/auth/list";
        Logger.logRequest(HttpMethod.GET, endpoint);

        List<UserDataResponse> userDataResponses = userService.getUserDataList();

        Logger.logResponse(HttpMethod.GET, endpoint, userDataResponses);
        return new ResponseEntity<>(userDataResponses, HttpStatus.OK);
    }

    // delete user
    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        String endpoint = "/api/auth/user/" + username;
        Logger.logRequest(HttpMethod.DELETE, endpoint);

        userService.deleteUser(username);
        Map<String, String> response = Map.of(
                "username", username,
                "status", "Deleted successfully");

        Logger.logResponse(HttpMethod.DELETE, endpoint, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ADMIN DAPAT update role dari user pada endpoint
    @PutMapping("/role")
    public ResponseEntity<?> updateRole(@RequestBody Map<String, String>usernameWithRole){
        String endpoint = "api/auth/role";
        Logger.logRequest(HttpMethod.PUT, endpoint, usernameWithRole);

        String username = usernameWithRole.get("username");
        String role = usernameWithRole.get("role");
        User user = authorizationService.updateRole(username, role);
        UserDataResponse userDataResponse = UserDataResponse.createUserDataResponse(user);

        Logger.logResponse(HttpMethod.PUT, endpoint, userDataResponse);
        return new ResponseEntity<>(userDataResponse, HttpStatus.OK);
    }

    // admin dapat mengakses access level
    @PutMapping("/access")
    public ResponseEntity<?> updateAccess(@RequestBody Map<String, String> usernameWithAccess){
        String endpoint = "/api/auth/access";
        Logger.logRequest(HttpMethod.PUT, endpoint, usernameWithAccess);

        String username = usernameWithAccess.get("username");
        String access = usernameWithAccess.get("access");

        Map<String, String> response = authorizationService.updateAccess(username, access);

        Logger.logResponse(HttpMethod.PUT, endpoint, response);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
