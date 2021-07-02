package it.polimi.ingsw.commons.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Resources that can't be stored and used to activate a production.
 */
public enum NonStorableResourcesEnum {
    RED,
    WHITE;

    /**
     * This method return the List of the Resources that are not storable
     *
     * @return the List of the non storable Resources
     */
    public static List<ResourcesEnum> getAsList() {
        return new ArrayList<>() {{
            add(ResourcesEnum.RED);
            add(ResourcesEnum.WHITE);
        }};
    }
}
