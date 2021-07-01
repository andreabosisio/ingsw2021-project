package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscountLeaderCardTest {
    final CardsGenerator cardsGenerator = new CardsGenerator();
    final DiscountLeaderCard discountLeaderCard = (DiscountLeaderCard) cardsGenerator.generateLeaderCards().get(12);

    @Test
    void applyDiscountTest() {
        List<Resource> discount = new ArrayList<>();
        assertTrue(discountLeaderCard.applyDiscount(discount));
        assertEquals(discount, new ArrayList<Resource>(){{
            add(new StorableResource(ResourcesEnum.GRAY));
        }});
    }
}