package it.polimi.ingsw;

import com.google.gson.*;
import it.polimi.ingsw.client.ClientApp;

import it.polimi.ingsw.server.ServerApp;
import it.polimi.ingsw.server.events.receive.MarketEvent;
import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.receive.SetupEvent;
import it.polimi.ingsw.server.model.gameBoard.DeckLeader;
import it.polimi.ingsw.server.model.gameBoard.DevelopmentCardsGrid;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.io.*;

/**
 * Hello world!
 */
public class App {

    /**
     * This is the main method to start the app
     * -server to start in server mode
     * -cli to start in client mode with CLI
     * -gui to start in client mode with GUI
     *
     * @param args args to start app(server/cli/gui)
     */
    public static void main(String[] args) {
        if (args[0].equals("-server"))
            ServerApp.main(args);
        else
            ClientApp.main(args);
    }
}

