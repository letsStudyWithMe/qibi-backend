package com.qi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qi.springbootinit.mapper.ChartMapper;
import com.qi.springbootinit.mapper.PostThumbMapper;
import com.qi.springbootinit.model.entity.Chart;
import com.qi.springbootinit.service.ChartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author Administrator
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-11-26 16:48:42
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

    @Resource
    private ChartMapper chartMapper;

    @Override
    public String buildUserInput(Chart chart){
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        // 构建⽤户输入信息
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(chartType)) {
            userInput.append(goal).append("请使用" + chartType).append(",要生成图例，图例与标题不能重叠").append("\n");
        }
        userInput.append("原始数据：").append("\n").append(csvData).append("\n");
        return userInput.toString();
    }

    @Override
    public void handleChartUpdateStatus(long chartId, String execMessage,String status){
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus(status);
        updateChartResult.setExecMessage(execMessage);
        int res = chartMapper.updateById(updateChartResult);
        if (res == 0) {
            log.error("更新图表失败状态失败"+ chartId +","+ execMessage);
        }
    }
}




