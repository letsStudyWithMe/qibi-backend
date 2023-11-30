package com.qi.springbootinit.model.dto.chart.xunfei.response;


import com.qi.springbootinit.model.dto.chart.xunfei.response.header.Header;
import com.qi.springbootinit.model.dto.chart.xunfei.response.payload.Payload;

import java.util.StringJoiner;

/**
 * 接口响应对象
 *
 */
public class Result {

    private Header header;
    private Payload payload;

    public void setHeader(Header header) {
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Payload getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Result.class.getSimpleName() + "[", "]")
                .add("header=" + header)
                .add("payload=" + payload)
                .toString();
    }
}