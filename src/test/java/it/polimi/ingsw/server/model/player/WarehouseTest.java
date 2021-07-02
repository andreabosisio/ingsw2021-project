package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.EmptySlotException;
import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    Warehouse warehouse = new Warehouse();

    @Test
    void isLegalReorganizationTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {

        warehouse.setupWarehouse(new ArrayList<>());

        assertEquals(0, warehouse.getNumberOfRemainingResources());

        assertFalse(warehouse.addResourcesFromMarket(new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.PURPLE));
            add(new StorableResource(ResourcesEnum.YELLOW));
        }})); //too many resource

        warehouse.addResourcesFromMarket(new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.YELLOW));
        }});
        //Market resources: B, Y, X, X
        //First depot:              X
        //Second depot:         X       X
        //Third depot:      X       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        warehouse.swap(0, 4);
        warehouse.swap(1, 5);
        assertEquals(warehouse.getNumberOfRemainingResources(), 0);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              B
        //Second depot:         Y       X
        //Third depot:      X       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        assertThrows(EmptySlotException.class, () -> warehouse.swap(8, 4)); //(X <--> B) : cannot swap an empty slot

        warehouse.swap(4,5);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              Y
        //Second depot:         B       X
        //Third depot:      X       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        warehouse.swap(4,6);
        assertFalse(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              X
        //Second depot:         B       Y
        //Third depot:      X       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        //assertFalse(warehouse.swap(6,0)); //cannot swap from a depot to the marketResourceZone

        assertFalse(warehouse.swap(7,120)); //cannot swap from a depot to the StrongBox

        warehouse.swap(5,7);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              X
        //Second depot:         X       Y
        //Third depot:      B       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        Map<Integer, String> correctPositionsAndResources = new HashMap<>() {{
            put(0, ResourcesEnum.EMPTY_RES.toString());
            put(1, ResourcesEnum.EMPTY_RES.toString());
            put(2, ResourcesEnum.EMPTY_RES.toString());
            put(3, ResourcesEnum.EMPTY_RES.toString());
            put(4, ResourcesEnum.EMPTY_RES.toString());
            put(5, ResourcesEnum.EMPTY_RES.toString());
            put(6, ResourcesEnum.YELLOW.toString());
            put(7, ResourcesEnum.BLUE.toString());
            put(8, ResourcesEnum.EMPTY_RES.toString());
        }};

        assertEquals(correctPositionsAndResources, warehouse.getAllPositionsAndResources());

        warehouse.addResourcesFromMarket(new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.GRAY));
        }});
        //Market resources: Y, G, X, X
        //First depot:              X
        //Second depot:         X       Y
        //Third depot:      B       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X


        warehouse.swap(0,5);
        warehouse.swap(1,4);
        assertEquals(warehouse.getNumberOfRemainingResources(), 0);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              G
        //Second depot:         Y       Y
        //Third depot:      B       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        assertThrows(NonAccessibleSlotException.class, () -> warehouse.swap(5,10)); //(Y <--> X) : cannot store to 1° extra slots

        assertThrows(NonAccessibleSlotException.class, () -> warehouse.swap(6,13)); //(Y <--> X) : cannot store to 2° extra slots

        assertTrue(warehouse.addExtraSlots(new StorableResource(ResourcesEnum.YELLOW))); //1° extra slots can contain 2 YELLOW resources

        warehouse.swap(5,10);
        warehouse.swap(6,11);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              G
        //Second depot:         X       X
        //Third depot:      B       X       X
        //1° extra slots:   Y, Y
        //2° extra slots:   X, X

        assertTrue(warehouse.addExtraSlots(new StorableResource(ResourcesEnum.BLUE))); //2° extra slots can contain 2 BLUE resources

        assertFalse(warehouse.addExtraSlots(new StorableResource(ResourcesEnum.GRAY))); //extra slots out of stock

        warehouse.swap(7,13);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              G
        //Second depot:         X       X
        //Third depot:      X       X       X
        //1° extra slots:   Y, Y
        //2° extra slots:   X, B

        assertTrue(warehouse.swap(4,11)); //swap G to 2° slot of 1° extra slots
        assertFalse(warehouse.isProperlyOrdered()); //now 1° extra slots configuration isn't legal anymore
        assertTrue(warehouse.swap(4,11));
        assertTrue(warehouse.isProperlyOrdered()); //back to the last legal configuration

        warehouse.addResourcesFromMarket(new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.PURPLE));
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.GRAY));
        }});
        //Market resources: B, P, B, G
        //First depot:              G
        //Second depot:         X       X
        //Third depot:      X       X       X
        //1° extra slots:   Y, Y
        //2° extra slots:   X, B

        assertTrue(warehouse.swap(0,8));
        assertTrue(warehouse.swap(2,12));
        assertTrue(warehouse.swap(3,6));
        assertFalse(warehouse.isProperlyOrdered());//two depots of G
        //Market resources: X, P, X, X
        //First depot:              G
        //Second depot:         X       G
        //Third depot:      X       B       X
        //1° extra slots:   Y, Y
        //2° extra slots:   B, B


        assertTrue(warehouse.swap(4,5));
        assertTrue(warehouse.swap(1,4));
        assertEquals(warehouse.getNumberOfRemainingResources(), 0);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              P
        //Second depot:         G       G
        //Third depot:      X       B       X
        //1° extra slots:   Y, Y
        //2° extra slots:   B, B

        assertTrue(warehouse.swap(4,13));
        assertTrue(warehouse.swap(4,9)); //2° extra slots not legal
        assertFalse(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              X
        //Second depot:         G       G
        //Third depot:      X       B       B
        //1° extra slots:   Y, Y
        //2° extra slots:   B, P

        warehouse.addResourcesFromMarket(new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.BLUE));
        }});
        assertEquals(warehouse.getResourcesFromMarket(), new ArrayList<Resource>(){{
            add(new StorableResource(ResourcesEnum.BLUE));
        }});
        assertEquals(warehouse.getNumberOfRemainingResources(), 1);

        //System.out.println(warehouse.getAllPositionsAndResources());
    }


    @Test
    void getResourcesTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {

        warehouse.setupWarehouse(new ArrayList<>());

        assertEquals(0, warehouse.getNumberOfRemainingResources());

        warehouse.addResourcesFromMarket(new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.YELLOW));
        }});

        warehouse.addResourcesToStrongBox(new StorableResource(ResourcesEnum.BLUE));
        warehouse.addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY));
        warehouse.addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY));
        warehouse.addResourcesToStrongBox(new StorableResource(ResourcesEnum.YELLOW));

        assertFalse(warehouse.swap(15, 4)); //cannot swap from StrongBox

        Map<Integer, String> correctPositionsAndResources = new HashMap<>() {{
            put(0, ResourcesEnum.BLUE.toString());
            put(1, ResourcesEnum.YELLOW.toString());
            put(2, ResourcesEnum.YELLOW.toString());
            put(3, ResourcesEnum.EMPTY_RES.toString());
            put(14, ResourcesEnum.BLUE.toString());
            put(15, ResourcesEnum.GRAY.toString());
            put(16, ResourcesEnum.GRAY.toString());
            put(17, ResourcesEnum.YELLOW.toString());
        }};

        assertEquals(correctPositionsAndResources, warehouse.getAllPositionsAndResources());

        //check if the resources have been correctly stored in the StrongBox by taking them
        assertEquals(warehouse.takeResources(14), new StorableResource(ResourcesEnum.BLUE));
        assertEquals(warehouse.takeResources(15), new StorableResource(ResourcesEnum.GRAY));
        assertEquals(warehouse.takeResources(16), new StorableResource(ResourcesEnum.GRAY));
        assertEquals(warehouse.takeResources(17), new StorableResource(ResourcesEnum.YELLOW));

        correctPositionsAndResources.put(14, ResourcesEnum.EMPTY_RES.toString());
        correctPositionsAndResources.put(15, ResourcesEnum.EMPTY_RES.toString());
        correctPositionsAndResources.put(16, ResourcesEnum.EMPTY_RES.toString());
        correctPositionsAndResources.put(17, ResourcesEnum.EMPTY_RES.toString());

        assertEquals(correctPositionsAndResources, warehouse.getAllPositionsAndResources());


        assertThrows(EmptySlotException.class, () -> warehouse.takeResources(16)); //third slot of the StrongBox is now empty

        assertThrows(EmptySlotException.class, () -> warehouse.takeResources(5)); //first slot of the 2° depot is empty

        assertThrows(EmptySlotException.class, () -> warehouse.takeResources(22));

        assertThrows(EmptySlotException.class, () -> warehouse.takeResources(140));

        //Market resources: B, Y, Y, X
        //First depot:              X
        //Second depot:         X       X
        //Third depot:      X       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        warehouse.swap(0,4);
        warehouse.swap(1,5);
        //Market resources: X, X, Y, X
        //First depot:              B
        //Second depot:         Y       X
        //Third depot:      X       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        assertEquals(warehouse.takeResources(4), new StorableResource(ResourcesEnum.BLUE));
        assertEquals(warehouse.takeResources(5), new StorableResource(ResourcesEnum.YELLOW));

        assertThrows(NonAccessibleSlotException.class, () -> warehouse.takeResources(1)); //cannot take a resource from the marketResourceZone

        assertThrows(EmptySlotException.class, () -> warehouse.takeResources(4)); //slot 4 is now empty
        assertThrows(EmptySlotException.class, () -> warehouse.takeResources(5)); //slot 5 is now empty

        assertEquals(warehouse.getResourcesFromMarket(), new ArrayList<Resource>(){{
            add(new StorableResource(ResourcesEnum.YELLOW));
        }});
        assertEquals(1, warehouse.getNumberOfRemainingResources()); //one resource (Y) remained in Market resources

        //invalid negative positions
        assertThrows(InvalidIndexException.class, () -> warehouse.takeResources(-5));
        assertThrows(InvalidIndexException.class, () -> warehouse.swap(-5, 5));
        assertThrows(InvalidIndexException.class, () -> warehouse.swap(4, -1));

        //System.out.println(warehouse.getAllPositionsAndResources());
    }

    @Test
    void getAllResources() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        warehouse.addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY));
        warehouse.addResourcesToStrongBox(new StorableResource(ResourcesEnum.PURPLE));
        warehouse.addResourcesToStrongBox(new StorableResource(ResourcesEnum.GRAY));

        warehouse.addResourcesFromMarket(new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.PURPLE));
        }});

        warehouse.swap(0,6);
        warehouse.swap(1,9);

        warehouse.addExtraSlots(new StorableResource(ResourcesEnum.PURPLE));
        warehouse.swap(2, 11);

        List<Resource> correctResources = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.GRAY));
            add(new StorableResource(ResourcesEnum.PURPLE));
            add(new StorableResource(ResourcesEnum.GRAY));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.PURPLE));
        }};
        List<Resource> result = warehouse.getAllResources();
        assertTrue(result.size() == correctResources.size() && result.containsAll(correctResources) && correctResources.containsAll(result));
    }

    @Test
    void setupWarehouseTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        warehouse = new Warehouse();

        List<Resource> chosenResources = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.GRAY));
            add(new StorableResource(ResourcesEnum.PURPLE));
        }};

        //two different resources (GRAY and PURPLE)
        warehouse.setupWarehouse(chosenResources);
        assertEquals(warehouse.getAllResources(), chosenResources);
        assertTrue(warehouse.isProperlyOrdered());

        //two same resources (PURPLE)
        warehouse = new Warehouse();
        chosenResources.set(0, new StorableResource(ResourcesEnum.PURPLE));
        warehouse.setupWarehouse(chosenResources);
        assertEquals(warehouse.getAllResources(), chosenResources);
        assertTrue(warehouse.isProperlyOrdered());
    }
}