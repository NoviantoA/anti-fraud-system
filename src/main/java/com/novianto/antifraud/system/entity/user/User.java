package com.novianto.antifraud.system.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @NotEmpty
    private String name;
    @Column
    @NotEmpty
    private String username;
    @Column
    @NotEmpty
    private String password;
    @Column
    @NotEmpty
    @JsonIgnore
    private String role;
    @Column
    @JsonIgnore
    private boolean isAccountNonLocked;

    public User(String name, String username, String password, String role, boolean isAccountNonLocked) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isAccountNonLocked = isAccountNonLocked;
    }

    // return plain user karena pada web security membutuhkan prefix ketika method lain tidak return role tanpa "ROLE_" prefix.
    public String getRoleWithoutPrefix() {
        return role.substring(5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && isAccountNonLocked == user.isAccountNonLocked
                && Objects.equals(name, user.name)
                && Objects.equals(username, user.username)
                && Objects.equals(password, user.password)
                && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, username, password, role, isAccountNonLocked);
    }
}
