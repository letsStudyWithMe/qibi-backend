package com.qi.springbootinit.utils;

import com.qi.springbootinit.common.ErrorCode;
import com.qi.springbootinit.constant.ChartStatusConstant;
import com.qi.springbootinit.exception.BusinessException;
import com.qi.springbootinit.model.entity.Chart;
import com.qi.springbootinit.service.ChartService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * AI生成结果处理工具类
 *
 */
@Slf4j
public class AiResultDetermineUtils {
    @Resource
    private static ChartService chartService;

    /**
     * 判断AI生成的返回的格式，提取正确的结果
     * @param res
     * @param chartId
     * @param type
     * @return
     */
    public static HashMap<String,String> getGenChartAndResult(String res,long chartId, Integer type) {
        HashMap<String, String> result = new HashMap<>();
        String genChart="";
        String genResult="";
        if (res.split("【【【【【【【").length == 2){ //AI生成会出现少一个【的情况
            String[] split1 = res.split("【【【【【【【");
            genResult = split1[1];
            String[] split2 = split1[0].split("【【【【【【");
            genChart = split2[1];
        }else if (res.split("【").length == 3) {
            String[] split = res.split("【");//AI生成只有两个【的情况
            genChart = split[1];
            genResult = split[2];
        }else if (res.split("】】】】】】】").length == 2) {//AI生成】】】】】】】的情况
            String[] split = res.split("】】】】】】】");
            genResult = split[1];
            String[] split1 = split[0].split("【【【【【【");
            genChart = split1[1];
        }else if (res.split("】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】").length == 2) {//AI生成】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】的情况
            String[] split = res.split("】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】】");
            genResult = split[1];
            String[] split1 = split[0].split("【【【【【【");
            genChart = split1[1];
        }else {
            if (res.contains("】")) {//AI生成会出现】的情况。进行替换
                res = res.replace("】", "【");
            }
            String[] split = res.split("【【【【【【【");//正常情况分割
            if (split.length < 3) {
                if (type == 1){//正常流程
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成错误");
                }else if (type == 2){//线程池 || RabbitMQ
                    chartService.handleChartUpdateStatus(chartId, "AI生成错误", ChartStatusConstant.FAILED);
                }
            }
            genChart = split[1];
            genResult = split[2];
        }
        if (genChart.contains("【") || genChart.contains("】")){
            String replace = genChart.replace("【", "");
            String replace1 = replace.replace("】", "");
            genChart = replace1;
        }
        if (genResult.contains("【") || genResult.contains("】")){
            String replace = genResult.replace("【", "");
            String replace1 = replace.replace("】", "");
            genResult = replace1;
        }
        result.put("genResult",genResult);
        result.put("genChart",genChart);
        return result;
    }
}
