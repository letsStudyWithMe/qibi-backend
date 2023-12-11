package com.qi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class DirectProducter {

  private static final String EXCHANGE_NAME = "direct_exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");


        String severityOne = "routingKeyOne";
        String messageOne = "caibiOne";

        String severityTwo = "routingKeyTwo";
        String messageTwo = "caibiTwo";

        channel.basicPublish(EXCHANGE_NAME, severityOne, null, messageOne.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + severityOne + "':'" + messageOne + "'");
        channel.basicPublish(EXCHANGE_NAME, severityTwo, null, messageTwo.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + severityTwo + "':'" + messageTwo + "'");
    }
  }
}