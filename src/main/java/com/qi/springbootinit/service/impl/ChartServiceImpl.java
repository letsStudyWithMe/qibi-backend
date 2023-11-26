package com.qi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qi.springbootinit.mapper.ChartMapper;
import com.qi.springbootinit.model.entity.Chart;
import com.qi.springbootinit.service.ChartService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-11-26 16:48:42
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

}




