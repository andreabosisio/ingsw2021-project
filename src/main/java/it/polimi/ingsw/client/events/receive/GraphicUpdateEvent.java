package it.polimi.ingsw.client.events.receive;

import com.google.gson.*;
import it.polimi.ingsw.client.model.*;
import it.polimi.ingsw.client.view.View;

public class GraphicUpdateEvent implements ReceiveEvent {

    //they are null if not present in json
    private JsonObject gridUpdate;
    private JsonObject marketUpdate;
    private JsonObject faithTracksUpdate;
    private JsonArray personalBoardUpdateList;
    private String messageUpdate;

    @Override
    public void updateView(View view) {
        Gson gson = new Gson();

        if (marketUpdate != null) {
            gson.fromJson(marketUpdate, MarketTray.class).update(view);
        }
        if (gridUpdate != null) {
            gson.fromJson(gridUpdate, DevelopmentCardsGrid.class).update(view);
        }
        if (faithTracksUpdate != null) {
            gson.fromJson(faithTracksUpdate, FaithTrack.class).update(view);
        }
        if (personalBoardUpdateList != null){
            for(JsonElement element : personalBoardUpdateList){
                PersonalBoard personalBoard = gson.fromJson(element.getAsJsonObject(), PersonalBoard.class);
                personalBoard.update(view.getNickname());
            }
        }
        if (messageUpdate != null && !view.isThisClientTurn())
            view.printInfoMessage(messageUpdate);
    }
}
