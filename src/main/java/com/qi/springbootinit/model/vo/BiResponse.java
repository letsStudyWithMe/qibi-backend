package com.qi.springbootinit.model.vo;

import com.google.gson.Gson;
import lombok.Data;

import java.io.Serializable;

/**
 * 调用星火助手返回值
 *
 */
@Data
public class BiResponse implements Serializable {

    private final static Gson GSON = new Gson();

    /**
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;

    /**
     * 图表表id
     */
    private Long chartId;
}
