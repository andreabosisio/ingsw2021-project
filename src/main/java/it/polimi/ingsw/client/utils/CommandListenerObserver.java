package it.polimi.ingsw.client.utils;

import it.polimi.ingsw.client.events.send.SendEvent;

public interface CommandListenerObserver {
    void update(SendEvent sendEvent);

    void setNickname(String nickname);
}
