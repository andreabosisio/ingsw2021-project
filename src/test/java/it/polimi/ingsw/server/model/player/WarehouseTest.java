package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    Warehouse warehouse = new Warehouse();

    @Test
    void isLegalReorganizationTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        assertFalse(warehouse.addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.BLUE));
            add(new OtherResource(ResourceEnum.YELLOW));
            add(new OtherResource(ResourceEnum.YELLOW));
            add(new OtherResource(ResourceEnum.PURPLE));
            add(new OtherResource(ResourceEnum.YELLOW));
        }})); //too many resource

        warehouse.addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.BLUE));
            add(new OtherResource(ResourceEnum.YELLOW));
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

        assertFalse(warehouse.swap(6,0)); //cannot swap from a depot to the marketResourceZone

        assertFalse(warehouse.swap(7,120)); //cannot swap from a depot to the StrongBox

        warehouse.swap(5,7);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              X
        //Second depot:         X       Y
        //Third depot:      B       X       X
        //1° extra slots:   X, X
        //2° extra slots:   X, X

        warehouse.addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.YELLOW));
            add(new OtherResource(ResourceEnum.GRAY));
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

        assertTrue(warehouse.addExtraSlots(new OtherResource(ResourceEnum.YELLOW))); //1° extra slots can contain 2 YELLOW resources

        warehouse.swap(5,10);
        warehouse.swap(6,11);
        assertTrue(warehouse.isProperlyOrdered());
        //Market resources: X, X, X, X
        //First depot:              G
        //Second depot:         X       X
        //Third depot:      B       X       X
        //1° extra slots:   Y, Y
        //2° extra slots:   X, X

        assertTrue(warehouse.addExtraSlots(new OtherResource(ResourceEnum.BLUE))); //2° extra slots can contain 2 BLUE resources

        assertFalse(warehouse.addExtraSlots(new OtherResource(ResourceEnum.GRAY))); //extra slots out of stock

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

        warehouse.addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.BLUE));
            add(new OtherResource(ResourceEnum.PURPLE));
            add(new OtherResource(ResourceEnum.BLUE));
            add(new OtherResource(ResourceEnum.GRAY));
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

        warehouse.addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.BLUE));
        }});
        assertEquals(warehouse.getResourcesFromMarket(), new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.BLUE));
        }});
        assertEquals(warehouse.getNumberOfRemainingResources(), 1);

    }


    @Test
    void getResourcesTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        warehouse.addResourcesToStrongBox(new OtherResource(ResourceEnum.BLUE));
        warehouse.addResourcesToStrongBox(new OtherResource(ResourceEnum.GRAY));
        warehouse.addResourcesToStrongBox(new OtherResource(ResourceEnum.GRAY));
        warehouse.addResourcesToStrongBox(new OtherResource(ResourceEnum.YELLOW));

        assertFalse(warehouse.swap(15, 4)); //cannot swap from StrongBox

        /*
        warehouse.addResourcesFromMarket(new ArrayList<Resource>() {{
            add(new OtherResource(ResourceEnum.BLUE));
            add(new WhiteResource()); //cannot store a WhiteResource
            add(new OtherResource(ResourceEnum.YELLOW));
        }});

         */

        warehouse.addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.BLUE));
            add(new OtherResource(ResourceEnum.YELLOW));
            add(new OtherResource(ResourceEnum.YELLOW));
        }});

        /*
        try {
            warehouse.addResourcesToStrongBox(new RedResource()); //cannot store a RedResource
        }catch (NonStorableResourceException e){
            assertTrue(true);
        }
        */

        //check if the resources have been correctly stored in the StrongBox taking them
        assertEquals(warehouse.takeResources(14), new OtherResource(ResourceEnum.BLUE));
        assertEquals(warehouse.takeResources(15), new OtherResource(ResourceEnum.GRAY));
        assertEquals(warehouse.takeResources(16), new OtherResource(ResourceEnum.GRAY));
        assertEquals(warehouse.takeResources(17), new OtherResource(ResourceEnum.YELLOW));

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

        assertEquals(warehouse.takeResources(4), new OtherResource(ResourceEnum.BLUE));
        assertEquals(warehouse.takeResources(5), new OtherResource(ResourceEnum.YELLOW));

        assertThrows(NonAccessibleSlotException.class, () -> warehouse.takeResources(1)); //cannot take a resource from the marketResourceZone

        assertThrows(EmptySlotException.class, () -> warehouse.takeResources(4)); //slot 4 is now empty
        assertThrows(EmptySlotException.class, () -> warehouse.takeResources(5)); //slot 5 is now empty

        assertEquals(warehouse.getResourcesFromMarket(), new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.YELLOW));
        }});
        assertEquals(1, warehouse.getNumberOfRemainingResources()); //one resource (Y) remained in Market resources

        //invalid negative positions
        assertThrows(InvalidIndexException.class, () -> warehouse.takeResources(-5));
        assertThrows(InvalidIndexException.class, () -> warehouse.swap(-5, 5));
        assertThrows(InvalidIndexException.class, () -> warehouse.swap(4, -1));

    }

    @Test
    void getAllResources() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        warehouse.addResourcesToStrongBox(new OtherResource(ResourceEnum.GRAY));
        warehouse.addResourcesToStrongBox(new OtherResource(ResourceEnum.PURPLE));
        warehouse.addResourcesToStrongBox(new OtherResource(ResourceEnum.GRAY));

        warehouse.addResourcesFromMarket(new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.YELLOW));
            add(new OtherResource(ResourceEnum.BLUE));
            add(new OtherResource(ResourceEnum.PURPLE));
        }});

        warehouse.swap(0,6);
        warehouse.swap(1,9);

        warehouse.addExtraSlots(new OtherResource(ResourceEnum.PURPLE));
        warehouse.swap(2, 11);

        List<Resource> correctResources = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.GRAY));
            add(new OtherResource(ResourceEnum.PURPLE));
            add(new OtherResource(ResourceEnum.GRAY));
            add(new OtherResource(ResourceEnum.YELLOW));
            add(new OtherResource(ResourceEnum.BLUE));
            add(new OtherResource(ResourceEnum.PURPLE));
        }};
        List<Resource> result = warehouse.getAllResources();
        assertTrue(result.size() == correctResources.size() && result.containsAll(correctResources) && correctResources.containsAll(result));
    }

    @Test
    void setupWarehouseTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        warehouse = new Warehouse();

        List<Resource> chosenResources = new ArrayList<Resource>(){{
            add(new OtherResource(ResourceEnum.GRAY));
            add(new OtherResource(ResourceEnum.PURPLE));
        }};

        //two different resources (GRAY and PURPLE)
        warehouse.setupWarehouse(chosenResources);
        assertEquals(warehouse.getAllResources(), chosenResources);
        assertTrue(warehouse.isProperlyOrdered());

        //two same resources (PURPLE)
        warehouse = new Warehouse();
        chosenResources.set(0, new OtherResource(ResourceEnum.PURPLE));
        warehouse.setupWarehouse(chosenResources);
        assertEquals(warehouse.getAllResources(), chosenResources);
        assertTrue(warehouse.isProperlyOrdered());
    }
}