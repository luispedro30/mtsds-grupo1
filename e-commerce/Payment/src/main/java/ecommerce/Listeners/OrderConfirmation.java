package ecommerce.Listeners;

import ecommerce.Messages.OrderConcludedListener;
import ecommerce.Messages.WalletConcludedListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmation {

    @RabbitListener(queues = "order-queue")
    public void listener(OrderConcludedListener message){
        System.out.println(message);
    }
}
