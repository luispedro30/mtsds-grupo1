package ecommerce.Controllers;

import ecommerce.Enums.Category;
import ecommerce.Models.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.core.IsIterableContaining.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LandingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getLanding() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/Landing").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Application is working fine.")));

    }

    @Test
    public void getProduct() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/catalog/products/P002").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P002"));

    }

    @Test
    public void getProductsCategory() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/catalog/products-by-category/COMPUTERS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].price").isNumber())
                .andExpect(jsonPath("$[0].code").isString())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].dummy").doesNotExist());

    }

    @Test
    public void getProducts() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        Product[] products;

        Product product1 = new Product("P001",
                "Printer 001",
                Category.PRINTERS,
                942.4f);

        Product product2 = new Product("C001",
                "Computer 001",
                Category.COMPUTERS,
                925.4f);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .get("/catalog/products")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        products = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<Product[]>() {
        });

        assertThat(products, arrayWithSize(4));
        assertThat(Arrays.stream(products).toList(), hasItem(product1));
        assertThat(products[0], is(product2));
        assertThat(products[0].getCode(), is("C001"));

    }


}
