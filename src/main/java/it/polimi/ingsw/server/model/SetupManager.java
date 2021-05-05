package it.polimi.ingsw.server.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.send.SetupSendEvent;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.ResourceFactory;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



//todo STATE???
public class SetupManager {
    private final List<Integer> numberOfResourcesToChoose = new ArrayList<Integer>(){{
        add(0);
        add(1);
        add(1);
        add(2);
    }};
    private final int numberOfLeaderCardsToChoose = 2;
    private final List<Player> players;
    private final ModelInterface modelInterface;
    private final List<SetupSendEvent> setupSendEvents = new ArrayList<>();

    public SetupManager(List<Player> players, ModelInterface modelInterface) {
        this.players = players;
        //startSetup();
        this.modelInterface = modelInterface;
    }

    /**
     * Reset the GameBoard and send to all the Players the LeaderCards and the number of resources to choose
     */
    public void startSetup(){
        int i = 0;
        for(Player player : players) {
            List<LeaderCard> drawnLeaderCards = GameBoard.getGameBoard().draw4LeaderCards();
            SetupSendEvent setupSendEvent = new SetupSendEvent(player.getNickname(), drawnLeaderCards, numberOfResourcesToChoose.get(i));
            setupSendEvents.add(setupSendEvent);
            modelInterface.notifyObservers(setupSendEvent);
            i++;
        }
    }

    /**
     * Add the chosen LeaderCards and Resources to the Player's board in the setup phase.
     *
     * @param nickname of the Player
     * @param leaderCardIndexes of the chosen LeaderCards by the Player
     * @param resources chosen by the Player
     * @return true if the choices are correct
     * @throws InvalidEventException if the choices aren't correct
     * @throws NonStorableResourceException if Player choose a NonStorableResource
     */
    public boolean setupAction(String nickname, List<Integer> leaderCardIndexes, List<String> resources) throws InvalidEventException, NonStorableResourceException {
        SetupSendEvent setupSendEvent = setupSendEvents.stream().filter(setupEvent -> setupEvent.getNickname().equals(nickname)).findFirst()
                .orElseThrow(() -> new InvalidEventException("Setup choose already done!"));

        //chosen leader cards must be two different cards
        Set<Integer> chosenIndexes = leaderCardIndexes.stream().filter(index -> index <= 3 && index >= 0).collect(Collectors.toSet());

        if(setupSendEvent.getNumberOfResources() == resources.size() && chosenIndexes.size() == numberOfLeaderCardsToChoose) {
            List<Resource> chosenResources = new ArrayList<>();
            List<LeaderCard> chosenLeaderCards = new ArrayList<>();

            for(String chosenColor : resources) {
                try {
                    ResourceEnum chosenEnum = ResourceEnum.valueOf(chosenColor.toUpperCase());
                    chosenResources.add(new ResourceFactory().produceResource(chosenEnum)); //throws NonStorableResourceException if RED or WHITE
                } catch (IllegalArgumentException e) {
                    throw new InvalidEventException("Non existing resource type"); //non existing resource type
                }
            }
            for(Integer chosenIndex : leaderCardIndexes){
                chosenLeaderCards.add(setupSendEvent.getLeaderCards().get(chosenIndex));
            }

            Player currentSetupPlayer = modelInterface.getTurnLogic().getPlayers().stream()
                    .filter(player -> player.getNickname().equals(nickname)).findFirst()
                    .orElseThrow(() -> new InvalidEventException("Invalid nickname"));

            //add the chosen resources to the warehouse
            try {
                currentSetupPlayer.getPersonalBoard().getWarehouse().setupWarehouse(chosenResources);
            } catch (InvalidIndexException | EmptySlotException | NonAccessibleSlotException e) {
                throw new InvalidEventException(); //impossible condition
            }

            //add the chosen leader cards to player's hand
            currentSetupPlayer.setLeaderHand(chosenLeaderCards);

            //second and third player receive an extra Faith Point
            if (modelInterface.getTurnLogic().getPlayers().indexOf(currentSetupPlayer) >= 2)
                GameBoard.getGameBoard().faithProgress(currentSetupPlayer, 1);

            setupSendEvents.remove(setupSendEvent);
            if (setupSendEvents.size() == 0) {
                //set turnLogic state from (idleState where very action is invalidEvent) to startTurn
                modelInterface.getTurnLogic().setCurrentState(modelInterface.getTurnLogic().getStartTurn());
                // todo: all the players receive an update event with the gameboard
            }

            //todo this is for testing!!
            /*
            JsonObject info = new JsonObject();
            info.addProperty("playerNick", currentSetupPlayer.getNickname());
            info.addProperty("leaderHand", new Gson().toJson(currentSetupPlayer.getLeaderHand().stream().map(LeaderCard::getID).collect(Collectors.toList())));
            info.addProperty("leaderActive", new Gson().toJson(currentSetupPlayer.getPersonalBoard().getActiveLeaderCards().stream().map(LeaderCard::getID).collect(Collectors.toList())));
            info.addProperty("warehouse", new Gson().toJson(currentSetupPlayer.getPersonalBoard().getWarehouse().getAllResources()));
            info.addProperty("faithTrack", GameBoard.getGameBoard().getFaithTrackOfPlayer(currentSetupPlayer).getFaithMarker());
            modelInterface.getVirtualViewByNickname(nickname).getPlayerData().getClientHandler()
                    .sendJsonMessage(info.toString());
            */

            return true;
        }
        throw new InvalidEventException("Invalid number of chosen Resources and/or LeaderCards");
    }
}