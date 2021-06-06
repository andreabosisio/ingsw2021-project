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
    final List<String> nicknames = new ArrayList<>() {{
        add("Andrea");
        add("Marco");
        add("Matteo");
    }};
    final ModelInterface modelInterface = new ModelInterface(nicknames);
    final CardsGenerator leaderCardGenerator = new CardsGenerator();
    ProductionLeaderCard leaderCard;

    @Test
    void canDoProductionTest() {
        leaderCard = (ProductionLeaderCard) leaderCardGenerator.generateLeaderCards().get(0);

        List<Resource> correctDesiredResources = new ArrayList<>() {{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.BLUE));
        }};
        List<Resource> wrongDesiredResources = new ArrayList<>() {{
            add(new StorableResource(ResourceEnum.PURPLE));
            add(new StorableResource(ResourceEnum.BLUE));
        }};
        List<Resource> nonStorableDesiredResources = new ArrayList<>() {{
            add(new StorableResource(ResourceEnum.PURPLE));
            add(new RedResource());
        }};
        List<Resource> insufficientDesiredResources = new ArrayList<>() {{
            add(new StorableResource(ResourceEnum.BLUE));
        }};
        List<Resource> anotherCorrectDesiredResources = new ArrayList<>() {{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.PURPLE));
        }};

        assertTrue(leaderCard.canDoProduction(correctDesiredResources));

        usePowerTest(leaderCard, correctDesiredResources.subList(1, correctDesiredResources.size()));

        assertFalse(leaderCard.canDoProduction(wrongDesiredResources));

        //assertThrows(NonStorableResourceException.class, () -> leaderCard.canDoProduction(nonStorableDesiredResources));

        assertFalse(leaderCard.canDoProduction(insufficientDesiredResources));

        assertTrue(leaderCard.canDoProduction(anotherCorrectDesiredResources));

        usePowerTest(leaderCard, anotherCorrectDesiredResources.subList(1, anotherCorrectDesiredResources.size()));

    }

    void usePowerTest(ProductionLeaderCard leaderCard, List<Resource> correctOutResources){
        correctOutResources.add(new RedResource());
        List<Resource> correctInResources = new ArrayList<>() {{
            add(new StorableResource(ResourceEnum.YELLOW));
        }};
        leaderCard.usePower(modelInterface.getTurnLogic());
        assertTrue(correctOutResources.size() == leaderCard.getOutResources().size() && correctOutResources.containsAll(leaderCard.getOutResources()) && leaderCard.getOutResources().containsAll(correctOutResources));
        assertEquals(correctInResources, leaderCard.getInResources());
    }
}