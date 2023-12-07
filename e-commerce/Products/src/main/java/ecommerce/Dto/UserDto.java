package ecommerce.Dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private Integer id;
    private String name;
    private String password;
    private int active;
    private String role;
}
