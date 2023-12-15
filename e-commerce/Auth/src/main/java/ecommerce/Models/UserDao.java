package ecommerce.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDao {
    private String title;
    private String country_of_residence;
    private String full_name;
    private String username;

    private String password;

    private String role;

    private String email;

    public UserDao(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
