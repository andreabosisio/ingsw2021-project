package it.polimi.ingsw.server.model.resources;

import java.util.ArrayList;
import java.util.List;

public final class NonStorableResources {

    /**
     * Resources that can't be stored and used to activate a production.
     */
    final static List<Resource> nonStorableResources = new ArrayList<Resource>(){{
        add(new RedResource());
        add(new WhiteResource());
    }};

    public static List<Resource> getNonStorableResources() {
        return nonStorableResources;
    }
}
