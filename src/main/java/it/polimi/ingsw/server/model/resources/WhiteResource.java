package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.turn.TurnLogic;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource that can be transformed in one of the possibleTransformations resource and
 * that can't be used to make a production by the ProductionCard cards.
 * For this reason the productionAbility is not redefined.
 */
public class WhiteResource extends Resource {

    /**
     * Used to construct a white resource
     */
    public WhiteResource() {
        super(ResourcesEnum.WHITE);
    }

    /**
     * Possible transformations of this WhiteResource provided by the active TransformationLeaderCard cards
     * owned by the current player.
     */
    private final List<Resource> possibleTransformations = new ArrayList<>();

    /**
     * Method to call after a Resource has been chosen in the MarketTray.
     * Check if there are some possible transformations of this WhiteResource provided by
     * the active TransformationLeaderCard cards owned by the current player.
     * If yes save them in possibleTransformations calling doTransformation() offered by the LeaderCard cards.
     *
     * @param turn containing the current player, the current state of the game and others information
     * @return true if there are some possible transformations of this WhiteResource
     */
    @Override
    public boolean marketAbility(TurnLogic turn) {
        possibleTransformations.clear();
        for (LeaderCard leaderCard : turn.getCurrentPlayer().getPersonalBoard().getActiveLeaderCards())
            leaderCard.doTransformation(this);

        if (possibleTransformations.size() == 1) { //the only possible transformation is applied automatically
            GameBoard.getGameBoard().getMarketTray().addNewResource(new StorableResource(possibleTransformations.get(0).getColor()));
        } else if (possibleTransformations.size() == 2) { //player must choose a transformation
            turn.addWhiteResourcesFromMarketToTransform(this);
        }
        return true;
    }

    /**
     * Add a possible transformation of this WhiteResource provided by a TransformationLeaderCard activated by
     * the current player.
     *
     * @param transformation that this WhiteResource can take
     * @return true if the transformation has been correctly added to the possibleTransformations list
     */
    @Override
    public boolean addPossibleTransformation(Resource transformation) {
        return this.possibleTransformations.add(transformation);
    }

    /**
     * Get all the possible transformations
     *
     * @return the list of the possible transformation
     */
    public List<Resource> getPossibleTransformations() {
        return possibleTransformations;
    }

    /**
     * Deep copy of this White Resource.
     *
     * @return The cloned Resource
     */
    public WhiteResource deepClone() {
        WhiteResource newWhiteResource = new WhiteResource();
        possibleTransformations.forEach(t-> {
            try {
                newWhiteResource.possibleTransformations.add(ResourceFactory.produceResource(t.getColor()));
            } catch (NonStorableResourceException e) {
                e.printStackTrace();
            }
        });
        return newWhiteResource;
    }

}
