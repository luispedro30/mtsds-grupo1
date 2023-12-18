package ecommerce.Controllers;

import ecommerce.Dto.UserDto;
import ecommerce.Enums.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Models.Product;
import ecommerce.Repository.ProductsRepository;
import ecommerce.Services.ProductsService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
public class ProductsControllerTest {

    @Autowired
    private MockMvc mvc;


    UserDto userDto;
    private List<Product> products;
    private Product product;

    @Mock
    private ProductsRepository productRepository;

    @MockBean
    private ProductsService productsService;

    @MockBean
    private ProductsRepository productsRepository;
    @Before
    public void setUp() {

        this.userDto = new UserDto(
                1,
                "Luis",
                "luis",
                1,
                "ADMIN"
        );
    }

    @Test
    public void getLanding() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/Landing").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Application is working fine.")));

    }

    @Test
    public void  getAllProductsButEmpty() throws Exception {
        //given
        List<Product> products = new ArrayList<Product>();

        //when
        when(productsService.getAllProduct()).thenReturn(products);

        //then
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                        .get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(productsService, Mockito.times(1)).getAllProduct();
        verifyNoMoreInteractions(productsService);
    }

    @Test
    public void  getAllProducts() throws Exception {

        //given
        List<Product> products = new ArrayList<Product>();
        Product product1 = new Product(1,
                "P001",
                "Printer 001",
                Category.PRINTERS,
                942.4f,
                5);
        products.add(product1);


        //when
        when(productsService.getAllProduct()).thenReturn(products);

        mvc.perform(MockMvcRequestBuilders
                        .get("/products")
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("P001"));

        verify(productsService, Mockito.times(1)).getAllProduct();
        verifyNoMoreInteractions(productsService);
    }

    @Test
    public void createProduct() throws Exception
    {

        Product product1 = new Product(3,
                "P003",
                "Printer 003",
                Category.PRINTERS,
                942.4f,
                5);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", String.valueOf(this.userDto.getId()));

        //when
        HttpServletRequest HttpServletRequest = null;
        when(productsRepository.save(product)).thenReturn(product);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/products?id=1")
                        .content(asJsonString(product1))
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
    public void getProduct1() throws Exception {
        //given
        Product product1 = new Product(1,
                "P001",
                "Printer 001",
                Category.PRINTERS,
                942.4f,
                5);

        //when
        when(productsService.getProductById(1)).thenReturn(product1);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .get("/products/{id}",1)
                        .accept((MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("P001"));

        verify(productsService, times(1)).getProductById(1);
        verifyNoMoreInteractions(productsService);
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

}
