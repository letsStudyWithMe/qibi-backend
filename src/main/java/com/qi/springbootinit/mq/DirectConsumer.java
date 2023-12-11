package com.qi.springbootinit.mq;

import com.rabbitmq.client.*;

public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String queueNameOne = "xiaos_queue";
        channel.queueDeclare(queueNameOne,true,false,false,null);
        channel.queueBind(queueNameOne,EXCHANGE_NAME,"routingKeyOne");

        String queueNameTwo = "xiaoq_queue";
        channel.queueDeclare(queueNameTwo,true,false,false,null);
        channel.queueBind(queueNameTwo,EXCHANGE_NAME,"routingKeyTwo");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallbackOne = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoss] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallbackTwo = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoqq] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(queueNameOne, true, deliverCallbackOne, consumerTag -> {
        });
        channel.basicConsume(queueNameTwo, true, deliverCallbackTwo, consumerTag -> {
        });
    }
}