package com.qi.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.qi.springbootinit.annotation.AuthCheck;
import com.qi.springbootinit.common.BaseResponse;
import com.qi.springbootinit.common.DeleteRequest;
import com.qi.springbootinit.common.ErrorCode;
import com.qi.springbootinit.common.ResultUtils;
import com.qi.springbootinit.constant.CommonConstant;
import com.qi.springbootinit.constant.UserConstant;
import com.qi.springbootinit.exception.BusinessException;
import com.qi.springbootinit.exception.ThrowUtils;
import com.qi.springbootinit.manager.RedisLimiterManager;
import com.qi.springbootinit.model.dto.chart.*;
import com.qi.springbootinit.model.dto.chart.xunfei.RoleContent;
import com.qi.springbootinit.model.entity.Chart;
import com.qi.springbootinit.model.entity.User;
import com.qi.springbootinit.model.vo.BiResponse;
import com.qi.springbootinit.service.ChartService;
import com.qi.springbootinit.service.UserService;
import com.qi.springbootinit.utils.ExcelUtils;
import com.qi.springbootinit.utils.SqlUtils;
import com.qi.springbootinit.utils.XunFeiBigModelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 图表信息接口
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    private final static Gson GSON = new Gson();


    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ROOT_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                     HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/user/list/page")
    public BaseResponse<Page<Chart>> listUserChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);

        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = new Page<>();

        //管理员可以查看所有图表
        if (loginUser.getUserRole().equals("root")){
            chartPage = chartService.page(new Page<>(current, size),
                    getQueryWrapper(chartQueryRequest));
        } else {
            chartQueryRequest.setUserId(loginUser.getId());
            chartPage = chartService.page(new Page<>(current, size),
                    getQueryWrapper(chartQueryRequest));
        }
        return ResultUtils.success(chartPage);
    }

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 智能分析
     *
     * @param multipartFile
     * @param chartGenByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/genChartByAi")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                 @Valid ChartGenByAiRequest chartGenByAiRequest, HttpServletRequest request) {
        String name = chartGenByAiRequest.getName();
        String chartType = chartGenByAiRequest.getChartType();
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

        //登陆才可以使用
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        redisLimiterManager.doRateLimit("genChartByAi_"+loginUser.getId());

        // 构建⽤户输入信息
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        if (StringUtils.isNotBlank(chartType)) {
            userInput.append(goal).append("请使用" + chartType).append(",要生成图例，图例与标题不能重叠").append("\n");
        }
        // 压缩后的数据（将multipartFile转换为CSV格式）
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        if ("表格转换错误".equals(csvData)) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "表格转换错误");
        }
        userInput.append("原始数据：").append(csvData).append("\n");

        //调用AI接口
        List<RoleContent> echartsResult = XunFeiBigModelUtils.getEchartsResult(userInput.toString());
        if (echartsResult == null) {
            return ResultUtils.error(ErrorCode.SPARK_USE_ERROR);
        }
        String res = echartsResult.get(0).getContent();
        if (res.contains("】")) {
            res = res.replace("】", "【");
        }
        String[] split = res.split("【【【【【【【");
        if (split.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成错误");
        }

        //保存图表信息
        String genChart = split[1];
        String genResult = split[2];
        Chart chart = new Chart();
        chart.setChartType(chartType);
        chart.setChartData(csvData);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setGoal(goal);
        chart.setUserId(loginUser.getId());
        chart.setCreateTime(new Date());
        chart.setName(name);
        boolean saveResult = chartService.save(chart);
        if (!saveResult) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "图表保存失败");
        }
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }

    /**
     * 智能分析(异步线程池)
     *
     * @param multipartFile
     * @param chartGenByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/genChartByAiThreadPool")
    public BaseResponse<BiResponse> genChartByAiThreadPool(@RequestPart("file") MultipartFile multipartFile,
                                                 @Valid ChartGenByAiRequest chartGenByAiRequest, HttpServletRequest request) {
        String name = chartGenByAiRequest.getName();
        String chartType = chartGenByAiRequest.getChartType();
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

        //登陆才可以使用
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }

        redisLimiterManager.doRateLimit("genChartByAi_"+loginUser.getId());

        // 构建⽤户输入信息
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        if (StringUtils.isNotBlank(chartType)) {
            userInput.append(goal).append("请使用" + chartType).append(",要生成图例，图例与标题不能重叠").append("\n");
        }
        // 压缩后的数据（将multipartFile转换为CSV格式）
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        if ("表格转换错误".equals(csvData)) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "表格转换错误");
        }
        userInput.append("原始数据：").append(csvData).append("\n");



        //保存图表信息
        Chart chart = new Chart();
        chart.setChartType(chartType);
        chart.setChartData(csvData);
        chart.setStatus("wait");
        chart.setGoal(goal);
        chart.setUserId(loginUser.getId());
        chart.setCreateTime(new Date());
        chart.setName(name);
        boolean saveResult = chartService.save(chart);
        if (!saveResult) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "图表保存失败");
        }

        CompletableFuture.runAsync(()->{
            //修改图表的状态为 执行中
            Chart chartUpdate = new Chart();
            chartUpdate.setId(chart.getId());
            chartUpdate.setStatus("running");
            boolean resUpdate = chartService.updateById(chartUpdate);
            if (!resUpdate){
                handleChartUpdateError(chart.getId(),"更新图表执行中状态失败");
                return;
            }

            //调用AI接口
            List<RoleContent> echartsResult = XunFeiBigModelUtils.getEchartsResult(userInput.toString());
            if (echartsResult == null) {
                handleChartUpdateError(chart.getId(), "分析失败");
            }
            String res = echartsResult.get(0).getContent();
            if (res.contains("】")) {
                res = res.replace("】", "【");
            }
            String[] split = res.split("【【【【【【【");
            if (split.length < 3) {
                handleChartUpdateError(chart.getId(), "AI生成错误");
            }
            String genChart = split[1];
            String genResult = split[2];
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            updateChartResult.setStatus("succeed");
            updateChartResult.setExecMessage("生成成功");
            boolean updateResult = chartService.updateById(updateChartResult);
            if (!updateResult) {
                handleChartUpdateError( chart.getId(),"更新图表成功状态失败");
            }
        });

        BiResponse biResponse = new BiResponse();
        /*biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);*/
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }

    private void handleChartUpdateError(long chartId,String execMessage){
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败"+ chartId +","+ execMessage);
        }
    }
}
