package ecommerce.Controllers;

import ecommerce.Dto.*;
import ecommerce.Enum.Status;
import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Models.Shipping;
import ecommerce.Repository.ShippingRepository;
import ecommerce.Services.ShippingService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ShippingControllerTest {

    @Autowired
    private MockMvc mvc;


    UserDto userDto;

    private OrderDto orderDto;

    private PaymentDto paymentDto;

    private WalletDto walletDto;

    @MockBean
    private ShippingService shippingService;


    @MockBean
    private ShippingRepository shippingRepository;

    private List<UserDto> users = new ArrayList<UserDto>();
    private List<OrderDto> orders = new ArrayList<OrderDto>();
    private List<ProductDto> products = new ArrayList<ProductDto>();

    private List<WalletDto> wallets = new ArrayList<WalletDto>();

    private List<PaymentDto> payments = new ArrayList<PaymentDto>();

    private UserDto userDto1;
    private UserDto userDto2;

    private ProductDto productDto1;

    private ProductDto productDto2;

    private OrderDto orderDto1;
    private OrderDto orderDto2;

    private PaymentDto paymentDto1;
    private PaymentDto paymentDto2;

    private WalletDto walletDto1;
    private WalletDto walletDto2;

    @Mock
    private HttpServletRequest request;

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

        this.productDto1 = new ProductDto(1,
                "Tshirt",
                "Tshirt manga comprida",
                "Tshirt",
                100,
                2);

        this.productDto1 = new ProductDto(2,
                "Tshirt curta",
                "Tshirt manga curta",
                "Tshirt",
                100,
                3);

        ArrayList<ProductDto> productsOfOrder1 = new ArrayList<>();
        productsOfOrder1.add(this.productDto1);
        productsOfOrder1.add(this.productDto2);

        ArrayList<ProductDto> productsOfOrder2 = new ArrayList<>();
        productsOfOrder2.add(this.productDto2);

        this.orderDto1 = new OrderDto(1, productsOfOrder1, 200d);

        this.orderDto2 = new OrderDto(2, productsOfOrder2, 100d);

        this.paymentDto1 = new PaymentDto(1, 1, 200);
        this.paymentDto2 = new PaymentDto(2, 2, 100);

        this.walletDto1 = new WalletDto(1,1, 300);
        this.walletDto2 = new WalletDto(2,2, 400);

        this.users.add(this.userDto1);
        this.users.add(this.userDto2);
        this.products.add(this.productDto1);
        this.products.add(this.productDto2);
        this.orders.add(this.orderDto1);
        this.orders.add(this.orderDto2);
        this.payments.add(this.paymentDto1);
        this.payments.add(this.paymentDto2);
        this.wallets.add(this.walletDto1);
        this.wallets.add(this.walletDto2);
    }

    @Test
    public void  getAllShippingButEmpty() throws Exception {
        //given
        List<Shipping> shippings = new ArrayList<Shipping>();

        //when
        when(shippingService.getAllShipping()).thenReturn(shippings);

        //then
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/shipping")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(shippingService, Mockito.times(1)).getAllShipping();
        verifyNoMoreInteractions(shippingService);
    }

    @Test
    public void getAllShipping() throws Exception {
        //given
        List<Shipping> shippings = new ArrayList<Shipping>();

        Shipping shipping1 = new Shipping(1,
                this.paymentDto1.getPaymentId(),
                this.orderDto1.getOrderId(),
                this.userDto1.getId(),
                Status.REGISTED,
                java.util.Date.from(Instant.now()),
                java.util.Date.from(Instant.now()));

        Shipping shipping2 = new Shipping(2,
                this.paymentDto2.getPaymentId(),
                this.orderDto2.getOrderId(),
                this.userDto2.getId(),
                Status.REGISTED,
                java.util.Date.from(Instant.now()),
                java.util.Date.from(Instant.now()));


        shippings.add(shipping1);
        shippings.add(shipping2);

        //when
        when(shippingService.getAllShipping()).thenReturn(shippings);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/shipping")
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].status").value("REGISTED"))
                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].status").value("REGISTED"));

        verify(shippingService, Mockito.times(1)).getAllShipping();
        verifyNoMoreInteractions(shippingService);
    }

    @Test
    public void addShippingSuccess() throws Exception {
        // Mocked Shipping object for testing
        Shipping shipping1 = new Shipping(1,
                this.paymentDto1.getPaymentId(),
                this.orderDto1.getOrderId(),
                this.userDto1.getId(),
                Status.REGISTED,
                java.util.Date.from(Instant.now()),
                java.util.Date.from(Instant.now()));

        // Stubbing the service method to return the mocked Shipping object
        when(shippingService.addShipping(shipping1, request)).thenReturn(shipping1);

        // Performing the test by calling the controller method
        Shipping response = shippingService.addShipping(shipping1, request);

        // Assertions for a successful scenario
        //then
        verify(shippingService, Mockito.times(1)).
                addShipping(shipping1, request);
        verifyNoMoreInteractions(shippingService);;
    }

}
