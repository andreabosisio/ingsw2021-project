package it.polimi.ingsw;

import it.polimi.ingsw.client.ClientApp;
import it.polimi.ingsw.client.model.FaithTrack;
import it.polimi.ingsw.client.model.MarketTray;
import com.google.gson.Gson;

import it.polimi.ingsw.client.model.DevelopmentCardsGrid;
import it.polimi.ingsw.client.model.Marble;
import it.polimi.ingsw.server.ServerApp;
import it.polimi.ingsw.server.events.receive.MarketReceiveEvent;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        if (args[0].equals("-server"))
            ServerApp.main(args);
        else
            ClientApp.main(args);
    }
}

