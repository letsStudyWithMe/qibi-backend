package com.qi.springbootinit.mq;

import com.rabbitmq.client.*;

public class FanoutConsumer {
    private static final String EXCHANGE_NAME = "fanout-exchange";

    public static void main(String[] argv) throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        //建立连接
        Connection connection = factory.newConnection();
        //创建两个通道
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();

        //绑定交换机
        channel1.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel2.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        //创建队列
        String queueName1 = "one-queue";
        channel1.queueDeclare(queueName1,true,false,false,null);
        channel1.queueBind(queueName1,EXCHANGE_NAME,"");

        String queueName2 = "two-queue";
        channel2.queueDeclare(queueName2,true,false,false,null);
        channel2.queueBind(queueName2,EXCHANGE_NAME,"");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //创建交付回调函数
        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [one] Received '" + message + "'");
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [two] Received '" + message + "'");
        };

        //开始消费消息队列
        channel1.basicConsume(queueName1, true, deliverCallback1, consumerTag -> { });
        channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
    }
}
