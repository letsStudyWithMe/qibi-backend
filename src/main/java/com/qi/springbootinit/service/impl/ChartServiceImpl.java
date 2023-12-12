package com.qi.springbootinit.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qi.springbootinit.common.ErrorCode;
import com.qi.springbootinit.exception.ThrowUtils;
import com.qi.springbootinit.mapper.ChartMapper;
import com.qi.springbootinit.model.dto.chart.ChartGenByAiRequest;
import com.qi.springbootinit.model.entity.Chart;
import com.qi.springbootinit.service.ChartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
* @author Administrator
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-11-26 16:48:42
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements ChartService {

    @Resource
    private ChartMapper chartMapper;

    /**
     * 组装调用AI的参数
     * @param chart
     * @return
     */
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

    /**
     * 修改Chart表状态
     * @param chartId
     * @param execMessage
     * @param status
     */
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

    /**
     * 校验前端传过来的参数
     * @param multipartFile
     * @param chartGenByAiRequest
     */
    @Override
    public void verifyGenChartParam(MultipartFile multipartFile, ChartGenByAiRequest chartGenByAiRequest) {
        String name = chartGenByAiRequest.getName();
        String goal = chartGenByAiRequest.getGoal();
        //校验
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isBlank(name), ErrorCode.PARAMS_ERROR, "名称为空");

        long size = multipartFile.getSize();//大小
        String originalFilename = multipartFile.getOriginalFilename();//原始文件名称
        //校验文件大小
        final long ONE_MB=1024*1024l;
        ThrowUtils.throwIf(size>ONE_MB,ErrorCode.PARAMS_ERROR,"文件超过1MB");
        //校验文件格式
        final String type = FileUtil.getSuffix(originalFilename);
        List<String> fileType = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!fileType.contains(type),ErrorCode.PARAMS_ERROR,",文件格式不正确，请上传正确的文件格式");
    }
}




