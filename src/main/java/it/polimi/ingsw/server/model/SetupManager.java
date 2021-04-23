package it.polimi.ingsw.server.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.events.send.SetupSendEvent;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.NonStorableResources;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SetupManager {
    private int res = 2110;
    private final List<Player> players;
    private final ModelInterface modelInterface;
    private final List<SetupSendEvent> setupSendEvents = new ArrayList<>();

    public SetupManager(List<Player> players, ModelInterface modelInterface) {
        this.players = players;
        //startSetup();
        this.modelInterface = modelInterface;
    }

    public void startSetup(){
        GameBoard.getGameBoard().reset();
        for(Player player:players) {
            int numberOfRes = res%10;
            res = res/10;
            List<LeaderCard> leaderDrawn;
            leaderDrawn = GameBoard.getGameBoard().drawLeader();
            SetupSendEvent setupSendEvent = new SetupSendEvent(player.getNickName(),leaderDrawn,numberOfRes);
            setupSendEvents.add(setupSendEvent);
            modelInterface.notifyObservers(setupSendEvent);
        }
    }

    public boolean choose(String nickname,List<Integer> leaderCardIndexes,List<String> resources) throws InvalidEventException, NonStorableResourceException, InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        SetupSendEvent setupSendEvent = setupSendEvents.stream().filter(setupEvent -> setupEvent.getNickName().equals(nickname)).
                findFirst().orElseThrow(InvalidEventException::new);

        //chosen leader cards must be two different cards
        Set<Integer> chosenIndexes = leaderCardIndexes.stream().filter(index -> index <= 3 && index >= 0).collect(Collectors.toSet());

        if(setupSendEvent.getNumberOfResources() == resources.size() && chosenIndexes.size() == 2) {
            List<Resource> chosenSetupResources = new ArrayList<>();
            for(String chosenColor : resources) {
                try {
                    ResourceEnum chosenEnum = ResourceEnum.valueOf(chosenColor.toUpperCase());
                    if(NonStorableResources.getNonStorableResourcesEnum().contains(chosenEnum)){
                        throw new NonStorableResourceException(); //invalid type of chosen resources (RED, WHITE)
                    }
                    chosenSetupResources.add(new OtherResource(chosenEnum));
                } catch (IllegalArgumentException e) {
                    throw new InvalidEventException(); //non existing resource type
                }
            }

            Player currentSetupPlayer = modelInterface.getTurnLogic().getPlayers().stream().
                    filter(player -> player.getNickName().equals(nickname)).findFirst().orElseThrow(InvalidEventException::new);

            //add the chosen resources to the warehouse
            currentSetupPlayer.getPersonalBoard().getWarehouse().setupWarehouse(chosenSetupResources);

            //second and third player receive an extra Faith Point
            if (modelInterface.getTurnLogic().getPlayers().indexOf(currentSetupPlayer) >= 2)
                GameBoard.getGameBoard().faithProgress(currentSetupPlayer, 1);

            setupSendEvents.remove(setupSendEvent);
            if (setupSendEvents.size() == 0)
                // todo: all the players receive an update event with the gameboard
            return true;
        }
        return false;
    }
}