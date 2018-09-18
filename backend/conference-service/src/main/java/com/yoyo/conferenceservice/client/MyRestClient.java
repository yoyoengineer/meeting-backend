package com.yoyo.conferenceservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanz.conferenceservice.ustils.UtilsClass;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;

@Component
public class MyRestClient {

    private RestTemplate rest;
    private ObjectMapper mapper;
    private final String BASE_URL ="http://localhost:8080";

    public MyRestClient() {
        this.rest = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    public String get(String endPoint){
        return get(endPoint,null,null);
    }

    public String get(String endPoint,String username,String password) {
        HttpEntity<String> requestEntity = new HttpEntity<String>("", getheadersWithAuthentication(username,password));
        ResponseEntity<String> responseEntity = rest.exchange(BASE_URL+endPoint, HttpMethod.GET, requestEntity, String.class);
        return responseEntity.getBody();
    }

    public HttpStatus post(String endPoint, Object obj){
        return post(endPoint,obj,null,null);
    }

    public HttpStatus post(String endPoint, Object obj,String username, String password) {

        String json;

        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            json="";
            e.printStackTrace();
        }

        System.out.println("JSON STRING IS " + json);
        HttpEntity<String> requestEntity = new HttpEntity<String>(json, getheadersWithAuthentication(username,password));
        ResponseEntity<String> responseEntity = rest.exchange(BASE_URL+endPoint, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getStatusCode();
    }




    private HttpHeaders getheadersWithAuthentication(String username,String password){
         HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");

        if(username!=null && password!=null)
            setBasicAuthentication(headers,username,password);
        return headers;
    }

    private void setBasicAuthentication(HttpHeaders headers,String username, String password){
            String auth = username + ":" + password;
            headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));
    }

}