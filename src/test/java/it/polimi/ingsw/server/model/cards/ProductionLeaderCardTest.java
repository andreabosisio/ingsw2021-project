package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductionLeaderCardTest {
    CardsGenerator leaderCardGenerator = new CardsGenerator();
    ProductionLeaderCard leaderCard;

    @Test
    void canDoProductionTest() throws NonStorableResourceException {
        leaderCard = (ProductionLeaderCard) leaderCardGenerator.generateLeaderCards().get(0);

        List<Resource> correctDesiredResources = new ArrayList<Resource>(){{
           add(new OtherResource(ResourceEnum.YELLOW));
           add(new OtherResource(ResourceEnum.BLUE));
        }};
        List<Resource> wrongDesiredResources = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.PURPLE));
            add(new OtherResource(ResourceEnum.BLUE));
        }};
        List<Resource> nonStorableDesiredResources = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.PURPLE));
            add(new RedResource());
        }};
        List<Resource> insufficientDesiredResources = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.BLUE));
        }};
        List<Resource> anotherCorrectDesiredResources = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.YELLOW));
            add(new OtherResource(ResourceEnum.PURPLE));
        }};

        assertTrue(leaderCard.canDoProduction(correctDesiredResources));

        assertFalse(leaderCard.canDoProduction(wrongDesiredResources));

        try {
            leaderCard.canDoProduction(nonStorableDesiredResources);
        }catch (NonStorableResourceException e){
            assertTrue(true);
        }

        assertFalse(leaderCard.canDoProduction(insufficientDesiredResources));

        assertTrue(leaderCard.canDoProduction(anotherCorrectDesiredResources));

    }
}