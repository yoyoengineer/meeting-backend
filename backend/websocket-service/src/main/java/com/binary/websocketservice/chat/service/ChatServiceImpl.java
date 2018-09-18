package com.binary.websocketservice.chat.service;

import com.binary.websocketservice.chat.model.Parcel;
import com.binary.websocketservice.chat.model.ParcelForFileMessage;
import com.binary.websocketservice.utils.Destinations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatServiceImpl(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    @Override
    public void sendMessage(Parcel parcel) {

        log.debug("received parceil {}",parcel);
        if(parcel==null) {
            log.debug("Parcel received from chat-service is empty!! For further process");
            return;
        }

        List<String> toUsers = parcel.getToUsers();

        if(toUsers!=null) {
            toUsers.forEach(toUser-> {
                log.debug("sending  message {} to user {}",parcel.getHistory(),toUser);
                // Send the message to each person on the list.
                simpMessagingTemplate.convertAndSendToUser(
                        toUser,
                        Destinations.Chat.privateMessages(),
                        parcel.getHistory());

            });
        }
    }

    @Override
    public void sendMessage(ParcelForFileMessage parcelForFileMessage) {

        if(parcelForFileMessage==null){
            log.debug("Parcel For File Message received from chat-service is null!!! No further process");
            return;
        }

        List<Parcel> parcels = parcelForFileMessage.getParcels();

        if(parcels==null){
            log.debug("List of parcels in Parcel For File Message is null!!! No Further process");
            return;
        }

        // Send each parcel to the appropriate destination
        parcels.forEach(parcel ->
            parcel.getToUsers()
                    .forEach(toUser->{
                        log.debug("Sending message {} to user {}", parcel.getHistory(),toUser);
                        simpMessagingTemplate.convertAndSendToUser(
                                toUser,
                                Destinations.Chat.privateMessages(),
                                parcel.getHistory());
                    })
        );
    }
}
