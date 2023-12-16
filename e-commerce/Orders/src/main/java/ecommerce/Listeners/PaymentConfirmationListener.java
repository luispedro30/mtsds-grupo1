package ecommerce.Listeners;

import ecommerce.Config.MQConfig;
import ecommerce.Messages.PaymentConfirmationMessage;
import ecommerce.Messages.OrderConcludedListener;
import ecommerce.Models.Order;
import ecommerce.Models.Products;
import ecommerce.Repository.ProductsRepository;
import ecommerce.Services.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentConfirmationListener {

    @Autowired
    OrderService orderService;

    @Autowired
    private RabbitTemplate template;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void listener(PaymentConfirmationMessage message) throws Exception {
        System.out.println(
                "paymentId :" + message.getPaymentId() + ", " +
                        "orderId :" + message.getOrderId() + ", " +
                        "userId: " + message.getUserId() + ", " +
                        "priceTotal: " + message.getPriceTotal() + ", " +
                        "messageDate" + message.getCreatedAt());

        Order order = orderService.getOrderWithProducts(message.getOrderId());


        OrderConcludedListener orderConcludedListener = new OrderConcludedListener();
        orderConcludedListener.setOrderId(message.getOrderId() );
        orderConcludedListener.setProductIds(order.getProducts().stream()
                .map(Products::getProductId)
                .collect(Collectors.toList()));
        orderConcludedListener.setUserId(message.getUserId());

        template.convertAndSend("order-queue", orderConcludedListener);
        System.out.println("Response sent: " + orderConcludedListener);

    }



}
