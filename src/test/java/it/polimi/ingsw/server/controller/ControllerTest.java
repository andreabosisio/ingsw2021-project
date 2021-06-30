package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.model.ModelInterface;
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
        when(virtualView1.getNickname()).thenReturn("Forza");
        when(virtualView2.getNickname()).thenReturn("Atalanta");
        when(virtualView3.getNickname()).thenReturn("usedToReset");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        when(virtualView2.getClientHandler()).thenReturn(clientHandler);
        //used so test won't failed with a saved data equals to first second
        Controller controller = new Controller(new ArrayList<>(){{add(virtualView1);add(virtualView3);}});
        List<VirtualView> virtualViews = new ArrayList<VirtualView>(){{add(virtualView1);add(virtualView2);}};
        controller = new Controller(virtualViews);
        String firstNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(0).getNickname();
        String secondNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(1).getNickname();

        List<Integer> chosenLeaderCardIndexes = new ArrayList<>(){{add(0);add(5);}};
        List<String> chosenResources = new ArrayList<>();
        //fail first setup
        controller.update(new SetupEventFromClient(firstNickname,chosenLeaderCardIndexes,chosenResources));
        chosenLeaderCardIndexes.add(1,1);
        controller.update(new SetupEventFromClient(firstNickname,chosenLeaderCardIndexes,chosenResources));
        chosenResources.add("blue");
        controller.update(new SetupEventFromClient(secondNickname,chosenLeaderCardIndexes,chosenResources));
        //setup done successfully
        assertEquals(controller.getModelInterfaceForTesting().getTurnLogic().getStartTurn(),controller.getModelInterfaceForTesting().getTurnLogic().getCurrentState());
        //do some actions with the players
        controller.update(new LeaderHandEventFromClient(firstNickname,controller.getModelInterfaceForTesting().getPlayerByNickname(firstNickname).getLeaderHand().get(0).getID(),true));
        controller.update(new MarketEventFromClient(firstNickname,1));
        controller.update(new PlaceResourcesEventFromClient(firstNickname,new ArrayList<>(),true));
        controller.update(new LeaderHandEventFromClient(firstNickname,controller.getModelInterfaceForTesting().getPlayerByNickname(firstNickname).getLeaderHand().get(0).getID(),true));
        //check that player one no longer owns leaderCards
        assertEquals(0,controller.getModelInterfaceForTesting().getPlayerByNickname(firstNickname).getLeaderHand().size());
        //disconnect second player
        controller.disconnectPlayer(secondNickname);
        //cheat from admin in the middle
        controller.cheat();
        roundOfNothingFor(controller,firstNickname);
        //reconnect second player
        controller.reconnectPlayer(secondNickname);
        roundOfNothingFor(controller,firstNickname);
        controller.update(new MarketEventFromClient(secondNickname,1));
        controller.update(new PlaceResourcesEventFromClient(secondNickname,new ArrayList<>(),true));
        controller.update(new LeaderHandEventFromClient(secondNickname,controller.getModelInterfaceForTesting().getPlayerByNickname(secondNickname).getLeaderHand().get(0).getID(),true));
        controller.update(new MarketEventFromClient(firstNickname,3));
        //simulate a server shutdown and reconnection of the same players
        controller = new Controller(virtualViews);
        //check that player one must place the resources he was placing before the shutdown
        controller.update(new PlaceResourcesEventFromClient(firstNickname,new ArrayList<>(),true));
    }

    private void roundOfNothingFor(Controller controller,String player){
        controller.update(new MarketEventFromClient(player,1));
        controller.update(new PlaceResourcesEventFromClient(player,new ArrayList<>(),true));
        controller.update(new EndTurnEventFromClient(player));
    }
}