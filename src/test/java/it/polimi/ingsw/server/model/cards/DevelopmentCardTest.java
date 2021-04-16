package it.polimi.ingsw.server.model.cards;


import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardTest {

    CardsGenerator devCardGenerator = new CardsGenerator();
    ModelInterface modelInterface = new ModelInterface();
    TurnLogic turnLogic = modelInterface.getTurnLogic();
    List<DevelopmentCard> devCards = devCardGenerator.generateDevelopmentCards();
    DevelopmentCard devCard;

    /**
     * Verify that the outResources of the card aren't mutable
     */
    @Test
     void getOutResourcesTest () {
        List<Resource> getOutResources = devCards.get(0).getOutResources();
        getOutResources.remove(0);
        assertFalse(devCards.get(0).getOutResources().isEmpty());
    }

    @Test
     void simplyCanDoProductionTest() {
        devCard = devCards.get(0);
        List<Resource> correctResources = new ArrayList<>();
        List<Resource> insufficientResources = new ArrayList<>();
        List<Resource> incorrectResources = new ArrayList<>();

        correctResources.add(new OtherResource(ResourceEnum.GRAY));
        correctResources.add(new OtherResource(ResourceEnum.GRAY));

        insufficientResources.add(new OtherResource(ResourceEnum.GRAY));

        incorrectResources.add(new OtherResource(ResourceEnum.PURPLE));
        incorrectResources.add(new OtherResource(ResourceEnum.GRAY));


        assertTrue(devCard.canDoProduction(correctResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(insufficientResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(incorrectResources) && !devCard.getInResources().isEmpty());
    }

    @Test
    void simplyCanDoProductionTest2() {
        devCard = devCards.get(8);
        List<Resource> correctResources = new ArrayList<>();
        List<Resource> insufficientResources = new ArrayList<>();
        List<Resource> incorrectResources = new ArrayList<>();

        correctResources.add(new OtherResource(ResourceEnum.GRAY));
        correctResources.add(new OtherResource(ResourceEnum.BLUE));

        insufficientResources.add(new OtherResource(ResourceEnum.BLUE));

        incorrectResources.add(new OtherResource(ResourceEnum.PURPLE));
        incorrectResources.add(new OtherResource(ResourceEnum.BLUE));


        assertTrue(devCard.canDoProduction(correctResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(insufficientResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(incorrectResources) && !devCard.getInResources().isEmpty());

        //devCards.get(8).getPrice().remove(0);
    }

    @Test
    void getterTest(){
            devCard = devCards.get(19);
        List<Resource> correctPrice = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.BLUE));
            add(new OtherResource(ResourceEnum.BLUE));
        }};
        List<Resource> correctInResources = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.YELLOW));
        }};
        List<Resource> correctOutResources = new ArrayList<Resource>(){{
            add(new RedResource());
        }};
        assertEquals(correctPrice, devCard.getPrice());
        assertEquals(CardColorEnum.GREEN, devCard.getColor());
        assertTrue(devCard.getLevel() == 1);
        assertTrue(devCard.getPoints() == 1);
        assertTrue(devCard.getOutResources().equals(correctOutResources) && devCard.getInResources().equals(correctInResources));
    }

    @Test
    void usePowerTest(){
        devCard = devCards.get(9);
        List<Resource> correctOutResources = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.PURPLE));
            add(new OtherResource((ResourceEnum.PURPLE)));
            add(new RedResource());
        }};
        assertEquals(correctOutResources, devCard.getOutResources());
        assertTrue(devCard.usePower(turnLogic));
    }

    //@Test
    //void buyCardTest(){

    //}


    //@Test
     /*void moreCanDoProductionTest() {
        ProductionCard devCard = createDevCard();
        List<Resource> plusResources = new ArrayList<>();
        List<Resource> emptyResources = new ArrayList<>();

        plusResources.add(new OtherResource(ResourceEnum.BLUE));
        plusResources.add(new OtherResource(ResourceEnum.PURPLE));
        plusResources.add(new OtherResource(ResourceEnum.GRAY));


        assertTrue(devCard.canDoProduction(plusResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(emptyResources) && !devCard.getInResources().isEmpty());

    }*/

    /*
    /**
     * Verify if usePower() return NEW instances of the correct resources
     */
    /*
    @Test
     void usePowerTest() {
        ProductionCard devCard = createDevCard();
        assertTrue(!devCard.usePower().isEmpty() && devCard.usePower() != devCard.getOutResources() && devCard.usePower().equals(devCard.getOutResources()));
    }*/

    //@Test
    //void buyCardTest(){
    //    DevelopmentCard devCard = createDevCard();
    //    devCard.buyCard(new Player(), )
    //}


}