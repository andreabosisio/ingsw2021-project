package it.polimi.ingsw;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.InvalidEventException;
import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.cards.CardsGenerator;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.WhiteResource;

import java.util.ArrayList;
import java.util.Collections;
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
        ModelInterface modelInterface = new ModelInterface(new ArrayList<>() {{
            add("first");
            add("second");
            add("third");
            add("fourth");
        }});

        //exit from idleState because there is no setUpPhase in testing
        modelInterface.setCurrentState(modelInterface.getStartTurn());

        if (auto) {
            setLeaderInHandAuto(modelInterface);
            setMarketTrayAuto();
            setDevelopmentCardsGrid();
        }
        modelInterface.getTurnLogic().setNextPlayer();
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
        players.get(0).setLeaderHand(new ArrayList<>() {{
            add(leaderCards.get(0));
            add(leaderCards.get(4));
        }});
        players.get(1).setLeaderHand(new ArrayList<>() {{
            add(leaderCards.get(7));
            add(leaderCards.get(11));
        }});
        players.get(2).setLeaderHand(new ArrayList<>() {{
            add(leaderCards.get(8));
            add(leaderCards.get(12));
        }});
        players.get(3).setLeaderHand(new ArrayList<>() {{
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
            hand.add(leaderCards.get(indexes.get(i)));
            hand.add(leaderCards.get(indexes.get(i + 1)));
            p.setLeaderHand(hand);
            i = i + 2;
        }
    }

    /**
     * Set a predefined number of Development Cards in the Player's hand.
     * Please note: the dimension of the List numberOfDevCardsForPlayer must be 4.
     *
     * @param modelInterface            model to modify
     * @param numberOfDevCardsForPlayer is a list that contains the number of Development Cards for each players.
     *                                  The index of the List represents the index of the Player to set.
     *                                  (for example List: 0, 8, 0, 0; the Player in position 1 receives 8 cards)
     */
    public void setDevCardInHand(ModelInterface modelInterface, List<Integer> numberOfDevCardsForPlayer) {
        List<DevelopmentCard> cards = GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards();

        List<DevelopmentCard> cardsLvl1 = new ArrayList<>();
        List<DevelopmentCard> cardsLvl2 = new ArrayList<>();
        List<DevelopmentCard> cardsLvl3 = new ArrayList<>();

        cards.stream().filter(card -> card.getLevel() == 1).forEach(cardsLvl1::add);
        cards.stream().filter(card -> card.getLevel() == 2).forEach(cardsLvl2::add);
        cards.stream().filter(card -> card.getLevel() == 3).forEach(cardsLvl3::add);

        Collections.shuffle(cardsLvl1);
        Collections.shuffle(cardsLvl2);
        Collections.shuffle(cardsLvl3);

        for (int indexPlayer = 0; indexPlayer < numberOfDevCardsForPlayer.size(); indexPlayer++) {
            int indexSlot = 1;
            int indexDevCardLvl = 0;
            for (int i = 0; i < numberOfDevCardsForPlayer.get(indexPlayer); i++) {
                if (i < 3) {
                    modelInterface.getTurnLogic().getPlayers().get(indexPlayer).getPersonalBoard().
                            setNewProductionCard(indexSlot, cardsLvl1.get(indexDevCardLvl));
                } else if (i == 3) {
                    indexSlot = 1;
                    indexDevCardLvl = 0;
                    modelInterface.getTurnLogic().getPlayers().get(indexPlayer).getPersonalBoard().
                            setNewProductionCard(indexSlot, cardsLvl2.get(indexDevCardLvl));
                } else if (i < 6) {
                    modelInterface.getTurnLogic().getPlayers().get(indexPlayer).getPersonalBoard().
                            setNewProductionCard(indexSlot, cardsLvl2.get(indexDevCardLvl));
                } else if (i == 6) {
                    indexSlot = 1;
                    indexDevCardLvl = 0;
                    modelInterface.getTurnLogic().getPlayers().get(indexPlayer).getPersonalBoard().
                            setNewProductionCard(indexSlot, cardsLvl3.get(indexDevCardLvl));
                } else if (i < 9) {
                    modelInterface.getTurnLogic().getPlayers().get(indexPlayer).getPersonalBoard().
                            setNewProductionCard(indexSlot, cardsLvl3.get(indexDevCardLvl));
                }
                indexDevCardLvl++;
                indexSlot++;
            }
        }
    }

    /**
     * set MarketTray like this
     * EXTRA: WHITE
     * BLUE   - BLUE   - GRAY   - GRAY
     * YELLOW - YELLOW - PURPLE - PURPLE
     * RED    - WHITE  - WHITE  - WHITE
     *
     */

    public void setMarketTrayAuto() {
        List<Resource> initResources = new ArrayList<>() {{
            add(new WhiteResource());
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.GRAY));
            add(new StorableResource(ResourcesEnum.GRAY));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.PURPLE));
            add(new StorableResource(ResourcesEnum.PURPLE));
            add(new RedResource());
            add(new WhiteResource());
            add(new WhiteResource());
            add(new WhiteResource());
        }};
        GameBoard.getGameBoard().getMarketTray().setNonRandom(initResources);
    }

    /**
     * set devCardsGrid following json file order.
     */
    public void setDevelopmentCardsGrid() {
        GameBoard.getGameBoard().getDevelopmentCardsGrid().setNonRandom();
    }

    /**
     * prepare player by giving him the resources he needs to buy a devCard
     * adds them in his strongbox
     *
     * @param modelInterface  model to modify
     * @param playerIndex     index of player to prepare(first player = 0)
     * @param developmentCard devCard to prepare for
     */
    public void preparePlayerForDevCard(ModelInterface modelInterface, int playerIndex, DevelopmentCard developmentCard) {
        Player player = modelInterface.getTurnLogic().getPlayers().get(playerIndex);
        player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(developmentCard.getPrice());
    }

    /**
     * takes a game generated by auto game and does 3 turns where current player get 1 red
     * this is achieved by picking the arrow with all white and one red balls (arrowID=2)
     *
     * @param modelInterface model interface to modify (must have marketTray autogenerated)
     */
    public void roundOfNothing(ModelInterface modelInterface) throws InvalidIndexException, InvalidEventException {
        for (int i = 0; i < 3; i++) {
            modelInterface.marketAction(2);
            modelInterface.placeResourceAction(new ArrayList<>(),true);
            modelInterface.endTurn();
        }
    }

    /**
     * prepare player by giving him the resources he needs to active
     * the production of a devCard and adds them in his strongbox
     *
     * @param modelInterface  model to modify
     * @param player          reference of the player to prepare
     * @param developmentCard devCard to prepare for
     */
    public void preparePlayerForProductionDevCard(ModelInterface modelInterface, Player player, ProductionCard developmentCard) {
        player.getPersonalBoard().getWarehouse().addResourcesToStrongBox(developmentCard.getInResources());
    }
}