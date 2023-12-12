package com.qi.springbootinit.mq;

import com.rabbitmq.client.*;

public class DirectConsumer {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置主机地址
        factory.setHost("localhost");
        // 创建连接
        Connection connection = factory.newConnection();
        // 创建通道
        Channel channel = connection.createChannel();

        // 声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        // 声明队列xiaos_queue，并绑定到交换器上，使用routingKeyOne路由键
        String queueNameOne = "xiaos_queue";
        channel.queueDeclare(queueNameOne,true,false,false,null);
        channel.queueBind(queueNameOne,EXCHANGE_NAME,"routingKeyOne");

        // 声明队列xiaoq_queue，并绑定到交换器上，使用routingKeyTwo路由键
        String queueNameTwo = "xiaoq_queue";
        channel.queueDeclare(queueNameTwo,true,false,false,null);
        channel.queueBind(queueNameTwo,EXCHANGE_NAME,"routingKeyTwo");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 声明消息接收回调函数
        DeliverCallback deliverCallbackOne = (consumerTag, delivery) -> {
            // 获取消息内容
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoss] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        DeliverCallback deliverCallbackTwo = (consumerTag, delivery) -> {
            // 获取消息内容
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [xiaoqq] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        // 消费消息
        channel.basicConsume(queueNameOne, true, deliverCallbackOne, consumerTag -> {
        });
        channel.basicConsume(queueNameTwo, true, deliverCallbackTwo, consumerTag -> {
        });
    }

}