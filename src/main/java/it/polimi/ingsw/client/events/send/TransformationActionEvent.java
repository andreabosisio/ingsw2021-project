package it.polimi.ingsw.client.events.send;

import it.polimi.ingsw.client.utils.ClientParser;

import java.util.List;

/**
 * Represent the request of the choice of the White Resources Transformation by a Player
 */
public class TransformationActionEvent extends EventToServer {
    private final List<String> chosenResources;

    /**
     * This event is sent when a Player chooses the transformation of the White Resources
     *
     * @param chosenResources The color of the chosen Resources as result of the Transformation
     */
    public TransformationActionEvent(List<String> chosenResources) {
        super(ClientParser.TRANSFORMATION_ACTION_TYPE);
        this.chosenResources = chosenResources;
    }
}
