package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

/**
 * Represent the request of a Login in the Game by a Player.
 */
public class LoginEvent extends EventToServer {
    private final String nickname;
    private final String password;

    /**
     * This event is sent when a Player have the will to join to the Game
     *
     * @param nickname is the nickname of the PLayer
     * @param password is the password of the Player
     */
    public LoginEvent(String nickname, String password) {
        super(ClientParser.LOGIN_TYPE);
        this.nickname = nickname;
        this.password = String.valueOf(password.hashCode());
    }
}
