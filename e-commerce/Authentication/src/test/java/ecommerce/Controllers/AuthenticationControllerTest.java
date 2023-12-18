package ecommerce.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.DTO.AuthenticationDTO;
import ecommerce.DTO.RegisterDTO;
import ecommerce.Enums.Role;
import ecommerce.Models.User;
import ecommerce.Repository.UserRepository;
import ecommerce.Service.AuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    private User user;
    private List<User> users;


    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private AuthenticationController authenticationController;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void createUser() throws Exception
    {

        RegisterDTO user1 = new RegisterDTO("luis123",
                "Luis",
                "luis.pedro_1998@hotmail.com",
                "password",
                Role.ADMIN);

        //when
        String encryptedPassword = new BCryptPasswordEncoder().encode(user1.password());

        long currentTimeMillis = System.currentTimeMillis();
        int uniqueInteger = (int) (currentTimeMillis % Integer.MAX_VALUE);

        User newUser = new User(uniqueInteger,
                user1.name(),
                user1.login(),
                user1.email(),
                encryptedPassword,
                user1.role());

        when(authenticationController.register(user1)).thenReturn(ResponseEntity.ok().build());

        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .content(asJsonString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createUserNull() throws Exception
    {

        User user1 = null;

        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .content(asJsonString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginUserValid() throws Exception
    {

        RegisterDTO user1 = new RegisterDTO("luis123",
                "Luis",
                "luis.pedro_1998@hotmail.com",
                "password",
                Role.ADMIN);

        //when
        String encryptedPassword = new BCryptPasswordEncoder().encode(user1.password());

        long currentTimeMillis = System.currentTimeMillis();
        int uniqueInteger = (int) (currentTimeMillis % Integer.MAX_VALUE);

        User newUser = new User(uniqueInteger,
                user1.name(),
                user1.login(),
                user1.email(),
                encryptedPassword,
                user1.role());

        when(authenticationController.register(user1)).thenReturn(ResponseEntity.ok().build());

        AuthenticationDTO authenticationDTO = new AuthenticationDTO(
                user1.login(),
                user1.password()
        );
        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .content(asJsonString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders
                        .post("/auth/login")
                        .content(asJsonString(authenticationDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
