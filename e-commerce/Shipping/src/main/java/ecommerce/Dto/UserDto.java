package ecommerce.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String name;
    private String login;
    private String email;
    private String password;
    private int active;
    private String role;
}