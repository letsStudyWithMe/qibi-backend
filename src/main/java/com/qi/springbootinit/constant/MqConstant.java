package com.qi.springbootinit.constant;

/**
 * mq队列名称
 *
 */
public interface MqConstant {

    /**
     * BI队列名称
     */
    String BI_QUEUE_NAME = "bi_queue";

    /**
     * BI交换机名称
     */
    String BI_EXCHANGE_NAME = "bi_exchange";

    /**
     * BI路由名称
     */
    String BI_ROUTING_KEY = "bi_routingKey";

    /**
     * BI死信队列名称
     */
    String BI_DEAD_QUEUE_NAME = "bi_dead_queue";

    /**
     * BI死信交换机名称
     */
    String BI_DEAD_EXCHANGE_NAME = "bi_dead_exchange";

    /**
     * BI死信路由名称
     */
    String BI_DEAD_ROUTING_KEY = "bi_dead_routingKey";





}
