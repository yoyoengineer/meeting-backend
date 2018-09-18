package com.binary.websocketservice.conference.service;

import com.binary.websocketservice.conference.model.Invitation;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConferenceServiceImpl implements ConferenceService {

    private SimpMessagingTemplate simpMessagingTemplate;

    public ConferenceServiceImpl(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void sendInvitation(List<Invitation> invitations) {

        if(invitations!=null){
//            invitations.forEach()
        }
    }

    @Override
    public void activityNotification() {

    }
}
