package com.qi.springbootinit.model.dto.chart.xunfei.request.payload;

import java.util.List;

/**
 * message部分
 *
 * @author Linzj
 * @date 2023/10/20/020 10:19
 */
public class Message {

    /**
     * text 部分
     */
    List<Text> text;

    public List<Text> getText() {
        return text;
    }

    public void setText(List<Text> text) {
        this.text = text;
    }
}
