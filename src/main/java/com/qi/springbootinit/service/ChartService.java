package com.qi.springbootinit.service;

import com.qi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qi.springbootinit.model.entity.User;

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
     * 组装调用AI的参数
     *
     * @param chartId
     * @param execMessage
     * @return
     */
    void handleChartUpdateStatus(long chartId, String execMessage,String status);
}
