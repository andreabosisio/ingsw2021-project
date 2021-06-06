package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.server.model.enums.ResourceEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class NonStorableResources {

    /**
     * Resources that can't be stored and used to activate a production.
     */
    final static List<Resource> nonStorableResources = new ArrayList<>() {{
        add(new RedResource());
        add(new WhiteResource());
    }};

    public static List<Resource> getNonStorableResources() {
        return nonStorableResources;
    }

    public static List<ResourceEnum> getNonStorableResourcesEnum() {
        return nonStorableResources.stream().map(Resource::getColor).collect(Collectors.toList());
    }
}
