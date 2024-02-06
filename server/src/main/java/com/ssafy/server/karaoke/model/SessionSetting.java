package com.ssafy.server.karaoke.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class SessionSetting {
    private int numberOfParticipants;
    private boolean isPrivate;
    private String password;

    public SessionSetting(int numberOfParticipants, boolean isPrivate) {
        setNumberOfParticipants(numberOfParticipants);
        setPrivate(isPrivate);
    }

    public SessionSetting(int numberOfParticipants, boolean isPrivate, String password) {
        setNumberOfParticipants(numberOfParticipants);
        setPrivate(isPrivate);
        setPassword(password);
    }

}
