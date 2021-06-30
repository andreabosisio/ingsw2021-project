package it.polimi.ingsw.client.events.send;

import java.util.List;

/**
 * Represents the request of the choice of the White Resources Transformation by a Player
 */
public class TransformationActionEvent extends EventToServer {
    private final List<String> chosenResources;

    /**
     * This event is sent when a Player chooses the transformation of the White Resources
     * @param chosenResources The color of the chosen Resources as result of the Transformation
     */
    public TransformationActionEvent(List<String> chosenResources) {
        super("transformationAction");
        this.chosenResources = chosenResources;
    }
}
