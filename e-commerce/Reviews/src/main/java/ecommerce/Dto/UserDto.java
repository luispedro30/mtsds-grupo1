package ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor

@Getter
@Setter
public class UserDto {
    private Integer id;
    private String name;
    private String login;
    private String email;
    private String password;
    private int active;
    private String role;
}