package com.binary.profileservice.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class FriendRequest {
    private String username;
    private String name;
    private Long time;
}
