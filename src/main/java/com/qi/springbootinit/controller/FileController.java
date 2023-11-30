package com.qi.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.qi.springbootinit.common.BaseResponse;
import com.qi.springbootinit.common.ErrorCode;
import com.qi.springbootinit.common.ResultUtils;
import com.qi.springbootinit.constant.FileConstant;
import com.qi.springbootinit.exception.BusinessException;
import com.qi.springbootinit.manager.CosManager;
import com.qi.springbootinit.model.dto.file.UploadFileRequest;
import com.qi.springbootinit.model.entity.User;
import com.qi.springbootinit.model.enums.FileUploadBizEnum;
import com.qi.springbootinit.service.UserService;

import java.io.File;
import java.util.Arrays;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口
 *
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

}
