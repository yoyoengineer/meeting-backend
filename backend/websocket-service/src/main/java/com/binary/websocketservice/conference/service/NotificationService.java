package com.binary.websocketservice.conference.service;

import com.binary.websocketservice.conference.model.NotificationParcel;

public interface NotificationService {

    void send(NotificationParcel notificationParcel);
}
