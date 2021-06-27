package it.polimi.ingsw.server.controller;

import com.google.gson.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.events.receive.ReceiveEvent;
import it.polimi.ingsw.server.events.receive.ReconnectEvent;
import it.polimi.ingsw.server.events.send.GameStartedEvent;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.network.personal.VirtualView;
import it.polimi.ingsw.server.utils.ReceiveObserver;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the component Controller of the Pattern MVC,
 * it's purpose is to receive the actions of the Players and changes the State of the Model.
 * It is Observer of the Events send by the Virtual Views.
 */
public class Controller implements ReceiveObserver {

    private final Map<String, Object> actionTypes = new HashMap<>() {{
        put("buyAction", BuyEvent.class);
        put("cardPlacementAction", PlaceDevelopmentCardEvent.class);
        put("setupAction", SetupEvent.class);
        put("endTurnAction", EndTurnEvent.class);
        put("leaderAction", LeaderHandEvent.class);
        put("marketAction", MarketEvent.class);
        put("productionAction", ProductionEvent.class);
        put("resourcesPlacementAction", PlaceResourcesEvent.class);
        put("transformationAction", TransformationEvent.class);
        put("connect", ReconnectEvent.class);
        put("disconnect", DisconnectEvent.class);
    }};

    private static final String SAVED_GAME_PATH = "src/main/resources/gameSaved.json";
    private ModelInterface modelInterface;
    private List<String> nicknames;
    private final Gson gson = new Gson();

    public Controller(List<VirtualView> virtualViews) {
        this.nicknames = virtualViews.stream().map(VirtualView::getNickname).collect(Collectors.toList());
        if (matchSavedGameData()) {
            this.modelInterface = new ModelInterface(nicknames);
            startSetup();
            reloadGame();
            setupObservers(virtualViews);
            //loadGraphic stuff e resend last event and sent turno di

            //todo test
            for(String nickname : nicknames){
                modelInterface.reconnectPlayer(nickname);
            }
            modelInterface.reSendLastEvent();

        } else {
            Collections.shuffle(nicknames);
            this.modelInterface = new ModelInterface(nicknames);
            resetGameDataFile();
            setupObservers(virtualViews);
            startSetup();
        }
    }

    /**
     * This function is used to register all the virtualViews as observers of the mod
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


    private void resetGameDataFile() {
        try (FileWriter file = new FileWriter(SAVED_GAME_PATH)) {
            //We can write any JSONArray or JSONObject instance to the file
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("players", gson.toJsonTree(nicknames));
            jsonObject.add("actions", new JsonArray());
            gson.toJson(jsonObject, file);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        modelInterface.saveMarketAndGridData();
    }

    private void reloadGame() {
        //this.modelInterface = new ModelInterface(nicknames);


        modelInterface.loadMarketAndGridData();
        //todo reload startingGame from saved data (remember to disconnect virtualViews)
        doSavedActions();
        //todo reAttack virtualViews
    }

    private void doSavedActions() {
        File input = new File(SAVED_GAME_PATH);
        JsonObject fileObject;
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfActions = fileObject.get("actions").getAsJsonArray();
            for (JsonElement element : jsonArrayOfActions) {
                JsonObject action = element.getAsJsonObject();
                Type eventType = (Type) actionTypes.get(action.get("type").getAsString());
                ReceiveEvent event = gson.fromJson(action, eventType);
                //do the action
                event.doAction(modelInterface);
            }

        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("error in the json file format");
            e.printStackTrace();
        }
    }

    private boolean matchSavedGameData() {
        File input = new File(SAVED_GAME_PATH);
        JsonObject fileObject;
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfNicknames = fileObject.get("players").getAsJsonArray();
            List<String> savedNicks = new ArrayList<>();
            jsonArrayOfNicknames.forEach(jEl -> savedNicks.add(jEl.getAsString()));
            if (savedNicks.size() == nicknames.size() && savedNicks.containsAll(nicknames)) {
                //reset turn order
                this.nicknames = savedNicks;
                return true;
            }

        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("error in the json file format");
            e.printStackTrace();
        }
        return false;
    }


    /**
     * This method is called by the VirtualView to notify this class
     * of an Event coming from the Client.
     * If the owner is the currently playing player the action is performed
     * If the action is not performed successfully the last event sent to the client is resented
     *
     * @param receiveEvent the Event from the Client
     */
    @Override
    public synchronized void update(ReceiveEvent receiveEvent) {

        ClientHandler currentClientHandler = Lobby.getLobby().getVirtualViewByNickname(receiveEvent.getNickname()).getClientHandler();

        if (receiveEvent.canBeExecutedFor(modelInterface.getCurrentPlayerNickname())) {
            try {
                //todo place resources action the model evolves even if the action throws an exception
                updateSavedGame(receiveEvent);
                receiveEvent.doAction(modelInterface);
            } catch (InvalidIndexException | NonStorableResourceException | EmptySlotException | NonAccessibleSlotException | InvalidEventException e) {
                currentClientHandler.sendErrorMessage(e.getMessage());
                //if exception was created by a choice re send choice event
                modelInterface.reSendLastEvent();
            } catch (InvalidSetupException ex) {
                //if exception was created by a failed setup event resend setup choice event
                currentClientHandler.sendErrorMessage(ex.getMessage());
                modelInterface.reSendSetup(receiveEvent.getNickname());
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
        updateSavedGame(new DisconnectEvent(nickname));
        return modelInterface.disconnectPlayer(nickname);
    }

    /**
     * Method used to set a player as online after a reconnection
     *
     * @param nickname of the player reconnected
     */
    public synchronized void reconnectPlayer(String nickname) {
        updateSavedGame(new ReconnectEvent(nickname));
        modelInterface.reconnectPlayer(nickname);
    }

    /**
     * This method is used to cheat 5 resources to each player during the game demo
     */
    public void cheat() {
        modelInterface.cheat();
    }

    /**
     * Saves the latest completed action as a jsonObject in a jsonArray in a file
     *
     * @param receiveEvent the completed action
     */
    private void updateSavedGame(ReceiveEvent receiveEvent) {
        File input = new File(SAVED_GAME_PATH);
        JsonObject fileObject = null;
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfInstructions = fileObject.get("actions").getAsJsonArray();
            System.out.println(jsonArrayOfInstructions);
            jsonArrayOfInstructions.add(gson.toJsonTree(receiveEvent));
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("error in the json file format");
            e.printStackTrace();
        }

        try (FileWriter file = new FileWriter(SAVED_GAME_PATH)) {
            //We can write any JSONArray or JSONObject instance to the file
            gson.toJson(fileObject, file);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}