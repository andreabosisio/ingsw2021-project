package it.polimi.ingsw;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.cards.CardsGenerator;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.WhiteResource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * class only used for testing
 * contains all method useful to create a game with no rng involved
 */

public class TestGameGenerator {
    /**
     * create a new game with this structure:
     * 4 players named: first, second, third, fourth
     *
     * @param auto true if you want everything auto
     *             false if you want to call settings after with your parameters
     *             settingsToCall: {  setLeaderInHand();
     *             setMarketTray();
     *             setDevelopmentCardsGrid();}
     * @return model prepared to start the game from player one
     */
    public ModelInterface modelInterfaceGenerator(boolean auto) {
        ModelInterface modelInterface = new ModelInterface(new ArrayList<String>() {{
            add("first");
            add("second");
            add("third");
            add("fourth");
        }});

        if (auto) {
            setLeaderInHandAuto(modelInterface);
            setMarketTrayAuto(modelInterface);
            setDevelopmentCardsGrid(modelInterface);
        }
        return modelInterface;
    }

    /**
     * set leaders in hand of all players like this
     * player1: p1,m1
     * player2: m4,w4
     * player3: w1,d1
     * player4: d4,p4
     *
     * @param modelInterface model to modify
     */
    public void setLeaderInHandAuto(ModelInterface modelInterface) {
        List<Player> players = modelInterface.getTurnLogic().getPlayers();
        List<LeaderCard> leaderCards = new CardsGenerator().generateLeaderCards();
        players.get(0).setLeaderHand(new ArrayList<LeaderCard>() {{
            add(leaderCards.get(0));
            add(leaderCards.get(4));
        }});
        players.get(1).setLeaderHand(new ArrayList<LeaderCard>() {{
            add(leaderCards.get(7));
            add(leaderCards.get(11));
        }});
        players.get(2).setLeaderHand(new ArrayList<LeaderCard>() {{
            add(leaderCards.get(8));
            add(leaderCards.get(12));
        }});
        players.get(3).setLeaderHand(new ArrayList<LeaderCard>() {{
            add(leaderCards.get(15));
            add(leaderCards.get(3));
        }});
    }

    /**
     * set leaderHand by indexes on json (0=first leader in json,15 = last leader)
     * starts from first player
     *
     * @param modelInterface model to modify
     * @param indexes        indexes of leaders to setup
     */
    public void setLeaderInHand(ModelInterface modelInterface, List<Integer> indexes) {
        List<Player> players = modelInterface.getTurnLogic().getPlayers();
        List<LeaderCard> leaderCards = new CardsGenerator().generateLeaderCards();
        int i = 0;
        for (Player p : players) {
            List<LeaderCard> hand = new ArrayList<>();
            hand.add(leaderCards.get(i));
            hand.add(leaderCards.get(i + 1));
            p.setLeaderHand(hand);
            i = i + 2;
        }
    }

    /**
     * set MarketTray like this
     * BLUE   - BLUE   - GRAY   - GRAY
     * YELLOW - YELLOW - PURPLE - PURPLE
     * RED    - WHITE  - WHITE  - WHITE
     * EXTRA: WHITE
     *
     * @param modelInterface model to modify
     */

    public void setMarketTrayAuto(ModelInterface modelInterface) {
        List<Resource> resources = new ArrayList<>();
        resources.add(new OtherResource(ResourceEnum.BLUE));
        resources.add(new OtherResource(ResourceEnum.BLUE));
        resources.add(new OtherResource(ResourceEnum.GRAY));
        resources.add(new OtherResource(ResourceEnum.GRAY));
        resources.add(new OtherResource(ResourceEnum.YELLOW));
        resources.add(new OtherResource(ResourceEnum.YELLOW));
        resources.add(new OtherResource(ResourceEnum.PURPLE));
        resources.add(new OtherResource(ResourceEnum.PURPLE));
        resources.add(new RedResource());
        resources.add(new WhiteResource());
        resources.add(new WhiteResource());
        resources.add(new WhiteResource());
        resources.add(new WhiteResource());
        GameBoard.getGameBoard().getMarketTray().setNonRandom(resources);
    }

    /**
     * set marketTray res as intended (from left to right and from top to bottom),
     * must contains 4 white, 2 yellow, 3 gray, 2 blue, 2 purple, 1 red
     * 1,2,3,4
     * 5,6,7,8
     * 9,7,11,12
     * extra=13
     *
     * @param modelInterface model to modify
     * @param resources      resources you want
     */
    public void setMarketTray(ModelInterface modelInterface, List<Resource> resources) {
        GameBoard.getGameBoard().getMarketTray().setNonRandom(resources);
    }

    /**
     * set devCardsGrid following jsonOrder:
     *
     * @param modelInterface model to modify
     */
    public void setDevelopmentCardsGrid(ModelInterface modelInterface) {
        GameBoard.getGameBoard().getDevelopmentCardsGrid().setNonRandom();
    }

    /**
     * prepare player by giving him the resources he needs to buy a devCard
     * adds them in his strongbox
     *
     * @param modelInterface model to modify
     * @param playerIndex index of player to prepare(first player = 0)
     * @param developmentCard devCard to prepare for
     */
    public void preparePlayerForDevCard(ModelInterface modelInterface, int playerIndex, DevelopmentCard developmentCard){
        Player player = modelInterface.getTurnLogic().getPlayers().get(playerIndex);
        player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(developmentCard.getPrice());
    }

    @Test
    void modelInterfaceTest() {
        ModelInterface modelInterface = modelInterfaceGenerator(true);
        //GameBoard.getGameBoard().getMarketTray().print();
        //GameBoard.getGameBoard().getDevelopmentCardsGrid().print();
    }
}