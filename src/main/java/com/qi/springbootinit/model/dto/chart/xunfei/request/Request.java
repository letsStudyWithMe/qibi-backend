package com.qi.springbootinit.model.dto.chart.xunfei.request;


import com.qi.springbootinit.model.dto.chart.xunfei.request.header.Header;
import com.qi.springbootinit.model.dto.chart.xunfei.request.parameter.Parameter;
import com.qi.springbootinit.model.dto.chart.xunfei.request.payload.Payload;

/**
 * 大模型请求实体
 *
 */
public class Request {

    /**
     * header 部分
     */
    private Header header;

    /**
     * parameter 部分
     */
    private Parameter parameter;

    /**
     * payload 部分
     */
    private Payload payload;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
