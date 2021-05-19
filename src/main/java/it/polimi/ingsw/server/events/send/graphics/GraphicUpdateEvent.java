package it.polimi.ingsw.server.events.send.graphics;

import com.google.gson.Gson;
import it.polimi.ingsw.server.events.send.SendEvent;

import java.util.ArrayList;
import java.util.List;

public class GraphicUpdateEvent implements SendEvent {
    private final String type = "graphicUpdate";
    private MarketUpdate marketUpdate = null;
    private GridUpdate gridUpdate = null;
    private List<PersonalBoardUpdate> personalBoardUpdateList = null;
    private FaithTracksUpdate faithTracksUpdate = null;

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

    @Override
    public boolean isForYou(String nickname) {
        return true;
    }

    @Override
    public String toJson() {
        return new Gson().toJson(this);
    }
}
