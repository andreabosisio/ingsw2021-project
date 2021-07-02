package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {
    final TurnLogic turnLogic = new TurnLogic(new ArrayList<>() {{
        add(new Player("Pluto"));
    }},null);
    @Test
    void productionAbility() {
        assertFalse(new WhiteResource().productionAbility(turnLogic));
    }

    @Test
    void addPossibleTransformation() {
        assertFalse(new RedResource().addPossibleTransformation(new StorableResource(ResourcesEnum.PURPLE)));
        assertFalse(new StorableResource(ResourcesEnum.BLUE).addPossibleTransformation(new StorableResource(ResourcesEnum.YELLOW)));
    }

    @Test
    void Clone() {
        WhiteResource white = new WhiteResource();
        assertTrue(white.addPossibleTransformation(new StorableResource(ResourcesEnum.YELLOW)));
        assertTrue(white.addPossibleTransformation(new StorableResource(ResourcesEnum.GRAY)));
        WhiteResource cloned = white.deepClone();
        assertNotSame(cloned, white);
        assertEquals(2,cloned.getPossibleTransformations().size());
        List<ResourcesEnum> clonedColors = cloned.getPossibleTransformations().stream().map(Resource::getColor).collect(Collectors.toList());
        assertTrue(clonedColors.contains(ResourcesEnum.YELLOW));
        assertTrue(clonedColors.contains(ResourcesEnum.GRAY));
    }

    @SuppressWarnings("AssertBetweenInconvertibleTypes")
    @Test
    void equals() {
        assertNotEquals(new StorableResource(ResourcesEnum.PURPLE), turnLogic);
    }

    @Test
    void constructor() throws NonStorableResourceException {
        assertEquals(ResourceFactory.produceResource(ResourcesEnum.GRAY), new StorableResource(ResourcesEnum.GRAY));

        assertThrows(NonStorableResourceException.class, () -> ResourceFactory.produceResource(ResourcesEnum.RED));
        assertThrows(NonStorableResourceException.class, () -> ResourceFactory.produceResource(ResourcesEnum.WHITE));
    }
}