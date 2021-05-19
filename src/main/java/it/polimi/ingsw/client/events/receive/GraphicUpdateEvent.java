package it.polimi.ingsw.client.events.receive;

import com.google.gson.*;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.DevelopmentCardsGrid;
import it.polimi.ingsw.client.model.FaithTrack;
import it.polimi.ingsw.client.model.MarketTray;
import it.polimi.ingsw.client.view.View;

import java.util.List;

public class GraphicUpdateEvent implements ReceiveEvent{

    //they are null if not present in json
    private JsonObject gridUpdate;
    private JsonObject marketUpdate;
    private JsonObject faithTracksUpdate;
    private JsonArray personalBoardUpdateList;



/*
    public GraphicUpdateEvent(JsonObject gridUpdate, JsonObject marketUpdate) {
        this.gridUpdate = gridUpdate;
        this.marketUpdate = marketUpdate;
    }

 */

    @Override
    public void updateView(View view) {
        Gson gson = new Gson();
        //MarketTray marketTray = new MarketTray(gson.fromJson(gridUpdate, List.class));
        Board.getBoard().setMarketTray(gson.fromJson(marketUpdate, MarketTray.class));
        Board.getBoard().setDevelopmentCardsGrid(gson.fromJson(gridUpdate, DevelopmentCardsGrid.class));
        Board.getBoard().setFaithTrack(gson.fromJson(faithTracksUpdate, FaithTrack.class));
        view.graphicUpdate();
        //System.out.println(market.getPrintable(20));
    }
}
