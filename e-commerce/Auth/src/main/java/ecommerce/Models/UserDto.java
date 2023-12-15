package ecommerce.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private String title;
    private String full_name;
    private String country_of_residence;
    private String username;
    private String password;
    private String role;
    private String email;
}
