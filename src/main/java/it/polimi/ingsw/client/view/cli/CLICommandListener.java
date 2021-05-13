package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.events.send.LoginEvent;
import it.polimi.ingsw.client.events.send.SelectNumberPlayersEvent;
import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;

import java.util.Scanner;


/**
 * This class listens to inputs from the terminal
 */
public class CLICommandListener implements CommandListener {
    CommandListenerObserver commandListenerObserver;
    private final Scanner scanner = new Scanner(System.in);
    //login();

    protected void askCredentials() {

        System.out.println("Insert a nickname:");
        String nickname = scanner.nextLine();

        System.out.println("Insert a password:");
        String password = scanner.nextLine();

        notifyObservers(new LoginEvent(nickname, password));
    }

    protected void askNumberOfPlayers(String payload) {
        System.out.println("Choose number of players (" + payload + ") :");
        String numberOfPlayers = scanner.nextLine();

        notifyObservers(new SelectNumberPlayersEvent(Integer.parseInt(numberOfPlayers)));
    }

    @Override
    public void notifyObservers(SendEvent sendEvent) {
        commandListenerObserver.update(sendEvent);
    }

    @Override
    public void registerObservers(CommandListenerObserver commandListenerObserver) {
        this.commandListenerObserver = commandListenerObserver;
    }
}
