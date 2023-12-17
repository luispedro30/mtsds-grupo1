package ecommerce.Listeners;

import ecommerce.Messages.Order2Email;
import ecommerce.Messages.Wallet2Email;
import ecommerce.Model.EmailModel;
import ecommerce.Service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationListener {

    @Autowired
    EmailService emailService;

    @Autowired
    private RabbitTemplate template;

    @RabbitListener(queues = "order-2-email-queue")
    public void listener(Order2Email message) throws Exception {
        System.out.println(
                "From :" + message.getEmailFrom() + ", " +
                        "To :" + message.getEmailTo() + ", " +
                        "subject: " + message.getSubject() + ", " +
                        "text: " + message.getText());

        System.out.println("Received message from wallet");

        EmailModel emailModel = new EmailModel(message.getOwnerRef(),
                message.getEmailFrom(),
                message.getEmailTo(),
                message.getSubject(),
                message.getText());

        emailService.sendEmail(emailModel);

    }

}
