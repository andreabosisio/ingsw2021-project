package it.polimi.ingsw.server.events.send;

import it.polimi.ingsw.server.model.resources.WhiteResource;

import java.util.ArrayList;
import java.util.List;

public class TransformationSendEvent extends SendEvent{
    private final int numberOfWhiteResources;
    private final List<String> possibleTransformations = new ArrayList<>();

    public TransformationSendEvent(String nickname, List<WhiteResource> whiteResources) {
        super(nickname);
        numberOfWhiteResources = whiteResources.size();

        //every white resource has the same possible transformations
        whiteResources.get(0).getPossibleTransformations().forEach(resource -> possibleTransformations.add(resource.getColor().toString()));
    }
}
