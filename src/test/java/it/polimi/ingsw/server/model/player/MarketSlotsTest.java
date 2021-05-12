package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.player.warehouse.MarketSlots;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MarketSlotsTest {

    final MarketSlots marketSlots = new MarketSlots();

    @Test
    void simplyMarketSlotsTest(){
        List<Resource> correctResources = new ArrayList<Resource>(){{
           add(new StorableResource(ResourceEnum.YELLOW));
           add(new StorableResource(ResourceEnum.BLUE));
        }};

        marketSlots.addResources(correctResources);

        assertEquals(marketSlots.getSlots().stream().filter(Objects::nonNull).collect(Collectors.toList()), correctResources);

        assertEquals(marketSlots.takeResource(1), correctResources.get(1));

        //after taking a resource, the slot is empty
        assertNull(marketSlots.takeResource(1));
    }

}