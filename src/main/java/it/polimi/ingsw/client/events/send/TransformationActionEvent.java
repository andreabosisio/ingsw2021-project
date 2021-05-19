package it.polimi.ingsw.client.events.send;

import java.util.List;

public class TransformationActionEvent extends SendEvent{
    private final List<String> chosenResources;
    public TransformationActionEvent(List<String> chosenResources) {
        super("transformationAction");
        this.chosenResources = chosenResources;
    }
}
