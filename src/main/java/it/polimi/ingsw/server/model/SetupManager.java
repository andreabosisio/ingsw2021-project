package it.polimi.ingsw.server.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.send.choice.SetupChoiceEvent;
import it.polimi.ingsw.server.events.send.graphics.FaithTracksUpdate;
import it.polimi.ingsw.server.events.send.graphics.GridUpdate;
import it.polimi.ingsw.server.events.send.graphics.MarketUpdate;
import it.polimi.ingsw.server.events.send.graphics.PersonalBoardUpdate;
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
    private final List<SetupChoiceEvent> setupSendEvents = new ArrayList<>();

    public SetupManager(List<Player> players, ModelInterface modelInterface) {
        this.players = players;
        //startSetup();
        this.modelInterface = modelInterface;
    }

    /**
     * Reset the GameBoard and send to all the Players the LeaderCards and the number of resources to choose
     */
    public void startSetup(){

        //initial update
        modelInterface.notifyObservers(new MarketUpdate());
        modelInterface.notifyObservers(new GridUpdate());

        int i = 0;
        for(Player player : players) {
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
     * @param nickname of the Player
     * @param leaderCardIndexes of the chosen LeaderCards by the Player
     * @param resources chosen by the Player
     * @return true if the choices are correct
     * @throws InvalidEventException if the choices aren't correct
     * @throws NonStorableResourceException if Player choose a NonStorableResource
     */
    public boolean setupAction(String nickname, List<Integer> leaderCardIndexes, List<String> resources) throws InvalidEventException, NonStorableResourceException {
        SetupChoiceEvent setupSendEvent = setupSendEvents.stream().filter(setupEvent -> setupEvent.getNickname().equals(nickname)).findFirst()
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

                //all the players receive an update event with the gameboard
                modelInterface.notifyObservers(new FaithTracksUpdate());
                for (Player player : modelInterface.getTurnLogic().getPlayers()) {
                    modelInterface.notifyObservers(new PersonalBoardUpdate(player));
                    modelInterface.notifyObservers(new PersonalBoardUpdate(player.getNickname(), player.getPersonalBoard().getWarehouse()));
                }
            }

            return true;
        }
        throw new InvalidEventException("Invalid number of chosen Resources and/or LeaderCards");
    }
}