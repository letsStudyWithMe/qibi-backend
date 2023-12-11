package com.qi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TopicProducter {

  private static final String EXCHANGE_NAME = "topic_exchange";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String routingKeyOne = "a.前端.后端.a";
        String messageOne = "前后联动";

        String routingKeyTwo = "后端";
        String messageTwo = "后端";

        String routingKeyThree = ".前端.产品.";
        String messageThree = "前产联动";

        String routingKeyFour = ".后端.";
        String messageFour = ".后端.";

        String routingKeyFive = "后端.";
        String messageFive = "后端.";

        channel.basicPublish(EXCHANGE_NAME, routingKeyOne, null, messageOne.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + routingKeyOne + "':'" + messageOne + "'");

        channel.basicPublish(EXCHANGE_NAME, routingKeyTwo, null, messageTwo.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + routingKeyTwo + "':'" + messageTwo + "'");

        channel.basicPublish(EXCHANGE_NAME, routingKeyThree, null, messageThree.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + routingKeyThree + "':'" + messageThree + "'");

        channel.basicPublish(EXCHANGE_NAME, routingKeyFour, null, messageFour.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + routingKeyFour + "':'" + messageFour + "'");

        channel.basicPublish(EXCHANGE_NAME, routingKeyFive, null, messageFive.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + routingKeyFive + "':'" + messageFive + "'");
    }
  }
  //..
}