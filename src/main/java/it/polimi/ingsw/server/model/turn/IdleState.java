package it.polimi.ingsw.server.model.turn;

import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.SetupManager;

/**
 * State of the Model that accepts only Setup Actions
 */
public class IdleState extends State {
    private final SetupManager setupManager;

    public IdleState(ModelInterface modelInterface) {
        super(modelInterface);
        this.setupManager = modelInterface.getSetupManager();
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
