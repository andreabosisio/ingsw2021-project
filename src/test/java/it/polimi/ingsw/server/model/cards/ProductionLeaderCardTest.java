package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductionLeaderCardTest {
    List<String> nicknames = new ArrayList<String>(){{
        add("Andrea");
        add("Marco");
        add("Matteo");
    }};
    ModelInterface modelInterface = new ModelInterface(nicknames);
    CardsGenerator leaderCardGenerator = new CardsGenerator();
    ProductionLeaderCard leaderCard;

    @Test
    void canDoProductionTest() {
        leaderCard = (ProductionLeaderCard) leaderCardGenerator.generateLeaderCards().get(0);

        List<Resource> correctDesiredResources = new ArrayList<Resource>(){{
           add(new StorableResource(ResourceEnum.YELLOW));
           add(new StorableResource(ResourceEnum.BLUE));
        }};
        List<Resource> wrongDesiredResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.PURPLE));
            add(new StorableResource(ResourceEnum.BLUE));
        }};
        List<Resource> nonStorableDesiredResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.PURPLE));
            add(new RedResource());
        }};
        List<Resource> insufficientDesiredResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.BLUE));
        }};
        List<Resource> anotherCorrectDesiredResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.PURPLE));
        }};

        assertTrue(leaderCard.canDoProduction(correctDesiredResources));

        usePowerTest(leaderCard);

        assertFalse(leaderCard.canDoProduction(wrongDesiredResources));

        //assertThrows(NonStorableResourceException.class, () -> leaderCard.canDoProduction(nonStorableDesiredResources));

        assertFalse(leaderCard.canDoProduction(insufficientDesiredResources));

        assertTrue(leaderCard.canDoProduction(anotherCorrectDesiredResources));

        usePowerTest(leaderCard);

    }

    void usePowerTest(ProductionLeaderCard leaderCard){
        List<Resource> correctOutResources = new ArrayList<Resource>(){{
            add(new RedResource());
        }};
        List<Resource> correctInResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
        }};
        leaderCard.usePower(modelInterface.getTurnLogic());
        assertEquals(leaderCard.getOutResources(), correctOutResources);
        assertEquals(leaderCard.getInResources(), correctInResources);
    }
}