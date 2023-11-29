package com.qi.springbootinit.model.dto.chart.xunfei.request.header;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * header部分
 *
 * @author Linzj
 * @date 2023/10/20/020 10:11
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Header {

    /**
     * 应用appid，从开放平台控制台创建的应用中获取
     */
    @JSONField(name = "app_id")
    private String appId;

    @JSONField(name = "uid")
    private String uid;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
