package com.qi.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class DirectProducter {

  private static final String EXCHANGE_NAME = "direct_exchange";

public static void main(String[] argv) throws Exception {
    // 创建一个连接工厂
    ConnectionFactory factory = new ConnectionFactory();
    // 设置主机地址
    factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
        // 声明一个交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");


        // 定义消息1的路由键
        String severityOne = "routingKeyOne";
        // 定义消息1的内容
        String messageOne = "caibiOne";

        // 定义消息2的路由键
        String severityTwo = "routingKeyTwo";
        // 定义消息2的内容
        String messageTwo = "caibiTwo";

        // 发布消息1
        channel.basicPublish(EXCHANGE_NAME, severityOne, null, messageOne.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + severityOne + "':'" + messageOne + "'");
        // 发布消息2
        channel.basicPublish(EXCHANGE_NAME, severityTwo, null, messageTwo.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + severityTwo + "':'" + messageTwo + "'");
    }
  }
}