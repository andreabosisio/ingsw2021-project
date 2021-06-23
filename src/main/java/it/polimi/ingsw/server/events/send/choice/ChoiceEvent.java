package it.polimi.ingsw.server.events.send.choice;

import com.google.gson.Gson;
import it.polimi.ingsw.server.events.send.SendEvent;

/**
 * This class is the implementation of the request messages of a game choice sent from the Server.
 */
public abstract class ChoiceEvent implements SendEvent {
    private final String nickname;
    private final String type;

    /**
     * Create a new Choice Message.
     *
     * @param nickname of the Player who has to make a choice
     * @param type of the choice
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
     * Traduces this message in a JSON format.
     *
     * @return the String containing the JSON message
     */
    @Override
    public String toJson(){
        return new Gson().toJson(this);
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