package ecommerce.Listeners;

import ecommerce.Messages.UserRegistrationMessage;
import ecommerce.Models.Wallet;
import ecommerce.Repository.WalletRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationListener {

    @Autowired
    WalletRepository walletRepository;

    @RabbitListener(queues = "user-queue")
    public void listener(UserRegistrationMessage userRegistrationMessage){

        System.out.println(
                "paymentId :" + userRegistrationMessage.getUserId() + ", " +
                        "orderId :" + userRegistrationMessage.getValue());

        Wallet wallet = walletRepository.findByUserId(userRegistrationMessage.getUserId());

        wallet.setValue((userRegistrationMessage.getValue()));

        walletRepository.save(wallet);

    }
}
