package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.events.receive.SetupEventFromClient;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.personal.VirtualView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    VirtualView virtualView1,virtualView2,virtualView3;
    @Mock
    ClientHandler clientHandler;

    @Test
    public void testWithMockito()  {
        when(virtualView1.getNickname()).thenReturn("first");
        when(virtualView3.getNickname()).thenReturn("usedToReset");
        when(virtualView2.getNickname()).thenReturn("second");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        when(virtualView2.getClientHandler()).thenReturn(clientHandler);
        //used so test won't failed with a saved data equals to first second
        Controller controller = new Controller(new ArrayList<>(){{add(virtualView1);add(virtualView3);}});

        controller = new Controller(new ArrayList<>(){{add(virtualView1);add(virtualView2);}});

        List<Integer> chosenLeaderCardIndexes = new ArrayList<>(){{add(0);add(1);}};
        List<String> chosenResources = new ArrayList<>();
        controller.update(new SetupEventFromClient("first",chosenLeaderCardIndexes,chosenResources));
        chosenResources.add("YELLOW");
        controller.update(new SetupEventFromClient("second",chosenLeaderCardIndexes,chosenResources));
        //setup done successfully
        assertEquals(controller.getModelInterfaceForTesting().getTurnLogic().getStartTurn(), controller.getModelInterfaceForTesting().getTurnLogic().getCurrentState());

    }
}