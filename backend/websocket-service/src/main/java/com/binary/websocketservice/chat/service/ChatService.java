package com.binary.websocketservice.chat.service;

import com.binary.websocketservice.chat.model.Parcel;
import com.binary.websocketservice.chat.model.ParcelForFileMessage;

public interface ChatService {

    void sendMessage(Parcel parcel);
    void sendMessage(ParcelForFileMessage parcelForFileMessage);
}
