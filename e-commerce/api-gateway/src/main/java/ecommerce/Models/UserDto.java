package ecommerce.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public record UserDto( String login,
                      EUserRole role) implements Serializable {
}