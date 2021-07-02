package it.polimi.ingsw.commons.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the Storable Resource's colors.
 */
public enum StorableResourceEnum {
    YELLOW,
    GRAY,
    PURPLE,
    BLUE;

    /**
     * This method return the Storable Resources as a List
     *
     * @return a List that contain all the Storable Resources
     */
    public static List<ResourcesEnum> getAsList() {
        return new ArrayList<>() {{
            add(ResourcesEnum.YELLOW);
            add(ResourcesEnum.GRAY);
            add(ResourcesEnum.PURPLE);
            add(ResourcesEnum.BLUE);
        }};
    }
}
