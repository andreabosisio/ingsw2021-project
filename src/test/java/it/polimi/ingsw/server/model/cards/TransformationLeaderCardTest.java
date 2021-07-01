package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.resources.WhiteResource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransformationLeaderCardTest {
    final List<LeaderCard> leaderCards = new CardsGenerator().generateLeaderCards();
    final List<String> nickname = new ArrayList<>() {{
        add("Ubaldo");
    }};
    final ModelInterface modelInterface = new ModelInterface(nickname);
    WhiteResource whiteResource;


    @Test
    void doTransformation() {
        TransformationLeaderCard leaderCard = (TransformationLeaderCard) leaderCards.get(4);
        List<Resource> correctPossibleTransformation = new ArrayList<>();
        whiteResource = new WhiteResource();

        assertEquals(whiteResource.getPossibleTransformations(), correctPossibleTransformation); //no available transformation
        assertTrue(leaderCard.doTransformation(whiteResource));
        correctPossibleTransformation.add(new StorableResource(ResourcesEnum.PURPLE));
        assertEquals(whiteResource.getPossibleTransformations(), correctPossibleTransformation);

        TransformationLeaderCard anotherLeaderCard = (TransformationLeaderCard) leaderCards.get(5);
        assertTrue(anotherLeaderCard.doTransformation(whiteResource));
        correctPossibleTransformation.add(new StorableResource(ResourcesEnum.BLUE));
        assertEquals(whiteResource.getPossibleTransformations(), correctPossibleTransformation);

        whiteResourceMarketAbility(leaderCard, anotherLeaderCard);

     }

    void whiteResourceMarketAbility(LeaderCard first, LeaderCard second) {

        List<Resource> correctTempNewResources = new ArrayList<>();

        //transformation with one leader card activated
        first.activate(modelInterface.getTurnLogic().getCurrentPlayer());
        assertTrue(new WhiteResource().marketAbility(modelInterface.getTurnLogic()));
        correctTempNewResources.add(new StorableResource(ResourcesEnum.PURPLE));
        assertEquals(GameBoard.getGameBoard().getMarketTray().getTempNewResources(), correctTempNewResources);



        //transformation with two leader card activated
        second.activate(modelInterface.getTurnLogic().getCurrentPlayer());
        assertTrue(new WhiteResource().marketAbility(modelInterface.getTurnLogic()));
        correctTempNewResources.add(new StorableResource(ResourcesEnum.BLUE));
        assertEquals(whiteResource.getPossibleTransformations(), correctTempNewResources);
        assertEquals(modelInterface.getTurnLogic().getWhiteResourcesFromMarket(), new ArrayList<Resource>(){{
            add(whiteResource);
        }});

    }
}