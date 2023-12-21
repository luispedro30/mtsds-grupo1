package ecommerce.Controllers;

import ecommerce.Dto.OrderDto;
import ecommerce.Dto.ProductDto;
import ecommerce.Dto.ShippingDto;
import ecommerce.Dto.UserDto;
import ecommerce.Enum.Status;
import ecommerce.Models.Review;
import ecommerce.Repository.ReviewRepository;
import ecommerce.Reviews;
import ecommerce.Services.ReviewService;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ReviewsControllerTest {

    @Autowired
    private MockMvc mvc;


    private UserDto userDto;

    private OrderDto orderDto;

    private ShippingDto shippingDto;


    @MockBean
    private ReviewService reviewService;


    @MockBean
    private ReviewRepository reviewRepository;

    private List<UserDto> users = new ArrayList<UserDto>();
    private List<OrderDto> orders = new ArrayList<OrderDto>();
    private List<ProductDto> products = new ArrayList<ProductDto>();

    private List<ShippingDto> shippings = new ArrayList<ShippingDto>();

    private UserDto userDto1;
    private UserDto userDto2;

    private ProductDto productDto1;

    private ProductDto productDto2;

    private OrderDto orderDto1;
    private OrderDto orderDto2;

    private ShippingDto shippingDto1;
    private ShippingDto shippingDto2;

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

        this.productDto2 = new ProductDto(2,
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

        this.orderDto1 = new OrderDto(1, productsOfOrder1);

        this.orderDto2 = new OrderDto(2, productsOfOrder2);


        this.users.add(this.userDto1);
        this.users.add(this.userDto2);
        this.products.add(this.productDto1);
        this.products.add(this.productDto2);
        this.orders.add(this.orderDto1);
        this.orders.add(this.orderDto2);
        this.shippings.add(this.shippingDto1);
        this.shippings.add(this.shippingDto2);
    }

    @Test
    public void  getAllReviewsButEmpty() throws Exception {
        //given
        List<Review> reviews = new ArrayList<Review>();

        //when
        when(reviewService.getAllReviews()).thenReturn(reviews);

        //then
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/reviews")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(reviewService, Mockito.times(1)).getAllReviews();
        verifyNoMoreInteractions(reviewService);
    }

    @Test
    public void getAllReviews() throws Exception {
        //given
        List<Review> reviews = new ArrayList<Review>();

        Review review1 = new Review(1,
                this.userDto1.getId(),
                this.productDto1.getId(),
                this.orderDto1.getOrderId(),
                10,
                "Muito bom produto",
                java.util.Date.from(Instant.now()),
                java.util.Date.from(Instant.now()));

        Review review2 =  new Review(2,
                this.userDto2.getId(),
                this.productDto2.getId(),
                this.orderDto2.getOrderId(),
                10,
                "Muito bom produto",
                java.util.Date.from(Instant.now()),
                java.util.Date.from(Instant.now()));


        reviews.add(review1);
        reviews.add(review2);

        //when
        when(reviewService.getAllReviews()).thenReturn(reviews);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/reviews")
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[1].orderId").value(2));

        verify(reviewService, Mockito.times(1)).getAllReviews();
        verifyNoMoreInteractions(reviewService);
    }

    @Test
    public void addReviewSuccess() throws Exception {
        Review review1 = new Review(1,
                this.userDto1.getId(),
                this.productDto1.getId(),
                this.orderDto1.getOrderId(),
                10,
                "Muito bom produto",
                java.util.Date.from(Instant.now()),
                java.util.Date.from(Instant.now()));

        // Stubbing the service method to return the mocked Shipping object
        when(reviewService.addReview(review1, request)).thenReturn(review1);

        // Performing the test by calling the controller method
        Review response = reviewService.addReview(review1, request);

        // Assertions for a successful scenario
        //then
        verify(reviewService, Mockito.times(1)).
                addReview(review1, request);
        verifyNoMoreInteractions(reviewService);
    }
}
