package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DevelopmentCard implements ProductionCard {

    private final String iD;

    private final List<Resource> price;

    private final int level;

    private final int points;

    private final CardColorEnum color;

    private final List<Resource> inResources;
    private final List<Resource> outResources;

    public DevelopmentCard(String iD, List<Resource> inResources, List<Resource> outResources, List<Resource> price, CardColorEnum color, int points, int level) {
        this.iD = iD;
        this.inResources = inResources;
        this.outResources = outResources;
        this.price = price;
        this.color = color;
        this.points = points;
        this.level = level;
    }

    /**
     * for test
     */
    public DevelopmentCard( List<Resource> inResources, List<Resource> outResources, List<Resource> price, CardColorEnum color, int points, int level) {
        this.iD = "useless";
        this.inResources = inResources;
        this.outResources = outResources;
        this.price = price;
        this.color = color;
        this.points = points;
        this.level = level;
    }

    /**
     * @return the ID of the card
     */
    @Override
    public String getID() {
        return iD;
    }

    /**
     * @return a new list that contains the resources required to buy the card
     */
    public List<Resource> getPrice() {
        return new ArrayList<>(this.price);
    }

    /**
     * @return the level of the card
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the victory points of the card
     */
    @Override
    public int getPoints() {
        return points;
    }

    /**
     * @return the color of the card
     */
    public CardColorEnum getColor() {
        return color;
    }

    /**
     * Check if the buyer can buy this card with a possible discount. If yes make the payment
     * and calls the method removeCard of the class DevelopmentCardGrid
     * to remove itself from the Grid.
     *
     * @param buyer            Player who wants to buy the card
     * @param resourcePosition Warehouse/Strongbox positions of the chosen resources
     * @param discount         Discount provided by the active TransformationLeaderCard cards
     * @return true if the buyer can buy the card
     * @throws InvalidIndexException if one the given positions is negative
     * @throws EmptySlotException if one of the chosen slots is empty
     * @throws NonAccessibleSlotException if one of the given position represent a slot that's not accessible
     */
    public boolean buyCard(Player buyer, List<Integer> resourcePosition, List<Resource> discount) throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        List<Resource> paymentResources = new ArrayList<>(discount);
        List<Resource> tempNeededResources = getPrice();
        paymentResources.addAll(buyer.getPersonalBoard().getWarehouse().getResources(resourcePosition));
        if (paymentResources.size() != tempNeededResources.size())
            return false;
        for (Resource r : paymentResources) {
            if (!tempNeededResources.contains(r)) {
                return false;
            }
            tempNeededResources.remove(r);
        }
        //remove the card from the grid
        GameBoard.getGameBoard().getDevelopmentCardsGrid().removeCard(this);

        //payment
        buyer.getPersonalBoard().getWarehouse().takeResources(resourcePosition);
        return true;
    }

    /**
     * Produce the desiredResource saved in outResources.
     *
     * @return true if the production has been applied correctly
     * @param turnLogic turn
     */
    @Override
    public boolean usePower(TurnLogic turnLogic) {
        for(Resource outResource : outResources)
            if(!outResource.productionAbility(turnLogic))
                return false;
        return true;
    }

    /**
     * Getter of the Resources required to activate the production of the card.
     *
     * @return a new list of the Resources required to activate the production of the card
     */
    @Override
    public List<Resource> getInResources() {
        return new ArrayList<>(this.inResources);
    }

    /**
     * Getter of the Resources provided by the production of the card.
     *
     * @return a new list of the Resources provided by the production of the card
     */
    @Override
    public List<Resource> getOutResources() {
        return new ArrayList<>(this.outResources);
    }

    /**
     * Check if the givenResources satisfy the production requirements of the card.
     *
     * @param givenResources from a player
     * @return true if the givenResources satisfy the production requirements of the card
     */
    @Override
    public boolean canDoProduction(List<Resource> givenResources) {

        List<Resource> tempNeededResources = getInResources();
        givenResources = givenResources.stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (givenResources.size() != tempNeededResources.size())
            return false;
        for (Resource r : givenResources) {
            if (!tempNeededResources.contains(r)) {
                return false;
            }
            tempNeededResources.remove(r);
        }
        return true;
    }

}