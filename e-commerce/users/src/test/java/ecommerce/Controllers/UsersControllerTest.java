package ecommerce.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Enums.Role;
import ecommerce.Models.User;
import ecommerce.Repository.UserRepository;
import ecommerce.Service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

    @Autowired
    private MockMvc mvc;

    private User user;
    private List<User> users;


    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void getLanding() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/Landing").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Application is working fine.")));
    }

    @Test
    public void createUser() throws Exception
    {

        User user1 = new User(1,
                "Luis",
                "luis123",
                "luis.pedro_1998@hotmail.com",
                "password",
                1,
                Role.ADMIN);

        //when
        when(userService.saveUser(user1)).thenReturn(user1);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .content(asJsonString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
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
                        .post("/users")
                        .content(asJsonString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void  getAllUsers() throws Exception {

        //given
        List<User> users = new ArrayList<User>();
        User user1 = new User(1,
                "Luis",
                "luis123",
                "luis.pedro_1998@hotmail.com",
                "password",
                1,
                Role.ADMIN);

        User user2 = new User(2,
                "João",
                "joao123",
                "luis.pedro_1998@hotmail.com",
                "password2",
                1,
                Role.USER);
        users.add(user1);
        users.add(user2);


        //when
        when(userService.getAllUsers()).thenReturn(users);

        mvc.perform(MockMvcRequestBuilders
                        .get("/users")
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Luis"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("João"));

        verify(userService, Mockito.times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void getUser1() throws Exception {
        //given
        User user1 = new User(1,
                "Luis",
                "luis123",
                "luis.pedro_1998@hotmail.com",
                "password",
                1,
                Role.ADMIN);

        //when
        when(userService.getUserById(1)).thenReturn(user1);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/{id}",1)
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Luis"));

        verify(userService, times(1)).getUserById(1);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void getUnexistingUser() throws Exception {
        //given
        User user1 = new User(2,
                "Luis",
                "luis123",
                "luis.pedro_1998@hotmail.com",
                "password",
                1,
                Role.ADMIN);

        //when
        when(userService.getUserById(1)).thenReturn(null);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/users/{id}",1)
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(1);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void getUserByName1() throws Exception {
        //given
        List<User> users = new ArrayList<User>();
        User user1 = new User(1,
                "Luis",
                "luis123",
                "luis.pedro_1998@hotmail.com",
                "password",
                1,
                Role.ADMIN);

        User user2= new User(2,
                "Luis",
                "luis1234",
                "luis.pedro_1998@hotmail.com",
                "password",
                1,
                Role.ADMIN);

        users.add(user1);
        users.add(user2);
        //when
        when(userService.getAllUserByName("Luis")).thenReturn(users);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/users?name=Luis")
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Luis"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Luis"));

        verify(userService, times(1)).getAllUserByName("Luis");
        verifyNoMoreInteractions(userService);
    }


}
