package com.qi.springbootinit.model.dto.chart.xunfei.response.payload;

import java.util.StringJoiner;

/**
 * 文本信息
 *
 * @author Linzj
 * @date 2023/10/19/019 16:46
 */
public class Text {

    /**
     * AI的回答内容
     */
    private String content;

    /**
     * 角色标识，固定为assistant，标识角色为AI
     */
    private String role;

    /**
     * 结果序号，取值为[0,10]; 当前为保留字段，开发者可忽略
     */
    private int index;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Text.class.getSimpleName() + "[", "]")
                .add("content='" + content + "'")
                .add("role='" + role + "'")
                .add("index=" + index)
                .toString();
    }
}