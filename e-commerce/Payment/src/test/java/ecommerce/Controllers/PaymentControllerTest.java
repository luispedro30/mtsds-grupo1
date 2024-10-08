package ecommerce.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Dto.OrderDto;
import ecommerce.Dto.ProductDto;
import ecommerce.Dto.UserDto;
import ecommerce.Models.Payment;
import ecommerce.Repository.PaymentRepository;
import ecommerce.Services.PaymentService;
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

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mvc;


    UserDto userDto;

    private List<Payment> payments;

    private OrderDto orderDto;

    private Payment payment;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private PaymentRepository paymentRepository;

    private List<UserDto> users = new ArrayList<UserDto>();
    private List<ProductDto> products = new ArrayList<ProductDto>();
    private List<OrderDto> orders = new ArrayList<OrderDto>();
    private UserDto userDto1;
    private UserDto userDto2;

    private ProductDto productDto1;

    private ProductDto productDto2;

    private OrderDto orderDto1;
    private OrderDto orderDto2;


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

        this.users.add(this.userDto1);
        this.users.add(this.userDto2);
        this.products.add(this.productDto1);
        this.products.add(this.productDto2);
        this.orders.add(this.orderDto1);
        this.orders.add(this.orderDto2);
    }

    @Test
    public void getLanding() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/payment/Landing").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Application is working fine.")));

    }

    @Test
    public void  getAllPaymentButEmpty() throws Exception {
        //given
        List<Payment> payments = new ArrayList<Payment>();

        //when
        when(paymentService.getAllPayments()).thenReturn(payments);

        //then
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/payment")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(paymentService, Mockito.times(1)).getAllPayments();
        verifyNoMoreInteractions(paymentService);
    }

    @Test
    public void getAllPayments() throws Exception {
        //given
        List<Payment> payments = new ArrayList<Payment>();

        Payment payment1 = new Payment(1,
                1,
                2,
                200,
                Date.from(Instant.now()),
                Date.from(Instant.now()));

        Payment payment2 = new Payment(2,
                2,
                2,
                100,
                Date.from(Instant.now()),
                Date.from(Instant.now()));

        payments.add(payment1);
        payments.add(payment2);

        //when
        when(paymentService.getAllPayments()).thenReturn(payments);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/payment")
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].userId").value(2))
                .andExpect(jsonPath("$[0].priceTotal").value(200))
                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].priceTotal").value(100));

        verify(paymentService, Mockito.times(1)).getAllPayments();
        verifyNoMoreInteractions(paymentService);
    }

    private String extractToken(HttpServletRequest request) {
        // Your logic to extract the token from the incoming request headers
        // For example:
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extract the token without "Bearer " prefix
        }
        return null; // Handle token not found scenario
    }

    /*
    @Test
    public void createPayment() throws Exception
    {
        Payment payment1 = new Payment(1, 1, 2, 200, Date.from(Instant.now()), Date.from(Instant.now()));

        // Setup the mock behavior
        when(paymentRepository.save(payment1)).thenReturn(payment1);

        verify(paymentRepository, times(1)).save(payment1);
        // Optionally, verify no further interactions with the mock
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    public void createPaymentWithAlreadyExistingPayment() throws Exception
    {

        Payment payment1 = new Payment(1,
                1,
                2,
                200,
                Date.from(Instant.now()),
                Date.from(Instant.now()));

        //when
        when(paymentRepository.save(payment1)).thenReturn(payment1);

        //then
        verify(paymentRepository, Mockito.times(1)).save(payment1);
        verifyNoMoreInteractions(paymentRepository);;


        // Perform a request to add the new Payment
        Payment payment2 = new Payment(2,
                1,
                2,
                200,
                Date.from(Instant.now()),
                Date.from(Instant.now()));


        // Verify that the save method was not called (to ensure the new wallet was not added)
        verify(paymentRepository, never()).save(payment2);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    */
}
