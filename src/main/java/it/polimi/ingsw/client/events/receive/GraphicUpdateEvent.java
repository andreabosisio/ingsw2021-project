package it.polimi.ingsw.client.events.receive;

import com.google.gson.*;
import it.polimi.ingsw.client.model.*;
import it.polimi.ingsw.client.view.View;

import java.util.List;

public class GraphicUpdateEvent implements ReceiveEvent {

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

        if (marketUpdate != null)
            gson.fromJson(marketUpdate, MarketTray.class).update();
        if (gridUpdate != null)
            gson.fromJson(gridUpdate, DevelopmentCardsGrid.class).update();
        if (faithTracksUpdate != null)
            gson.fromJson(faithTracksUpdate, FaithTrack.class).update();

        if(personalBoardUpdateList!=null){
            for(JsonElement element:personalBoardUpdateList){
                gson.fromJson(element.getAsJsonObject(), Player.class).update();
            }
        }


        Board matteo = Board.getBoard();
        view.graphicUpdate();
        //System.out.println(market.getPrintable(20));
    }
}
