package com.qi.springbootinit.model.dto.chart.xunfei.response.payload;

import java.util.List;
import java.util.StringJoiner;

/**
 * choices部分
 *
 */
public class Choices {

    /**
     * 文本响应状态，取值为[0,1,2]; 0代表首个文本结果；1代表中间文本结果；2代表最后一个文本结果
     */
    private int status;

    /**
     * 返回的数据序号，取值为[0,9999999]
     */
    private int seq;

    /**
     * 文本信息
     */
    private List<Text> text;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeq() {
        return seq;
    }

    public void setText(List<Text> text) {
        this.text = text;
    }

    public List<Text> getText() {
        return text;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Choices.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("seq=" + seq)
                .add("text=" + text)
                .toString();
    }
}