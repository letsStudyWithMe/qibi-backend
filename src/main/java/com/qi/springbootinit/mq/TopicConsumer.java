package com.qi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class TopicConsumer {

    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String queueNameOne = "frontend_queue";
        channel.queueDeclare(queueNameOne,true,false,false,null);
        channel.queueBind(queueNameOne,EXCHANGE_NAME,"#.前端.#");

        String queueNameTwo = "backend_queue";
        channel.queueDeclare(queueNameTwo,true,false,false,null);
        channel.queueBind(queueNameTwo,EXCHANGE_NAME,"#.后端.#");

        String queueNameThree = "product_queue";
        channel.queueDeclare(queueNameThree,true,false,false,null);
        channel.queueBind(queueNameThree,EXCHANGE_NAME,"#.产品.#");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallbackOne = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [one-frontend] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallbackTwo = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [two-backend] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallbackThree = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [three-product] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(queueNameOne, true, deliverCallbackOne, consumerTag -> {
        });
        channel.basicConsume(queueNameTwo, true, deliverCallbackTwo, consumerTag -> {
        });
        channel.basicConsume(queueNameThree, true, deliverCallbackThree, consumerTag -> {
        });
    }
}