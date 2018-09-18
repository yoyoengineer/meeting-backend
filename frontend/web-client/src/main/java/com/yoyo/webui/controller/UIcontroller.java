package com.yoyo.webui.controller;

import com.yoyo.webui.client.RestClient;
import com.yoyo.webui.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UIcontroller {

    private RestClient restClient;

    public UIcontroller(RestClient restClient){
        this.restClient = restClient;
    }


    @GetMapping("/event")
    public String event(Principal user, @RequestParam("eventId")String eventId){
        String username = user.getName();
        Map<String,String> map = new HashMap<>();
        map.put("username",username);
        map.put("eventId",eventId);
        System.out.println(Utils.API_GATEWAY+"/event/ownerofconf");
        try {
            HttpStatus status = restClient.post(Utils.API_GATEWAY + "/event/ownerofconf", map);
            if(status == HttpStatus.OK)
                return "admin-event";
        }
        catch (Exception e){

        }
            return "event";
    }

    @GetMapping("/attended")
    public String attend(){
        return "attended";
    }

    @GetMapping("/edit-profile")
    public String editProfile(){
        return "edit-profile";
    }

    @GetMapping("/create_event")
    public String createEvent(){
        return "create_event";
    }
}
