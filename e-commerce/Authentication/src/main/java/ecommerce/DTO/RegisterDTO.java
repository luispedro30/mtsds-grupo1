package ecommerce.DTO;

import ecommerce.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public record RegisterDTO(
        String login,
        String name,
        String email,

        String password,
        Role role) {
}