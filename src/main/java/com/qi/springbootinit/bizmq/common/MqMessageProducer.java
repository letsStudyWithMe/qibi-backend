package com.qi.springbootinit.bizmq.common;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 公共队列生产者
 */
@Component
public class MqMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发信息
     * @param exchange   交换机
     * @param routingKey      路由
     * @param message   消息
     */
    public void sendMessage(String exchange,String routingKey,String message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }
}