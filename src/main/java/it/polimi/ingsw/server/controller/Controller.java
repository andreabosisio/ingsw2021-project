package it.polimi.ingsw.server.controller;

import com.google.gson.*;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.events.send.GameStartedEvent;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.personal.VirtualView;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.server.utils.EventsForClientObserver;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the component Controller of the Pattern MVC,
 * it's purpose is to receive the actions of the Players and changes the State of the Model.
 * It is Observer of the Events send by the Virtual Views.
 */
public class Controller implements EventsForClientObserver {

    private final ModelInterface modelInterface;
    private List<String> nicknames;
    private final List<VirtualView> virtualViews;

    /**
     * This constructor firstly checks if the new Players are the same of the previous Game
     * saved in the gameSaved.json Json File, if yes it reloads the information written in the Json Files
     * otherwise it creates a new Game and it resets the Json Files.
     * In both cases it also set all the VirtualViews as observers of the model.
     *
     * @param virtualViews the list of the Virtual Views of the Players
     */
    public Controller(List<VirtualView> virtualViews) {
        this.virtualViews = virtualViews;
        this.nicknames = virtualViews.stream().map(VirtualView::getNickname).collect(Collectors.toList());
        if (nicknamesMatchSavedGame()) {
            this.modelInterface = new ModelInterface(nicknames);
            reloadGame();
            startSetup();
            reDoSavedActions();
            setupObservers(virtualViews);
            //use reconnect event to send every graphic information to every player
            for (String nickname : nicknames) {
                modelInterface.reconnectPlayer(nickname);
                updateSavedGame(new ReconnectEventFromClient(nickname));
            }
            Lobby.getLobby().broadcastMessage("Loaded an existing game");
            modelInterface.sendNecessaryEvents();

        } else {
            Collections.shuffle(nicknames);
            this.modelInterface = new ModelInterface(nicknames);
            resetGameDataFile();
            setupObservers(virtualViews);
            startSetup();
        }
    }

    /**
     * This function is used to register all the virtualViews as observers of the model
     *
     * @param virtualViews virtualViews to set as observers
     */
    private void setupObservers(List<VirtualView> virtualViews) {
        virtualViews.forEach(modelInterface::registerObserver);
        virtualViews.forEach(virtualView -> virtualView.registerObserver(this));
    }

    /**
     * This method notifies all players that the game is starting
     */
    private void startSetup() {
        //notify players of game starting
        modelInterface.notifyObservers(new GameStartedEvent(nicknames));
        modelInterface.startSetup();
    }

    /**
     * This method reset the Game Data saved in the gameSaved.json Json File
     * and saves the state of the Market Tray in the initialMarketState.json json File
     * and the state of the Development Cards Grid in the initialGridState.json json File
     */
    private void resetGameDataFile() {
        FileUtilities.resetGameData(nicknames);
        modelInterface.saveMarketAndGridData();
    }

    /**
     * This method loads the state of the Market Tray and the state of the Development Cards Grid from the Json Files
     */
    private void reloadGame() {
        modelInterface.loadMarketAndGridAndDeckLeaderData();
    }

    /**
     * This method read and performs all the actions written in the Json File gameSaved.json
     */
    private void reDoSavedActions() {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.SAVED_GAME_PATH);
        if(fileElement != null) {
            JsonArray jsonArrayOfActions = Parser.extractFromField(fileElement, "actions").getAsJsonArray();
            for (JsonElement element : jsonArrayOfActions) {
                try {
                    Objects.requireNonNull(ServerParser.getEventFromClient(element)).doAction(modelInterface);
                } catch (InvalidSetupException | InvalidIndexException | NonStorableResourceException | EmptySlotException | NonAccessibleSlotException | InvalidEventException ignored) {
                }
            }
        }
    }

    /**
     * This method checks if the new Players have the same nickname of that saved in the Json File
     *
     * @return true if the Players are the same, false otherwise
     */
    private boolean nicknamesMatchSavedGame() {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.SAVED_GAME_PATH);
        if(fileElement != null) {
            JsonArray jsonArrayOfNicknames = Parser.extractFromField(fileElement, "players").getAsJsonArray();
            List<String> savedNicks = new ArrayList<>();
            jsonArrayOfNicknames.forEach(jEl -> savedNicks.add(jEl.getAsString()));
            if (savedNicks.size() > 1 && savedNicks.size() == nicknames.size() && savedNicks.containsAll(nicknames)) {
                //reset turn order
                this.nicknames = savedNicks;
                return true;
            }
            return false;
        }
        return false;
    }


    /**
     * This method is called by the VirtualView to notify this class
     * of an Event coming from the Client.
     * If the owner is the currently playing player the action is performed
     * If the action is not performed successfully the last event sent to the client is resented
     *
     * @param eventFromClient the Event from the Client
     */
    @Override
    public synchronized void update(EventFromClient eventFromClient) {
        ClientHandler currentClientHandler = Objects.requireNonNull(virtualViews.stream().filter(v -> v.getNickname().equals(eventFromClient.getNickname())).findFirst().orElse(null)).getClientHandler();
        if (eventFromClient.canBeExecutedFor(modelInterface.getCurrentPlayerNickname())) {
            try {
                updateSavedGame(eventFromClient);
                eventFromClient.doAction(modelInterface);
            } catch (InvalidIndexException | NonStorableResourceException | EmptySlotException | NonAccessibleSlotException | InvalidEventException e) {
                currentClientHandler.sendErrorMessage(e.getMessage());
                //if exception was created by a choice re send choice event
                modelInterface.reSendLastEvent();
            } catch (InvalidSetupException ex) {
                //if exception was created by a failed setup event resend setup choice event
                currentClientHandler.sendErrorMessage(ex.getMessage());
                modelInterface.reSendSetup(eventFromClient.getNickname());
            }
        } else
            currentClientHandler.sendErrorMessage("It's not your turn!");
    }

    /**
     * Method used to set a player as offline
     *
     * @param nickname of the player offline
     * @return true if the player was the last one online
     */
    public synchronized boolean disconnectPlayer(String nickname) {
        updateSavedGame(new DisconnectEventFromClient(nickname));
        if (modelInterface.disconnectPlayer(nickname)) {
            //invalidate gameSaved data
            FileUtilities.resetGameData(new ArrayList<>());
            return true;
        }
        return false;
    }

    /**
     * Method used to set a player as online after a reconnection
     *
     * @param nickname of the player reconnected
     */
    public synchronized void reconnectPlayer(String nickname) {
        updateSavedGame(new ReconnectEventFromClient(nickname));
        modelInterface.reconnectPlayer(nickname);
    }

    /**
     * This method is used to cheat 5 resources to each player during the game demo
     */
    public void cheat() {
        modelInterface.cheat();
        updateSavedGame(new CheatEventFromClient("_____admin_____"));
    }

    /**
     * Saves the latest completed action as a jsonObject in a jsonArray in a file
     *
     * @param eventFromClient the completed action
     */
    private void updateSavedGame(EventFromClient eventFromClient) {
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.getSavedGamePath());
        if(fileElement != null) {
            JsonArray jsonArrayOfInstructions = Parser.extractFromField(fileElement, "actions").getAsJsonArray();
            jsonArrayOfInstructions.add(Parser.toJsonTree(eventFromClient));
            FileUtilities.writeJsonElementInFile(fileElement, FileUtilities.getSavedGamePath());
        }
    }

    public ModelInterface getModelInterfaceForTesting(){
        return modelInterface;
    }
}