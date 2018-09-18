package com.yoyo.conferenceservice.model;

import com.kanz.conferenceservice.model.mongodb.Conference;
import com.yoyo.conferenceservice.model.mongodb.Conference;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
@ToString
public class ConferenceParcel {
    private final Integer pageNumber;
    private final Integer totalPages;
    private final List<Conference> conferences;
}
