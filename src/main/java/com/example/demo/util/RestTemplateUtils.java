package com.example.demo.util;

import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate工具类
 *
 * RPC 的优势在于高效的网络传输模型（常使用 NIO 来实现），以及针对服务调用场景专门设计协议和高效的序列化技术。
 * HTTP 的优势在于它的成熟稳定、使用实现简单、被广泛支持、兼容性良好、防火墙友好、消息的可读性高。
 */
public class RestTemplateUtils {

    private static class SingletonRestTemplate {
        static final RestTemplate INSTANCE = new RestTemplate();
    }

    private RestTemplateUtils() {

    }

    public static RestTemplate getInstance() {
        return SingletonRestTemplate.INSTANCE;
    }
}
