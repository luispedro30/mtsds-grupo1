package ecommerce.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Dto.UserDto;
import ecommerce.Models.Wallet;
import ecommerce.Repository.WalletRepository;
import ecommerce.Services.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mvc;


    UserDto userDto;
    private List<Wallet> wallets;

    private Wallet product;

    @MockBean
    private WalletService walletService;

    @MockBean
    private WalletRepository walletRepository;

    private List<UserDto> users = new ArrayList<UserDto>();;
    private UserDto userDto1;
    private UserDto userDto2;
    @Before
    public void setUp() {
        this.userDto1 = new UserDto(
                1,
                "Luis",
                "luis",
                "luis.pedro_1998@hotmail.com",
                "password",
                1,
                "ADMIN"
        );
        this.userDto2 = new UserDto(
                2,
                "João",
                "joao",
                "luis.pedro_1998@hotmail.com",
                "password",
                1,
                "ADMIN"
        );

        this.users.add(this.userDto1);
        this.users.add(this.userDto2);
    }

    @Test
    public void getLanding() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/wallet/Landing").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Application is working fine.")));

    }

    @Test
    public void  getAllWalletsButEmpty() throws Exception {
        //given
        List<Wallet> wallets = new ArrayList<Wallet>();

        //when
        when(walletService.getAllWallets()).thenReturn(wallets);

        //then
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/wallet")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(walletService, Mockito.times(1)).getAllWallets();
        verifyNoMoreInteractions(walletService);
    }

    @Test
    public void getAllWallets() throws Exception {
        //given
        List<Wallet> wallets = new ArrayList<Wallet>();
        Wallet wallet1 = new Wallet(1,
                1,
                100);
        Wallet wallet2 = new Wallet(2,
                2,
                100);
        wallets.add(wallet1);
        wallets.add(wallet2);
        //when
        when(walletService.getAllWallets()).thenReturn(wallets);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/wallet")
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].value").value(100))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].value").value(100));

        verify(walletService, Mockito.times(1)).getAllWallets();
        verifyNoMoreInteractions(walletService);
    }

    @Test
    public void createWallet() throws Exception
    {

        Wallet wallet1 = new Wallet(1,
                1,
                100);

        //when
        when(walletRepository.save(wallet1)).thenReturn(wallet1);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/wallet")
                        .content(asJsonString(wallet1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
    }

    /*
    @Test
    public void createWalletWithAlreadyExistingUser() throws Exception
    {


        Wallet wallet1 = new Wallet(1,
                1,
                100);

        Wallet wallet2 = new Wallet(2,
                1,
                200);

        //when
        when(walletService.addWallet(wallet1)).thenReturn(wallet1);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/wallet")
                        .content(asJsonString(wallet1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(MockMvcRequestBuilders
                        .post("/wallet")
                        .content(asJsonString(wallet2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    */
    @Test
    public void addMoneyWallet() throws Exception
    {


        Wallet wallet1 = new Wallet(1,
                1,
                100);



        //when
        when(walletRepository.save(wallet1)).thenReturn(wallet1);
        wallet1.setValue(wallet1.getValue() + 50);
        when(walletRepository.save(wallet1)).thenReturn(wallet1);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/wallet")
                        .content(asJsonString(wallet1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(MockMvcRequestBuilders
                        .post("/wallet/addMoney/1?money=50")
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
}
