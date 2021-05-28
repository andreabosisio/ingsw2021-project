package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;

import java.util.List;

public class TransformationReceiveEvent implements ReceiveEvent{
    private final int numberOfTransformation;
    private final List<String> possibleTransformations;

    public TransformationReceiveEvent(int numberOfTransformation, List<String> possibleTransformations) {
        this.numberOfTransformation = numberOfTransformation;
        this.possibleTransformations = possibleTransformations;
    }

    @Override
    public void updateView(View view) {
        view.setOnTransformation(numberOfTransformation, possibleTransformations);
    }
}
