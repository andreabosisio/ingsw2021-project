package it.polimi.ingsw.client.events.send;

public class LoginEvent extends SendEvent {
    private final String nickname;
    private final String password;

    public LoginEvent(String nickname, String password) {
        super("login");
        this.nickname = nickname;
        this.password = String.valueOf(password.hashCode());
    }
}
