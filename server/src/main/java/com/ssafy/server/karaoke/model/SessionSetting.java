package com.ssafy.server.karaoke.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class SessionSetting {
    private String manager;
    private int numberOfParticipants;
    private boolean isPrivate;
    private String password;

    public SessionSetting(String manager, int numberOfParticipants, boolean isPrivate) {
        setManager(manager);
        setNumberOfParticipants(numberOfParticipants);
        setPrivate(isPrivate);
    }

    public SessionSetting(String manager, int numberOfParticipants, boolean isPrivate, String password) {
        setManager(manager);
        setNumberOfParticipants(numberOfParticipants);
        setPrivate(isPrivate);
        setPassword(password);
    }

}
