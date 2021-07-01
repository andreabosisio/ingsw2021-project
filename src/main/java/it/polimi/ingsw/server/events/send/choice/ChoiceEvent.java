package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.events.send.EventToClient;

/**
 * This class is the implementation of the request messages of a game choice sent from the Server.
 */
public abstract class ChoiceEvent extends EventToClient {
    private final String nickname;
    private final String type;

    /**
     * Create a new Choice Message.
     *
     * @param nickname of the Player who has to make a choice
     * @param type     of the choice
     */
    public ChoiceEvent(String nickname, String type) {
        this.nickname = nickname;
        this.type = type;
    }

    /**
     * @return the nickname of the Player who has to make a choice
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Check if the receiver's nickname of this Event is the same of the given nickname.
     *
     * @param nickname to check
     * @return true if the receiver's nickname of this Event is the same of the given nickname
     */
    @Override
    public boolean isForYou(String nickname) {
        return nickname.equals(this.nickname);
    }
}