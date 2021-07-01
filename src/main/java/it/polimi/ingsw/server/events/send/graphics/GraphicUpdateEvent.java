package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.events.send.EventToClient;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all the needed data to update the Client Light Model state.
 */
public class GraphicUpdateEvent extends EventToClient {
    private final String type = ServerParser.GRAPHIC_UPDATE_TYPE;
    private MarketUpdate marketUpdate = null;
    private GridUpdate gridUpdate = null;
    private List<PersonalBoardUpdate> personalBoardUpdateList = null;
    private FaithTracksUpdate faithTracksUpdate = null;
    private String messageUpdate = null;

    /**
     * Add a Market Update to this Update.
     *
     * @param marketUpdate A Market Update
     */
    public void addUpdate(MarketUpdate marketUpdate) {
        this.marketUpdate = marketUpdate;
    }

    /**
     * Add a Grid Update to this Update.
     *
     * @param gridUpdate A Grid Update
     */
    public void addUpdate(GridUpdate gridUpdate) {
        this.gridUpdate = gridUpdate;
    }

    /**
     * Add a Personal Board Update to this Update.
     *
     * @param personalBoardUpdate A Personal Board Update
     */
    public void addUpdate(PersonalBoardUpdate personalBoardUpdate) {
        if (personalBoardUpdateList == null) {
            personalBoardUpdateList = new ArrayList<>();
        }
        this.personalBoardUpdateList.add(personalBoardUpdate);
    }

    /**
     * Add a Faith Tracks Update to this Update.
     *
     * @param faithTracksUpdate A Faith Tracks Update
     */
    public void addUpdate(FaithTracksUpdate faithTracksUpdate) {
        this.faithTracksUpdate = faithTracksUpdate;
    }

    /**
     * Add a message to this Update.
     *
     * @param messageUpdate A String containing a message
     */
    public void addUpdate(String messageUpdate) {
        this.messageUpdate = messageUpdate;
    }

}
