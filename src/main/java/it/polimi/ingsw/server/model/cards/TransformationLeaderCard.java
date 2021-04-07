package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.resources.Resource;

import java.util.List;

public class TransformationLeaderCard extends LeaderCard {
    private final Resource transformation;


    public TransformationLeaderCard(String ID, int points, List<Requirement> requirements, Resource transformation) {
        super(ID, points, requirements);
        this.transformation = transformation;
    }

}
