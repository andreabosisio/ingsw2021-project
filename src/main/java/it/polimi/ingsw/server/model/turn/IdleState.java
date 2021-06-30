package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.events.send.choice.SetupChoiceEvent;
import it.polimi.ingsw.server.model.SetupManager;

public class IdleState extends State {
    private SetupManager setupManager;
    public IdleState(TurnLogic turnLogic) {
        super(turnLogic);
    }

    public void setSetupManager(SetupManager setupManager) {
        this.setupManager = setupManager;
    }

    @Override
    public boolean disconnectAction(String nickname) {
        return setupManager.disconnectPlayer(nickname);
    }

    @Override
    public void reconnectAction(String nickname) {
        setupManager.reconnectPlayer(nickname);
    }

    @Override
    public void sendNecessaryEvents() {
        setupManager.reSendAllPendingSetupEvents();
    }
}
