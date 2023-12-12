package com.qi.springbootinit.service;

import com.qi.springbootinit.model.dto.chart.ChartGenByAiRequest;
import com.qi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图表Service
 *
 */
public interface ChartService extends IService<Chart> {

    /**
     * 组装调用AI的参数
     *
     * @param chart
     * @return
     */
    String buildUserInput(Chart chart);

    /**
     * 修改Chart表状态
     *
     * @param chartId
     * @param execMessage
     * @return
     */
    void handleChartUpdateStatus(long chartId, String execMessage,String status);

    /**
     * 校验前端传过来的参数
     *
     * @param multipartFile
     * @param chartGenByAiRequest
     * @return
     */
    void verifyGenChartParam(MultipartFile multipartFile, ChartGenByAiRequest chartGenByAiRequest);
}
