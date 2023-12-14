package ecommerce.Models;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;


public record UserDto(Integer id,
                      @NotNull
                      String name,
                      @NotNull
                      String login,
                      @NotNull
                      String email,
                      EUserRole role) implements Serializable {
}