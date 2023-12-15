package ecommerce.Listeners;

import ecommerce.Config.MQConfig;
import ecommerce.Messages.PaymentConfirmationMessage;
import ecommerce.Messages.WalletConcludedListener;
import ecommerce.Models.Wallet;
import ecommerce.Services.WalletService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfirmationListener {

    @Autowired
    WalletService walletService;

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

        Wallet wallet = walletService.getWalletByUserId(message.getUserId());

        walletService.takeMoneyWallet(wallet.getId(), (float) message.getPriceTotal());


        System.out.println("Wallet value reduced");

        WalletConcludedListener walletConcludedListener = new WalletConcludedListener();
        walletConcludedListener.setMessage("Received your message: " + message);

        template.convertAndSend("wallet-queue", walletConcludedListener);
        System.out.println("Response sent: " + walletConcludedListener);

    }



}
