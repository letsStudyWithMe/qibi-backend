/**
 * Copyright 2023 bejson.com
 */
package com.qi.springbootinit.model.dto.chart.xunfei.response.payload;

import com.qi.springbootinit.model.dto.chart.xunfei.response.usage.Usage;

import java.util.StringJoiner;

/**
 * Payload 部分
 *
 * @author Linzj
 * @date 2023/10/19/019 16:55
 */
public class Payload {

    /**
     * choices
     */
    private Choices choices;

    /**
     * usage
     */
    private Usage usage;

    public void setChoices(Choices choices) {
        this.choices = choices;
    }

    public Choices getChoices() {
        return choices;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public Usage getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Payload.class.getSimpleName() + "[", "]")
                .add("choices=" + choices)
                .add("usage=" + usage)
                .toString();
    }
}