package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.commons.enums.NonStorableResourcesEnum;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MarketTrayTest {

    ModelInterface modelInterface = new ModelInterface(new ArrayList<>() {{
        add("pepo");
    }});
    MarketTray market = GameBoard.getGameBoard().getMarketTray();

    @Test
    void multipleTakeResourcesTest() throws InvalidIndexException {
        int random;
        for(int i = 0; i < 20; i++ ) {
            random = getRandomInt(0, 4);
            if(random == 3){
                modelInterface = new ModelInterface(new ArrayList<>() {{
                    add("pepo");
                }});
                market = GameBoard.getGameBoard().getMarketTray();
            }

            if(random % 2 == 0)
                takeColumnTest();
            else
                takeRowTest();
        }
    }

    @Test
    void takeColumnTest() throws InvalidIndexException {

        //market = GameBoard.getGameBoard().getMarketTray();
        //modelInterface = new ModelInterface();


        Resource[][] correctMarket = new Resource[market.getNUM_R()][market.getNUM_C()];
        Resource correctExtraSlot = null;
        List<Resource> correctTakeResources = new ArrayList<>();
        //market.printMarket();

        int arrow = getRandomInt(3, 7);

        for (int i = 0; i < market.getNUM_R(); i++) {
            for (int j = 0; j < market.getNUM_C(); j++) {
                if (j != 6 - arrow)
                    correctMarket[i][j] = market.getMarketBoard()[i][j];
                else {
                    if(!NonStorableResourcesEnum.getAsList().contains(market.getMarketBoard()[i][j].getColor()))
                        correctTakeResources.add(market.getMarketBoard()[i][j]);
                    if (i < market.getNUM_R() - 1)
                        correctMarket[i][j] = market.getMarketBoard()[i + 1][j];
                    else
                        correctMarket[i][j] = market.getExtraSlot();
                }
            }
            correctExtraSlot = market.getMarketBoard()[0][6 - arrow];
        }
        assertEquals(market.takeResources(arrow), correctTakeResources);
        //market.printMarket(correctMarket, correctExtraSlot);
        //market.printMarket();

        List<String> resultToStringList = market.toStringList();

        assertEquals(correctExtraSlot , market.getExtraSlot());
        assertNotNull(correctExtraSlot);
        assertEquals(resultToStringList.get(0), correctExtraSlot.getColor().toString());
        for (int i = 0; i < market.getNUM_R(); i++)
            for (int j = 0; j < market.getNUM_C(); j++){
                assertEquals(correctMarket[i][j] , market.getMarketBoard()[i][j]);
                assertEquals(correctMarket[i][j].getColor().toString() , market.getMarketBoard()[i][j].getColor().toString());
            }

        correctTakeResources.clear();
        market.setTempNewResources(new ArrayList<>());
        //GameBoard.getGameBoard().reset();
    }

    @Test
    void takeRowTest() throws InvalidIndexException {


        //modelInterface = new ModelInterface();
        //market = GameBoard.getGameBoard().getMarketTray();
        Resource[][] correctMarket = new Resource[market.getNUM_R()][market.getNUM_C()];
        Resource correctExtraSlot = null;
        List<Resource> correctTakeResources = new ArrayList<>();
        //market.printMarket();

        int arrow = getRandomInt(0, 3);

        for (int i = 0; i < market.getNUM_R(); i++) {
            for (int j = 0; j < market.getNUM_C(); j++) {
                if (i != arrow)
                    correctMarket[i][j] = market.getMarketBoard()[i][j];
                else {
                    if(!NonStorableResourcesEnum.getAsList().contains(market.getMarketBoard()[i][j].getColor()))
                        correctTakeResources.add(market.getMarketBoard()[i][j]);
                    if (j < market.getNUM_C() - 1)
                        correctMarket[i][j] = market.getMarketBoard()[i][j+1];
                    else
                        correctMarket[i][j] = market.getExtraSlot();
                }
            }
            correctExtraSlot = market.getMarketBoard()[arrow][0];
        }
        assertEquals(market.takeResources(arrow), correctTakeResources);
        //market.printMarket(correctMarket, correctExtraSlot);
        //market.printMarket();

        assertEquals(market.getExtraSlot(), correctExtraSlot);
        for (int i = 0; i < market.getNUM_R(); i++)
            for (int j = 0; j < market.getNUM_C(); j++)
                assertEquals(market.getMarketBoard()[i][j], correctMarket[i][j]);

        correctTakeResources.clear();
        market.setTempNewResources(new ArrayList<>());
        //GameBoard.getGameBoard().reset();


    }

    public int getRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    @Test
    void invalidArrowTest() {

        assertThrows(InvalidIndexException.class, () -> market.takeResources(-1));

        assertThrows(InvalidIndexException.class, () -> market.takeResources(7)); //not existing arrow index

    }
}