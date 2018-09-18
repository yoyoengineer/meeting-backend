package com.binary.websocketservice.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
public class ParcelForFileMessage {
    private final List<Parcel> parcels;
}
