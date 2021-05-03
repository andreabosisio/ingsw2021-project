package it.polimi.ingsw.server.network;

public class PlayerData {
    private boolean online;
    private final String username;
    private final String password;
    private ClientHandler clientHandler;

    public PlayerData(String username, String password, ClientHandler clientHandler) {
        this.username = username;
        this.password = password;
        this.clientHandler = clientHandler;
        this.online=true;
    }

    public void setClientConnectionHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }
}
