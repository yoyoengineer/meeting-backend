package com.yoyo.webui.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class RestClient {

    private RestTemplate rest;
    private HttpHeaders headers;
    private ObjectMapper mapper;

    public RestClient() {
        this.rest = new RestTemplate();
        this.headers = new HttpHeaders();
        this.mapper = new ObjectMapper();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        String auth = "conference" + ":" + "password";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
    }

    public HttpStatus get(String url) {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        System.out.println("This is the body: " +requestEntity.getBody());
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
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getStatusCode();
    }

}