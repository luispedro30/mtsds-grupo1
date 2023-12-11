package ecommerce.Models;

import ecommerce.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
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
