package com.qi.springbootinit.model.dto.chart.xunfei.request.parameter;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * chat 部分
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Chat {

    /**
     * 取值为[general,generalv2]
     * 指定访问的领域,general指向V1.5版本 generalv2指向V2版本。注意：不同的取值对应的url也不一样！
     */
    @JSONField(name = "domain")
    private String domain;

    /**
     * 取值为[0,1],默认为0.5
     * 核采样阈值。用于决定结果随机性，取值越高随机性越强即相同的问题得到的不同答案的可能性越高
     */
    @JSONField(name = "temperature")
    private Float temperature;

    /**
     * V1.5取值为[1,4096]，V2.0取值为[1,8192]。默认为2048
     * 模型回答的tokens的最大长度
     */
    @JSONField(name = "max_tokens")
    private Integer maxTokens;

    /**
     * 取值为[1，6],默认为4
     * 从k个候选中随机选择⼀个（⾮等概率）
     */
    @JSONField(name = "top_k")
    private Integer topK;

    /**
     * 需要保障用户下的唯一性，用于关联用户会话
     */
    @JSONField(name = "chat_id")
    private String chatId;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
