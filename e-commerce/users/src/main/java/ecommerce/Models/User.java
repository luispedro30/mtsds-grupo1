package ecommerce.Models;

import ecommerce.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    private Integer id;
    private String name;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String email;
    private String password;
    private int active;
    private Role role;


    public User(String name, String encryptedPassword, int active, Role role) {
        this.name = name;
        this.password = encryptedPassword;
        this.active = active;
        this.role = role;
    }
}
