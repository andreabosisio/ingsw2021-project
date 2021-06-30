package it.polimi.ingsw.client.utils;

import it.polimi.ingsw.client.events.send.EventToServer;

public interface CommandListenerObserver {
    void update(EventToServer eventToServer);

    void setNickname(String nickname);
}
