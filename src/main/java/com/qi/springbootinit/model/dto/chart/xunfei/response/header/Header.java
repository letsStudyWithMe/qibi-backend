package com.qi.springbootinit.model.dto.chart.xunfei.response.header;

import java.util.StringJoiner;

/**
 * header部分
 *
 * @author Linzj
 * @date 2023/10/19/019 16:41
 */
public class Header {

    /**
     * 错误码，0表示正常，非0表示出错；详细释义可在接口说明文档最后的错误码说明了解
     */
    private int code;

    /**
     * 会话是否成功的描述信息
     */
    private String message;

    /**
     * 会话的唯一id，用于讯飞技术人员查询服务端会话日志使用,出现调用错误时建议留存该字段
     */
    private String sid;

    /**
     * 会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果
     */
    private int status;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Header.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("message='" + message + "'")
                .add("sid='" + sid + "'")
                .add("status=" + status)
                .toString();
    }
}