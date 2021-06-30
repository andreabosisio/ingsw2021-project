package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.events.send.EventToClient;
import it.polimi.ingsw.server.utils.ServerParser;

import java.util.ArrayList;
import java.util.List;

public class GraphicUpdateEvent extends EventToClient {
    private final String type = ServerParser.graphicUpdateType;
    private MarketUpdate marketUpdate = null;
    private GridUpdate gridUpdate = null;
    private List<PersonalBoardUpdate> personalBoardUpdateList = null;
    private FaithTracksUpdate faithTracksUpdate = null;
    private String messageUpdate = null;

    public void addUpdate(MarketUpdate marketUpdate) {
        this.marketUpdate = marketUpdate;
    }

    public void addUpdate(GridUpdate gridUpdate) {
        this.gridUpdate = gridUpdate;
    }

    public void addUpdate(PersonalBoardUpdate personalBoardUpdate) {
        if(personalBoardUpdateList == null){
            personalBoardUpdateList = new ArrayList<>();
        }
        this.personalBoardUpdateList.add(personalBoardUpdate);
    }

    public void addUpdate(FaithTracksUpdate faithTracksUpdate) {
        this.faithTracksUpdate = faithTracksUpdate;
    }

    public void addUpdate(String messageUpdate) {this.messageUpdate = messageUpdate;}

}
