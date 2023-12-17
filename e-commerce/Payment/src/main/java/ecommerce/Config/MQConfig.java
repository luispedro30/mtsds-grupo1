package ecommerce.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String QUEUE = "payment_confirmation_requests";

    public static final String EXCHANGE = "exchange_confirmation_requests";

    public static final String ROUTING_KEY_1 = "routing_key_1";
    public static final String ROUTING_KEY_2 = "routing_key_2";
    public static final String ROUTING_KEY_3 = "routing_key_3";


    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }

    @Bean
    public Queue productsQueue() {
        return new Queue("products-queue"); // Destination queue for sending messages
    }

    @Bean
    public Queue walletQueue() {
        return new Queue("wallet-queue"); // Destination queue for sending messages
    }

    @Bean
    public Queue shippingQueue() {
        return new Queue("shipping-queue"); // Destination queue for sending messages
    }

    @Bean
    public Queue payment2EmailQueue() {
        return new Queue("payment-2-email-queue"); // Destination queue for sending messages
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding1(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_1);
    }

    @Bean
    public Binding binding2(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_2);
    }

    @Bean
    public Binding binding3(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_3);
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

}

