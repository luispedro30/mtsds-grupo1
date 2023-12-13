package ecommerce.Controllers;

import ecommerce.Dto.ProductDto;
import ecommerce.Dto.UserDto;
import ecommerce.Models.Order;
import ecommerce.Models.Products;
import ecommerce.Models.User;
import ecommerce.Services.OrderService;
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

import java.util.ArrayList;
import java.util.Arrays;
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
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;


    UserDto userDto;
    private List<Order> orders;

    private Order order;

    @MockBean
    private OrderService orderService;

    private List<User> users = new ArrayList<User>();
    private List<ProductDto> products = new ArrayList<ProductDto>();
    private User userDto1;
    private User userDto2;

    private ProductDto productDto1;
    private ProductDto productDto2;


    @Before
    public void setUp() {
        this.userDto1 = new User(
                1,
                "Luis",
                1,
                "ADMIN"
        );
        this.userDto2 = new User(
                2,
                "João",
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

        this.users.add(this.userDto1);
        this.users.add(this.userDto2);
        this.products.add(this.productDto1);
        this.products.add(this.productDto2);
    }

    @Test
    public void getLanding() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/orders/Landing").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Application is working fine.")));

    }

    @Test
    public void  getAllOrdersButEmpty() throws Exception {
        //given
        List<Order> orders = new ArrayList<Order>();

        //when
        when(orderService.getAll()).thenReturn(orders);

        //then
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(orderService, Mockito.times(1)).getAll();
        verifyNoMoreInteractions(orderService);
    }

    @Test
    public void getAllOrders() throws Exception {
        //given
        Products product1 = new Products();
        product1.setProductId(1);
        product1.setPrice(100);


        Products product2 = new Products();
        product2.setProductId(2);
        product2.setPrice(100);



        List<Order> orders = new ArrayList<Order>();
        Order order1 = new Order();
        order1.setOrderId(1);
        order1.setUserId(this.userDto1.getUserId()); // Set the user ID
        order1.setProducts(Arrays.asList(product1, product2));
        order1.setPriceTotal(product1.getPrice() + product2.getPrice());

        Order order2 = new Order();
        order2.setOrderId(2);
        order1.setUserId(this.userDto2.getUserId());// Set the user ID
        order2.setProducts(Arrays.asList(product2));
        order2.setPriceTotal(product2.getPrice());

        orders.add(order1);
        orders.add(order2);

        //when
        when(orderService.getAll()).thenReturn(orders);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/orders")
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].priceTotal").value(200))
                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].priceTotal").value(100));

        verify(orderService, Mockito.times(1)).getAll();
        verifyNoMoreInteractions(orderService);
    }
}
