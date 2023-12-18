package ecommerce.Controllers;

import ecommerce.Enums.StatusEmail;
import ecommerce.Model.EmailModel;
import ecommerce.Repository.EmailRepository;
import ecommerce.Service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class EmailControllersTest {
    @Mock
    private EmailRepository emailRepository;

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void testSendEmail_Success() {
        EmailModel emailModel = new EmailModel();
        emailModel.setEmailFrom("from@example.com");
        emailModel.setEmailTo("to@example.com");
        emailModel.setSubject("Test Subject");
        emailModel.setText("Test Email Body");

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom(emailModel.getEmailFrom());
        expectedMessage.setTo(emailModel.getEmailTo());
        expectedMessage.setSubject(emailModel.getSubject());
        expectedMessage.setText(emailModel.getText());

        // Mock behavior for email sending
        doNothing().when(emailSender).send(expectedMessage);

        // Mock behavior for saving to repository
        when(emailRepository.save(any(EmailModel.class))).thenReturn(emailModel);

        EmailModel result = emailService.sendEmail(emailModel);

        // Verifying the interaction
        verify(emailSender).send(expectedMessage);
        verify(emailRepository).save(any(EmailModel.class));

        assertEquals(StatusEmail.SENT, result.getStatusEmail());
        // ... perform more assertions if needed
    }

    // Add more test cases for other scenarios if required
}
