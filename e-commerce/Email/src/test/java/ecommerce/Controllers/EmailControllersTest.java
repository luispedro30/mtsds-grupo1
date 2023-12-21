package ecommerce.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Controllers.EmailController;
import ecommerce.Enums.StatusEmail;
import ecommerce.Model.EmailModel;
import ecommerce.Service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class EmailControllersTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    public void createEmailSuccess() throws Exception
    {

        EmailModel emailModel = new EmailModel();
        emailModel.setEmailFrom("from@example.com");
        emailModel.setEmailTo("to@example.com");
        emailModel.setSubject("Test Subject");
        emailModel.setText("Test Email Body");
        emailModel.setOwnerRef("oi");
        emailModel.setStatusEmail(StatusEmail.SENT);



        //when
        when(emailService.sendEmail(emailModel)).thenReturn(emailModel);

        //then
        mvc.perform(MockMvcRequestBuilders
                        .post("/sending-email")
                        .content(asJsonString(emailModel))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void createEmailFailure() throws Exception {
        EmailModel emailModel = new EmailModel();
        // Set up emailModel for a failure scenario

        // Mocking the service method to throw an exception
        when(emailService.sendEmail(any(EmailModel.class))).thenReturn(null);

        mvc.perform(post("/sending-email")
                        .content(asJsonString(emailModel))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Assuming a failure returns 500 status

        verify(emailService, times(0)).sendEmail(emailModel);
    }

    // Utility method to convert object to JSON string
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
