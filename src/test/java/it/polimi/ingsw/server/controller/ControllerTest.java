package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.events.receive.*;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.network.personal.ClientHandler;
import it.polimi.ingsw.server.network.personal.VirtualView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class ControllerTest {
    private static final String nameForReset = "??#??";//this names contains characters the server won't accept as a valid name therefore it can be used to create a brand new saved data
    private VirtualView virtualView1,virtualView2,virtualView3;
    private  List<VirtualView> virtualViews;
    private ClientHandler clientHandler;
    @BeforeEach
    void setUp() {
        virtualView1 = Mockito.mock(VirtualView.class);
        virtualView2 = Mockito.mock(VirtualView.class);
        virtualView3 = Mockito.mock(VirtualView.class);
        clientHandler = Mockito.mock(ClientHandler.class);
        when(virtualView3.getNickname()).thenReturn(nameForReset);
        //reset savedData
        new Controller(new ArrayList<>(){{add(virtualView3);}});
        virtualViews = new ArrayList<>(){{add(virtualView1);add(virtualView2);}};
    }

    @Test
    public void multiPlayerPersistenceTest()  {
        when(virtualView1.getNickname()).thenReturn("Forza");
        when(virtualView2.getNickname()).thenReturn("Atalanta");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        when(virtualView2.getClientHandler()).thenReturn(clientHandler);
        //Create a game for the 2 players
        Controller controller = new Controller(virtualViews);
        String firstNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(0).getNickname();
        String secondNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(1).getNickname();

        List<Integer> chosenLeaderCardIndexes = new ArrayList<>(){{add(0);add(5);}};
        List<String> chosenResources = new ArrayList<>();
        //fail first setup
        controller.update(new SetupEventFromClient(firstNickname,chosenLeaderCardIndexes,chosenResources));
        chosenLeaderCardIndexes.add(1,1);
        //do the second and successfully
        controller.update(new SetupEventFromClient(firstNickname,chosenLeaderCardIndexes,chosenResources));
        chosenResources.add("blue");
        //Setup for second player
        controller.update(new SetupEventFromClient(secondNickname,chosenLeaderCardIndexes,chosenResources));
        //check setup done successfully
        assertEquals(controller.getModelInterfaceForTesting().getStartTurn(),controller.getModelInterfaceForTesting().getCurrentState());
        //do some actions with the players
        controller.update(new LeaderHandEventFromClient(firstNickname,controller.getModelInterfaceForTesting().getPlayerByNickname(firstNickname).getLeaderHand().get(0).getID(),true));
        //fail one action
        controller.update(new MarketEventFromClient(firstNickname,12));
        //do it successfully
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
        assertEquals(controller.getModelInterfaceForTesting().getWaitResourcePlacement(),controller.getModelInterfaceForTesting().getCurrentState());
        controller.update(new PlaceResourcesEventFromClient(firstNickname,new ArrayList<>(),true));
    }
    @Test
    public void singlePlayerPersistenceTest() {
        when(virtualView1.getNickname()).thenReturn("Romero");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        List<Integer> chosenLeaderCardIndexes = new ArrayList<>(){{add(0);add(1);}};
        List<String> chosenResources = new ArrayList<>();
        //Create a game for the player
        Controller controller = new Controller(virtualViews.subList(0,1));
        controller.update(new SetupEventFromClient(virtualView1.getNickname(),chosenLeaderCardIndexes,chosenResources));
        roundOfNothingFor(controller,virtualView1.getNickname());
        roundOfNothingFor(controller,virtualView1.getNickname());
        controller = new Controller(virtualViews.subList(0,1));
        //check that controller is restarting from an initial game
        assertEquals(controller.getModelInterfaceForTesting().getStartTurn(),controller.getModelInterfaceForTesting().getCurrentState());
        roundOfNothingFor(controller,virtualView1.getNickname());
        //check with second crash
        controller = new Controller(virtualViews.subList(0,1));
        assertEquals(controller.getModelInterfaceForTesting().getStartTurn(),controller.getModelInterfaceForTesting().getCurrentState());
    }
    @Test
    public void multiPlayerPersistenceInSetUpTest() {
        when(virtualView1.getNickname()).thenReturn("Beppe");
        when(virtualView2.getNickname()).thenReturn("Chicken");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        when(virtualView2.getClientHandler()).thenReturn(clientHandler);
        //Create a game for the 2 players
        Controller controller = new Controller(virtualViews);
        String firstNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(0).getNickname();
        String secondNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(1).getNickname();
        List<Integer> chosenLeaderCardIndexes = new ArrayList<>() {{
            add(0);
            add(1);
        }};
        List<String> chosenResources = new ArrayList<>();
        controller.update(new SetupEventFromClient(firstNickname,chosenLeaderCardIndexes,chosenResources));
        chosenResources.add("blue");
        //simulate server crash
        controller = new Controller(virtualViews);
        //second player does his pending setup
        controller.update(new SetupEventFromClient(secondNickname,chosenLeaderCardIndexes,chosenResources));
        //check that game is ready as first player already did his action
        assertEquals(controller.getModelInterfaceForTesting().getStartTurn(),controller.getModelInterfaceForTesting().getCurrentState());
    }
    @Test
    public void multiPlayerPersistenceInSetUpTestWithAutoSetup() {
        when(virtualView1.getNickname()).thenReturn("Beppe");
        when(virtualView2.getNickname()).thenReturn("Chicken");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        when(virtualView2.getClientHandler()).thenReturn(clientHandler);
        //Create a game for the 2 players
        Controller controller = new Controller(virtualViews);
        String firstNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(0).getNickname();
        String secondNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(1).getNickname();
        List<Integer> chosenLeaderCardIndexes = new ArrayList<>() {{
            add(0);
            add(1);
        }};
        List<String> chosenResources = new ArrayList<>();
        controller.update(new SetupEventFromClient(firstNickname,chosenLeaderCardIndexes,chosenResources));
        chosenResources.add("blue");
        //second player disconnect
        controller.disconnectPlayer(secondNickname);
        //game has moved in startGame for first player
        assertEquals(controller.getModelInterfaceForTesting().getStartTurn(),controller.getModelInterfaceForTesting().getCurrentState());
        //second player reconnect
        controller.reconnectPlayer(secondNickname);
        roundOfNothingFor(controller,firstNickname);
        //check that second player can do his turn as his setup has been done
        assertEquals(secondNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
    }


    private void roundOfNothingFor(Controller controller,String player){
        controller.update(new MarketEventFromClient(player,6));
        controller.update(new PlaceResourcesEventFromClient(player,new ArrayList<>(),true));
        controller.update(new EndTurnEventFromClient(player));
    }

    @Test
    public void disconnectionOfOnePlayerTests()  {
        when(virtualView1.getNickname()).thenReturn("Zapata");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        List<Integer> chosenLeaderCardIndexes = new ArrayList<>(){{add(0);add(1);}};
        List<String> chosenResources = new ArrayList<>();
        //Create a game for the player
        Controller controller = new Controller(virtualViews.subList(0,1));
        controller.update(new SetupEventFromClient(virtualView1.getNickname(),chosenLeaderCardIndexes,chosenResources));
        roundOfNothingFor(controller,virtualView1.getNickname());
        controller.disconnectPlayer(virtualView1.getNickname());
        //test that reconnection of one player equals a new game
        controller = new Controller(virtualViews.subList(0,1));
        //check that server is waiting for a setup
        assertEquals(controller.getModelInterfaceForTesting().getIdle(),controller.getModelInterfaceForTesting().getCurrentState());
        controller.update(new SetupEventFromClient(virtualView1.getNickname(),chosenLeaderCardIndexes,chosenResources));
    }
    @Test
    public void disconnectDuringSetupTest(){
        when(virtualView1.getNickname()).thenReturn("Muriel");
        when(virtualView2.getNickname()).thenReturn("Bischero");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        List<Integer> chosenLeaderCardIndexes = new ArrayList<>(){{add(0);add(1);}};
        List<String> chosenResources = new ArrayList<>();
        //Create a game for the player
        Controller controller = new Controller(virtualViews.subList(0,1));
        controller.disconnectPlayer(virtualView1.getNickname());
        controller = new Controller(virtualViews.subList(1,2));
        controller.update(new SetupEventFromClient(virtualView2.getNickname(),chosenLeaderCardIndexes,chosenResources));
        assertEquals(virtualView2.getNickname(),controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
    }
    @Test
    public void disconnectionOfTwoPlayersTests()  {
        when(virtualView1.getNickname()).thenReturn("Pepe");
        when(virtualView2.getNickname()).thenReturn("TheFrog");
        when(virtualView1.getClientHandler()).thenReturn(clientHandler);
        when(virtualView2.getClientHandler()).thenReturn(clientHandler);
        //Prepare a game with a setup done for 2 players
        Controller controller = new Controller(virtualViews);
        String firstNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(0).getNickname();
        String secondNickname = controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().get(1).getNickname();
        List<Integer> chosenLeaderCardIndexes = new ArrayList<>(){{add(0);add(1);}};
        List<String> chosenResources = new ArrayList<>();
        controller.update(new SetupEventFromClient(firstNickname,chosenLeaderCardIndexes,chosenResources));
        chosenResources.add("blue");
        controller.update(new SetupEventFromClient(secondNickname,chosenLeaderCardIndexes,chosenResources));
        //first player disconnect
        controller.disconnectPlayer(firstNickname);
        //check that it is second player turn and he is the only one online
        assertEquals(secondNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        assertEquals(1,controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().stream().filter(Player::isOnline).count());
        //second player does his turn
        roundOfNothingFor(controller,secondNickname);
        //check that it is still the second player turn
        assertEquals(secondNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        //first player reconnect and second player end his turn
        controller.reconnectPlayer(firstNickname);
        roundOfNothingFor(controller,secondNickname);
        //check that it is first player turn as he is now reconnected
        assertEquals(firstNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        assertEquals(2,controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().stream().filter(Player::isOnline).count());
        //do his turn
        roundOfNothingFor(controller,firstNickname);
        //check that it is second player turn
        assertEquals(secondNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        //do the same for second player disconnection
        //second player disconnect
        controller.disconnectPlayer(secondNickname);
        //check that it is first player turn and he is the only one online
        assertEquals(firstNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        assertEquals(1,controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().stream().filter(Player::isOnline).count());
        //first player does his turn
        roundOfNothingFor(controller,firstNickname);
        //check that it is still the first player turn
        assertEquals(firstNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        //second player reconnect and first player end his turn
        controller.reconnectPlayer(secondNickname);
        roundOfNothingFor(controller,firstNickname);
        //check that it is second player turn as he is now reconnected
        assertEquals(secondNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        assertEquals(2,controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().stream().filter(Player::isOnline).count());
        //do his turn
        roundOfNothingFor(controller,secondNickname);
        //check that it is first player turn
        assertEquals(firstNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        //test disconnection when not in their turn
        //second player disconnect
        controller.disconnectPlayer(secondNickname);
        //check that after first actions it is still his turn
        roundOfNothingFor(controller,firstNickname);
        assertEquals(firstNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        assertEquals(1,controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().stream().filter(Player::isOnline).count());
        //reconnect second player and try the same for him
        controller.reconnectPlayer(secondNickname);
        roundOfNothingFor(controller,firstNickname);
        //it's second turn and he is now online
        assertEquals(secondNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
        assertEquals(2,controller.getModelInterfaceForTesting().getTurnLogic().getPlayers().stream().filter(Player::isOnline).count());
        controller.disconnectPlayer(firstNickname);
        roundOfNothingFor(controller,secondNickname);
        assertEquals(secondNickname,controller.getModelInterfaceForTesting().getTurnLogic().getCurrentPlayer().getNickname());
    }
}