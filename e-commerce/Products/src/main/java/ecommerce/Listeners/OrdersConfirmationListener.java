package ecommerce.Listeners;

import ecommerce.Config.MQConfig;
import ecommerce.Messages.OrderConcludedListener;
import ecommerce.Messages.ProductsConfirmationMessage;
import ecommerce.Services.ProductsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrdersConfirmationListener {

    @Autowired
    ProductsService productsService;

    @Autowired
    private RabbitTemplate template;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void listener(OrderConcludedListener message) throws Exception {
        System.out.println(
                "orderId :" + message.getOrderId() + ", " +
                        "userId: " + message.getUserId() + ", " +
                        "productId: " + message.getProductIds());

        for (int i = 0; i < message.getProductIds().size(); i++) {
            productsService.decreaseStockProduct(message.getProductIds().get(i));
        }

        ProductsConfirmationMessage productsConfirmationMessage = new ProductsConfirmationMessage();
        productsConfirmationMessage.setMessage("Decresead stock of products" + message.getProductIds());

        template.convertAndSend("products-queue", productsConfirmationMessage);
        System.out.println("Response sent: " + productsConfirmationMessage);

    }



}
