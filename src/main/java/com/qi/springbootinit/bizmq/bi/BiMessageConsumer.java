package com.qi.springbootinit.bizmq.bi;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import com.qi.springbootinit.common.ErrorCode;
import com.qi.springbootinit.common.ResultUtils;
import com.qi.springbootinit.constant.ChartStatusConstant;
import com.qi.springbootinit.constant.MqConstant;
import com.qi.springbootinit.exception.BusinessException;
import com.qi.springbootinit.exception.ThrowUtils;
import com.qi.springbootinit.model.dto.chart.xunfei.RoleContent;
import com.qi.springbootinit.model.entity.Chart;
import com.qi.springbootinit.service.ChartService;
import com.qi.springbootinit.utils.AiResultDetermineUtils;
import com.qi.springbootinit.utils.XunFeiBigModelUtils;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * 图表分析消费者队列
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @SneakyThrows
    @RabbitListener(queues = {MqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("接收到BI队列信息，receiveMessage={}=======================================", message);
        if (StringUtils.isBlank(message)) {
            //消息为空，消息拒绝，不重复发送，不重新放入队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表为空");
        }

        //修改表状态为执行中
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus(ChartStatusConstant.RUNNING);
        boolean updateResult = chartService.updateById(updateChart);
        if (!updateResult) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表执行状态失败");
            return;
        }
        //调用AI
        String result = null;
        try {
            //调用AI接口
            List<RoleContent> echartsResult = XunFeiBigModelUtils.getEchartsResult(chartService.buildUserInput(chart));
            ThrowUtils.throwIf(echartsResult == null, ErrorCode.SPARK_USE_ERROR);
            String res = echartsResult.get(0).getContent();

            HashMap<String, String> genChartAndResult = AiResultDetermineUtils.getGenChartAndResult(res, chartId, 2);
            String genChart = genChartAndResult.get("genChart");
            String genResult = genChartAndResult.get("genResult");
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            updateChartResult.setStatus("succeed");
            updateChartResult.setExecMessage("生成成功");
            updateResult = chartService.updateById(updateChartResult);
            if (!updateResult) {
                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, true);
            log.warn("信息放入队列{}", DateTime.now());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 服务错误");
        }
        //消息确认
        channel.basicAck(deliveryTag, false);
    }

    private void handleChartUpdateError(Long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setStatus(ChartStatusConstant.FAILED);
        updateChartResult.setId(chartId);
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图片失败状态失败" + chartId + "," + execMessage);
        }
    }
}