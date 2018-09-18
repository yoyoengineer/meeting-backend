package com.binary.websocketservice.conference.service;

import com.binary.websocketservice.conference.model.Invitation;

import java.util.List;

public interface ConferenceService {

    void sendInvitation(List<Invitation> invitations);
    void activityNotification();
}
