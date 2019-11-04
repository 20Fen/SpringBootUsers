package com.example.demo.util;

import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate工具类
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
