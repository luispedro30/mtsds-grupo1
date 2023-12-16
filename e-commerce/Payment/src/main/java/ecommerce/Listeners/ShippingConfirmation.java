package ecommerce.Listeners;

import ecommerce.Messages.WalletConcludedListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ShippingConfirmation {

    @RabbitListener(queues = "shipping-queue")
    public void listener(WalletConcludedListener message){
        System.out.println(message.getMessage());
    }
}
