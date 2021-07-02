package it.polimi.ingsw.server.model;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.events.send.GameStartedEvent;
import it.polimi.ingsw.server.events.send.choice.SetupChoiceEvent;
import it.polimi.ingsw.server.events.send.graphics.*;
import it.polimi.ingsw.server.exceptions.*;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.ResourceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class has the purpose to manage the first phase of the Game: the Setup Phase of all the Players
 */
public class SetupManager {
    private final List<Integer> numberOfResourcesToChoose = new ArrayList<>() {{
        add(0);
        add(1);
        add(1);
        add(2);
    }};
    private final int numberOfLeaderCardsToChoose = 2;
    private final List<Player> players;
    private final ModelInterface modelInterface;
    private final List<SetupChoiceEvent> setupSendEvents = new ArrayList<>();

    public SetupManager(List<Player> players, ModelInterface modelInterface) {
        this.players = players;
        this.modelInterface = modelInterface;
    }

    /**
     * Reset the GameBoard and send to all the Players the LeaderCards and the number of resources to choose
     */
    public void startSetup() {
        //initial update
        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        graphicUpdateEvent.addUpdate(new MarketUpdate());
        graphicUpdateEvent.addUpdate(new GridUpdate());
        modelInterface.notifyObservers(graphicUpdateEvent);

        int i = 0;
        for (Player player : players) {
            List<LeaderCard> drawnLeaderCards = GameBoard.getGameBoard().draw4LeaderCards();
            SetupChoiceEvent setupSendEvent = new SetupChoiceEvent(player.getNickname(), drawnLeaderCards, numberOfResourcesToChoose.get(i));
            setupSendEvents.add(setupSendEvent);
            modelInterface.notifyObservers(setupSendEvent);
            i++;
        }
    }

    /**
     * Add the chosen LeaderCards and Resources to the Player's board in the setup phase.
     *
     * @param nickname          of the Player
     * @param leaderCardIndexes of the chosen LeaderCards by the Player
     * @param resources         chosen by the Player
     * @return true if the choices are correct
     * @throws InvalidEventException if the choices aren't correct
     * @throws InvalidSetupException if the setup failed
     */
    public boolean setupAction(String nickname, List<Integer> leaderCardIndexes, List<String> resources) throws InvalidEventException, InvalidSetupException {
        SetupChoiceEvent setupSendEvent = setupSendEvents.stream().filter(setupEvent -> setupEvent.getNickname().equals(nickname)).findFirst()
                .orElseThrow(() -> new InvalidEventException("Setup choose already done!"));

        if (leaderCardIndexes == null || resources == null)
            throw new InvalidSetupException("Null parameters are not permitted");

        //chosen leader cards must be two different cards
        Set<Integer> chosenIndexes = leaderCardIndexes.stream().filter(index -> index <= 3 && index >= 0).collect(Collectors.toSet());

        if (setupSendEvent.getNumberOfResources() == resources.size() && chosenIndexes.size() == numberOfLeaderCardsToChoose) {
            List<Resource> chosenResources = new ArrayList<>();
            List<LeaderCard> chosenLeaderCards = new ArrayList<>();

            for (String chosenColor : resources) {
                try {
                    ResourcesEnum chosenEnum = ResourcesEnum.valueOf(chosenColor.toUpperCase());
                    chosenResources.add(ResourceFactory.produceResource(chosenEnum)); //throws NonStorableResourceException if RED or WHITE
                } catch (IllegalArgumentException | NonStorableResourceException e) {
                    throw new InvalidSetupException("Non permitted resource type"); //non existing resource type
                }
            }
            for (Integer chosenIndex : chosenIndexes) {
                chosenLeaderCards.add(setupSendEvent.getLeaderCards().get(chosenIndex));
            }

            preparePlayer(nickname, chosenLeaderCards, chosenResources);

            setupSendEvents.remove(setupSendEvent);

            initialGameSetup();
            return true;
        }
        throw new InvalidSetupException("Invalid number of chosen Resources and/or LeaderCards");
    }

    /**
     * This method is used to disconnect a player while the game is in the setup phase
     * If the server was waiting for this player setup the setup is carried out randomly
     *
     * @param nickname nickname of the player to disconnect
     * @return if the player was the last one online
     */
    public boolean disconnectPlayer(String nickname) {
        SetupChoiceEvent event = setupSendEvents.stream().filter(e -> e.getNickname().equals(nickname)).findFirst().orElse(null);
        players.stream().filter(p -> p.getNickname().equals(nickname)).forEach(p -> p.setOnline(false));

        if (event == null)
            return players.stream().noneMatch(Player::isOnline);

        if (players.stream().noneMatch(Player::isOnline))
            return true;

        List<LeaderCard> chosenLeaderCards = event.getLeaderCards().subList(0, 2);
        List<Resource> chosenResources = new ArrayList<>();

        for (int i = 0; i < event.getNumberOfResources(); i++) {
            try {
                chosenResources.add(ResourceFactory.produceResource(ResourcesEnum.values()[1]));
            } catch (NonStorableResourceException ignored) {}
        }

        try {
            preparePlayer(nickname, chosenLeaderCards, chosenResources);
        } catch (InvalidSetupException | InvalidEventException ignored) {
        }

        setupSendEvents.remove(event);
        initialGameSetup();
        return players.stream().noneMatch(Player::isOnline);
    }

    /**
     * This method updates the model with the player' choices
     *
     * @param nickname          nickname of the player to prepare
     * @param chosenLeaderCards List of the chosen leader cards
     * @param chosenResources   list of the chosen resources
     * @throws InvalidSetupException If the setup choices weren't legal
     * @throws InvalidEventException If the setup failed
     */
    private void preparePlayer(String nickname, List<LeaderCard> chosenLeaderCards, List<Resource> chosenResources) throws InvalidSetupException, InvalidEventException {
        Player currentSetupPlayer = modelInterface.getTurnLogic().getPlayers().stream()
                .filter(player -> player.getNickname().equals(nickname)).findFirst()
                .orElseThrow(() -> new InvalidEventException("Invalid nickname"));

        //add the chosen resources to the warehouse
        try {
            currentSetupPlayer.getPersonalBoard().getWarehouse().setupWarehouse(chosenResources);
        } catch (InvalidIndexException | EmptySlotException | NonAccessibleSlotException e) {
            throw new InvalidSetupException("Failed to add chosen resources"); //impossible condition
        }

        //add the chosen leader cards to player's hand
        currentSetupPlayer.setLeaderHand(chosenLeaderCards);

        //second and third player receive an extra Faith Point
        if (modelInterface.getTurnLogic().getPlayers().indexOf(currentSetupPlayer) >= 2)
            GameBoard.getGameBoard().faithProgress(currentSetupPlayer, 1);
    }

    /**
     * This method is used to check if every player has done his setup action, if so the game starts and every player is updated.
     */
    private void initialGameSetup() {
        if (setupSendEvents.size() == 0) {
            //set turnLogic state from (idleState where very action is invalidEvent) to startTurn
            modelInterface.setCurrentState(modelInterface.getStartTurn());

            //all the players receive an update event with the gameBoard
            GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
            graphicUpdateEvent.addUpdate(new FaithTracksUpdate());
            for (Player player : modelInterface.getTurnLogic().getPlayers())
                graphicUpdateEvent.addUpdate(new PersonalBoardUpdate(player, new LeaderCardSlotsUpdate(), new ProductionSlotsUpdate(), new WarehouseUpdate()));
            modelInterface.notifyObservers(graphicUpdateEvent);
            modelInterface.getTurnLogic().setNextPlayer();
        }
    }

    /**
     * This method is used to reconnect a player while the model is in the setupPhase
     *
     * @param nickname nickname of the player to reconnect
     */
    public void reconnectPlayer(String nickname) {
        players.stream().filter(p -> p.getNickname().equals(nickname)).forEach(p -> p.setOnline(true));
        modelInterface.notifyObservers(new GameStartedEvent(players.stream().map(Player::getNickname).collect(Collectors.toList())));
        GraphicUpdateEvent graphicUpdateEvent = new GraphicUpdateEvent();
        graphicUpdateEvent.addUpdate(new MarketUpdate());
        graphicUpdateEvent.addUpdate(new GridUpdate());
        modelInterface.notifyObservers(graphicUpdateEvent);
    }

    /**
     * Resend all the setup events to the player that still didn't complete them
     */
    public void reSendAllPendingSetupEvents() {
        for (SetupChoiceEvent event : setupSendEvents) {
            modelInterface.notifyObservers(event);
        }
    }

    /**
     * Resend the personal setup event to a specified player
     *
     * @param nickname nickname of the player to resend the setup to
     */
    public void resendSetupEventFor(String nickname) {
        for (SetupChoiceEvent event : setupSendEvents) {
            if (event.getNickname().equals(nickname)) {
                modelInterface.notifyObservers(event);
            }
        }
    }
}