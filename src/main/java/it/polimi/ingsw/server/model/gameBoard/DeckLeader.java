package it.polimi.ingsw.server.model.gameBoard;

import com.google.gson.*;
import it.polimi.ingsw.server.model.cards.*;
import it.polimi.ingsw.commons.FileUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeckLeader {
    private final List<LeaderCard> leaders;
    private final CardsGenerator generator = new CardsGenerator();

    /**
     * Constructor of a deck of leader cards
     */
    public DeckLeader() {
        leaders = generator.generateLeaderCards();
        shuffle();
    }

    /**
     * shuffle all the LeaderCards currently in the deck
     */
    public void shuffle() {
        Collections.shuffle(leaders);
    }

    /**
     * Draws the top 4 cards of the deck removing them
     *
     * @return a{@link ArrayList<LeaderCard> of the cards drawn}
     */
    public List<LeaderCard> draw4() {

        //shuffle();

        if (leaders.size() < 4) {
            throw new IndexOutOfBoundsException();
        }
        List<LeaderCard> drawn = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            drawn.add(leaders.get(i));
        }
        leaders.removeAll(drawn);
        return drawn;
    }

    /**
     * This method writes in a Json file the ID of the Card of the Leader Cards
     */
    public void saveData() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("leaders",gson.toJsonTree(leaders.stream().map(LeaderCard::getID).collect(Collectors.toList())));
        FileUtilities.writeJsonElementInFile(jsonObject,FileUtilities.getSavedLeaderCardDataPath());
    }

    /**
     * This method loads from a Json file the ID of the Cards of the Leaders Cards
     */
    public void loadSavedData() {
        JsonObject fileObject;
        leaders.clear();
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.getSavedLeaderCardDataPath());
        assert fileElement != null;
        fileObject = fileElement.getAsJsonObject();
        JsonArray jsonArrayOfLeaderIds = fileObject.get("leaders").getAsJsonArray();
        for (JsonElement el : jsonArrayOfLeaderIds) {
            String cardId = el.getAsString();
            leaders.add(generator.generateLeaderCardFromId(cardId));
        }
    }
}