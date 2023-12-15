package ecommerce.Listeners;

import ecommerce.Messages.ProductsConcludedListener;
import ecommerce.Messages.WalletConcludedListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class WalletConfirmation {

    @RabbitListener(queues = "wallet-queue")
    public void listener(WalletConcludedListener message){
        System.out.println(message.getMessage());
    }
}
