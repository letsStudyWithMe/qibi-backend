package com.qi.springbootinit.model.dto.chart.xunfei.response.usage;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.StringJoiner;

/**
 * 整体对话数据
 *
 */
public class Text {

    /**
     * 保留字段，可忽略
     */
    @JSONField(name = "question_tokens")
    private int questionTokens;

    /**
     * 包含历史问题的总tokens大小
     */
    @JSONField(name = "question_tokens")
    private int promptTokens;

    /**
     * 回答的tokens大小
     */
    @JSONField(name = "question_tokens")
    private int completionTokens;

    /**
     * prompt_tokens和completion_tokens的和，也是本次交互计费的tokens大小
     */
    @JSONField(name = "question_tokens")
    private int totalTokens;

    public int getQuestionTokens() {
        return questionTokens;
    }

    public void setQuestionTokens(int questionTokens) {
        this.questionTokens = questionTokens;
    }

    public int getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(int promptTokens) {
        this.promptTokens = promptTokens;
    }

    public int getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(int completionTokens) {
        this.completionTokens = completionTokens;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Text.class.getSimpleName() + "[", "]")
                .add("questionTokens=" + questionTokens)
                .add("promptTokens=" + promptTokens)
                .add("completionTokens=" + completionTokens)
                .add("totalTokens=" + totalTokens)
                .toString();
    }
}