package com.novianto.antifraud.system.service;

import com.novianto.antifraud.system.entity.Role;
import com.novianto.antifraud.system.entity.user.User;
import com.novianto.antifraud.system.repository.UserRepository;
import com.novianto.antifraud.system.request.LoginRequest;
import com.novianto.antifraud.system.request.UserDTO;
import com.novianto.antifraud.system.response.UserDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("unused")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Return user tertentu dengan nama pengguna.
     *
     * @param username yang akan di return
     * @return object User dengan username yang diberikan
     */
    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    /**
     * Login user dengan memvalidasi nama user dan kata sandi.
     *
     * @param loginRequest Object login request yang berisi nama pengguna dan kata sandi
     * @return object UserDataResponse yang berisi informasi pengguna
     */
    public UserDataResponse login(LoginRequest loginRequest){
        User user = findByUsername(loginRequest.getUsername());

        // check apakah user sudah ada
        if (user == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");

        // check apakah password sesuai
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Password");

        // check apakah user terkunci
        if (!user.isAccountNonLocked()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is Locked");
        return UserDataResponse.createUserDataResponse(user);
    }

    /**
     * Create user baru dari UserDTO yang diberikan dan menyimpannya ke database.
     *
     * @param userDTO UserDTO berisi data mentah dari pengguna baru
     * @return User yang baru dibuat.
     */
    public User signup(UserDTO userDTO){
        // create pengguna sementara untuk melihat apakah nama pengguna tersedia
        User tempUser = userRepository.findByUsername(userDTO.getUsername());
        if (tempUser != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is Already Taken");

        // Set roel untuk user. User pertama ditetapkan sebagai administrator
        String role = userRepository.findAll()
                .isEmpty() ? Role.ADMINISTRATOR.stringWithRolePrefix : Role.MERCHANT.stringWithRolePrefix;
        boolean isAccountNonLocked = userRepository.findAll().isEmpty();

        // create new user dari DTO
        User user = new User(
                userDTO.getName(),
                userDTO.getUsername(),
                userDTO.getPassword(),
                role,
                isAccountNonLocked
        );
        // salt password dan encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // save user ke database
        userRepository.save(user);
        return user;
    }

    // remove user dari database
    public void deleteUser(String username){
        // create temp user untuk melihat apakah user sudah ada sebelumnya
        Optional<User> tempUser = userRepository.findUserByUsername(username);
        if (tempUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        }else {
            User userToDelete = tempUser.get();
            userRepository.delete(userToDelete);
        }
    }

    // list all user
    public List<UserDataResponse> getUserDataList(){
        List<User> users = userRepository.findAll();
        List<UserDataResponse> userDataResponses = new ArrayList<>();

        // convert user objects ke UserDataResponse object termasuk informasi sensituve
        for (User u : users){
            UserDataResponse userDataResponse = UserDataResponse.createUserDataResponse(u);
            userDataResponses.add(userDataResponse);
        }
        return userDataResponses;
    }
}
