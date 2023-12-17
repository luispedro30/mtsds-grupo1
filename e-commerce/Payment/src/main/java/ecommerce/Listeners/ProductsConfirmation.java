package ecommerce.Listeners;

import ecommerce.Messages.OrderConcludedListener;
import ecommerce.Messages.ProductsConcludedListener;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductsConfirmation {

    @RabbitListener(queues = "products-queue")
    public void listener(ProductsConcludedListener message){
        System.out.println(message);
    }
}
