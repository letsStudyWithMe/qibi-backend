package com.qi.springbootinit.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HMac;
import com.alibaba.fastjson.JSONObject;

import com.qi.springbootinit.model.dto.chart.xunfei.RoleContent;
import com.qi.springbootinit.model.dto.chart.xunfei.request.Request;
import com.qi.springbootinit.model.dto.chart.xunfei.request.header.Header;
import com.qi.springbootinit.model.dto.chart.xunfei.request.parameter.Chat;
import com.qi.springbootinit.model.dto.chart.xunfei.request.parameter.Parameter;
import com.qi.springbootinit.model.dto.chart.xunfei.request.payload.Message;
import com.qi.springbootinit.model.dto.chart.xunfei.request.payload.Payload;
import com.qi.springbootinit.model.dto.chart.xunfei.response.Result;
import com.qi.springbootinit.model.dto.chart.xunfei.response.payload.Text;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 讯飞大模型
 *
 */
@Slf4j
public class XunFeiBigModelUtils {


    /**
     * 请求地址
     */
    private static final String HOST_URL = "http://spark-openapi.cn-huabei-1.xf-yun.com/v1/assistants/c3po5ycuf95c_v1";

    /**
     * v1版本
     */
    private static final String DOMAIN_1 = "generalv1";

    /**
     * APPID
     */
    private static final String APPID = "0990efa8";

    /**
     * APISecret
     */
    private static final String API_SECRET = "N2RhYjA2OTM5ZGQ1NTBmMTNkNmRmMzlm";

    /**
     * APIKey
     */
    private static final String API_KEY = "b9257f9470f23559de7fa68a72792aa8";

    /**
     * user表示是用户的问题
     */
    private static final String ROLE_USER = "user";

    /**
     * assistant表示AI的回复
     */
    private static final String ROLE_ASSISTANT = "assistant";

    /**
     * 接口响应内容集合
     */
    private static final LinkedList<Result> RESULT_LINKED_LIST = new LinkedList<>();

    /**
     * 对话历史存储集合
     */
    public static List<RoleContent> historyList = new LinkedList<>();

    /**
     * 需要的结果存储集合
     */
    public static List<RoleContent> resultList = new LinkedList<>();


    //调用星火助手
    public static List<RoleContent> getEchartsResult(String content){
        resultList.clear();
        try {
            websocketClient(getAuthUrl(), createReqParams(content));
            while (resultList.isEmpty()){
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            log.info("调用星火助手发生异常"+e.toString());
            return null;
        }
        return resultList;
    }

    /**
     * websocket 连接
     *
     * @param authUrl   鉴权地址
     * @param reqParams 请求参数
     * @throws URISyntaxException 异常
     */
     private static void websocketClient(String authUrl, String reqParams) throws URISyntaxException {
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        URI uri = new URI(url);
        WebSocketClient webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                RESULT_LINKED_LIST.clear();
                send(reqParams);
            }

            @Override
            public void onMessage(String s) {
                // 错误码，0表示正常
                final int successCode = 0;
                // 会话状态，2代表最后一个结果
                final int lastStatus = 2;

                Result result = JSONObject.parseObject(s, Result.class);
                com.qi.springbootinit.model.dto.chart.xunfei.response.header.Header header = result.getHeader();
                if (Objects.equals(successCode, header.getCode())) {
                    RESULT_LINKED_LIST.add(result);
                } else {
                    log.error("大模型接口响应异常，错误码：{}，sid：{}", header.getCode(), header.getSid());
                }

                // 如果是最后的结果，整合答复数据打印出来
                if (Objects.equals(lastStatus, header.getStatus())) {
                    printReply();
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                log.info("WebSocket连接已关闭，原因：{}，状态码：{}，是否远程关闭：{}", i, s, b);
            }

            @Override
            public void onError(Exception e) {
                log.error("大模型接口调用发生异常，异常原因:{},异常位置:{}", e.getMessage(), e.getStackTrace()[0]);
            }
        };
        webSocketClient.connect();
    }

    /**
     * 生成请求参数
     *
     * @param content 对话内容
     * @return 请求参数
     */
    public static String createReqParams(String content) {
        // 组装接口请求参数
        Header header = new Header();
        header.setAppId(APPID);

        Chat chat = new Chat();
        chat.setDomain(DOMAIN_1);
        Parameter parameter = new Parameter();
        parameter.setChat(chat);

        Message message = new Message();
        if (historyList.isEmpty()) {
            com.qi.springbootinit.model.dto.chart.xunfei.request.payload.Text text = new com.qi.springbootinit.model.dto.chart.xunfei.request.payload.Text();
            text.setRole(ROLE_USER);
            text.setContent(content);
            message.setText(Collections.singletonList(text));

            // 添加历史对话集合
            RoleContent roleContent = new RoleContent();
            roleContent.setRole(ROLE_USER);
            roleContent.setContent(content);
            historyList.add(roleContent);
        } else {
            // 添加历史对话集合
            RoleContent roleContent = new RoleContent();
            roleContent.setRole(ROLE_USER);
            roleContent.setContent(content);
            historyList.add(roleContent);
            delHistory();

            List<com.qi.springbootinit.model.dto.chart.xunfei.request.payload.Text> textList = historyList.stream()
                    .map(item -> {
                        com.qi.springbootinit.model.dto.chart.xunfei.request.payload.Text text = new com.qi.springbootinit.model.dto.chart.xunfei.request.payload.Text();
                        text.setContent(item.getContent());
                        text.setRole(item.getRole());
                        return text;
                    })
                    .collect(Collectors.toList());
            message.setText(textList);
        }

        Payload payload = new Payload();
        payload.setMessage(message);

        Request request = new Request();
        request.setHeader(header);
        request.setParameter(parameter);
        request.setPayload(payload);
        return JSONObject.toJSONString(request);
    }

    /**
     * URL鉴权
     *
     * @return 请求url
     * @throws MalformedURLException 异常
     */
    private static String getAuthUrl() throws MalformedURLException {
        String date = DateUtil.format(new Date(), DatePattern.HTTP_DATETIME_FORMAT);
        URL url = new URL(HOST_URL);
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";

        HMac hMac = SecureUtil.hmacSha256(API_SECRET.getBytes(StandardCharsets.UTF_8));
        byte[] digest = hMac.digest(preStr);
        String signature = Base64.encode(digest);
        String authorizationOrigin = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                API_KEY, "hmac-sha256", "host date request-line", signature);
        String authorization = Base64.encode(authorizationOrigin);
        return UriComponentsBuilder.fromUriString(HOST_URL)
                .queryParam("authorization", authorization)
                .queryParam("date", date)
                .queryParam("host", url.getHost()).toUriString();
    }

    /**
     * 如果历史数据太大，将前面的历史数据清除一些
     */
    public static void delHistory() {
        // 由于历史记录最大上线1.2W左右，如果最大值，删除前面的数据
        final int maxLength = 12000;
        Iterator<RoleContent> iterator = historyList.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            int historyLength = historyList.stream()
                    .map(RoleContent::getContent)
                    .mapToInt(String::length).sum();
            if (historyLength > maxLength) {
                iterator.remove();
            } else {
                break;
            }
        }
    }

    /**
     * 打印星火认知大模型回复内容
     */
    private static void printReply() {
        String content = RESULT_LINKED_LIST.stream()
                .map(item -> item.getPayload().getChoices().getText())
                .flatMap(Collection::stream)
                .map(Text::getContent)
                .collect(Collectors.joining());
        RoleContent roleContent = new RoleContent();
        roleContent.setRole(ROLE_ASSISTANT);
        roleContent.setContent(content);
        historyList.add(roleContent);
        resultList.add(roleContent);
    }

}
