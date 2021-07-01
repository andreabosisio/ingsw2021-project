package it.polimi.ingsw.server.events.send.choice;

import it.polimi.ingsw.server.model.resources.WhiteResource;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.ArrayList;
import java.util.List;

public class TransformationChoiceEvent extends ChoiceEvent {
    private final int numberOfTransformation;
    private final List<String> possibleTransformations = new ArrayList<>();

    public TransformationChoiceEvent(String nickname, List<WhiteResource> whiteResources) {
        super(nickname, ServerParser.TRANSFORMATION_TYPE);
        numberOfTransformation = whiteResources.size();

        //every white resource has the same possible transformations
        whiteResources.get(0).getPossibleTransformations().forEach(resource -> possibleTransformations.add(resource.getColor().toString()));
    }
}
