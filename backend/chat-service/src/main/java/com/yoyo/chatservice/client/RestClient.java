package com.yoyo.chatservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Base64;

@Component
public class RestClient {

    private RestTemplate rest;
    private HttpHeaders headers;
    private ObjectMapper mapper;

    public RestClient() {
        this.rest = new RestTemplate();
        this.rest.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        this.headers = new HttpHeaders();
        this.mapper = new ObjectMapper();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        String auth = "websocket" + ":" + "password";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
    }

    public HttpStatus get(String url) {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return responseEntity.getStatusCode();
    }

    public HttpStatus post(String url, Object obj) {

        String json;

        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            json="";
            e.printStackTrace();
        }

        System.out.println("JSON STRING IS " + json);
        HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
        System.out.println("In entity body "+requestEntity.getBody());
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getStatusCode();
    }

}