package ecommerce.DTO;

import ecommerce.Enums.Role;

public record RegisterDTO(String login, String password, Role role) {
}