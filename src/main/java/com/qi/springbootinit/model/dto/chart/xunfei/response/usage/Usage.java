package com.qi.springbootinit.model.dto.chart.xunfei.response.usage;

import java.util.StringJoiner;

/**
 * Usage 部分,在最后一次结果返回
 *
 */
public class Usage {

    /**
     * 整体对话数据
     */
    private Text text;

    public void setText(Text text) {
        this.text = text;
    }

    public Text getText() {
        return text;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Usage.class.getSimpleName() + "[", "]")
                .add("text=" + text)
                .toString();
    }
}