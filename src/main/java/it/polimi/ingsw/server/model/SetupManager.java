package it.polimi.ingsw.server.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.send.SetupSendEvent;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.NonStorableResources;
import it.polimi.ingsw.server.model.resources.ResourceFactory;
import it.polimi.ingsw.server.model.resources.StorableResource;
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
    private void startSetup(){
        GameBoard.getGameBoard().reset();
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
                .orElseThrow(InvalidEventException::new);

        //chosen leader cards must be two different cards
        Set<Integer> chosenIndexes = leaderCardIndexes.stream().filter(index -> index <= 3 && index >= 0).collect(Collectors.toSet());

        if(setupSendEvent.getNumberOfResources() == resources.size() && chosenIndexes.size() == 2) {
            List<Resource> chosenSetupResources = new ArrayList<>();
            for(String chosenColor : resources) {
                try {
                    ResourceEnum chosenEnum = ResourceEnum.valueOf(chosenColor.toUpperCase());
                    chosenSetupResources.add(new ResourceFactory().produceResource(chosenEnum)); //throws NonStorableResourceException if RED or WHITE
                } catch (IllegalArgumentException e) {
                    throw new InvalidEventException(); //non existing resource type
                }
            }

            Player currentSetupPlayer = modelInterface.getTurnLogic().getPlayers().stream()
                    .filter(player -> player.getNickname().equals(nickname)).findFirst()
                    .orElseThrow(InvalidEventException::new);

            //add the chosen resources to the warehouse
            try {
                currentSetupPlayer.getPersonalBoard().getWarehouse().setupWarehouse(chosenSetupResources);
            } catch (InvalidIndexException | EmptySlotException | NonAccessibleSlotException e) {
                e.printStackTrace();
                throw new InvalidEventException();
            }

            //second and third player receive an extra Faith Point
            if (modelInterface.getTurnLogic().getPlayers().indexOf(currentSetupPlayer) >= 2)
                GameBoard.getGameBoard().faithProgress(currentSetupPlayer, 1);

            setupSendEvents.remove(setupSendEvent);
            if (setupSendEvents.size() == 0) {
                return true; //remove
                // todo: all the players receive an update event with the gameboard
            }
            return true;
        }
        //todo: throw InvalidEventException invece che false?
        return false;
    }
}