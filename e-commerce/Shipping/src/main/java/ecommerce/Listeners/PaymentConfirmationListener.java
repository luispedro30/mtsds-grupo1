package ecommerce.Listeners;

import ecommerce.Config.MQConfig;
import ecommerce.Enum.Status;
import ecommerce.Messages.PaymentConfirmationMessage;
import ecommerce.Messages.ShippingConcludedListener;
import ecommerce.Models.Shipping;
import ecommerce.Services.ShippingService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfirmationListener {

    @Autowired
    ShippingService shippingService;

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

        Shipping newShipping = new Shipping();
        newShipping.setPaymentId(message.getPaymentId());
        newShipping.setOrderId(message.getOrderId());
        newShipping.setUserId(message.getUserId());
        newShipping.setStatus(Status.REGISTED);
        Shipping shipping = shippingService.addShipping(newShipping);

        System.out.println("Wallet value reduced");

        ShippingConcludedListener walletConcludedListener = new ShippingConcludedListener();
        walletConcludedListener.setMessage("Received your message: " + message);

        template.convertAndSend("shipping-queue", walletConcludedListener);
        System.out.println("Response sent: " + walletConcludedListener);

    }



}
