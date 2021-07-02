package it.polimi.ingsw.server.model.gameBoard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.commons.FileUtilities;
import it.polimi.ingsw.commons.Parser;
import it.polimi.ingsw.server.model.cards.CardsGenerator;
import it.polimi.ingsw.server.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements the Deck of the Leader Cards.
 */
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
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("leaders", Parser.toJsonTree(leaders.stream().map(LeaderCard::getID).collect(Collectors.toList())));
        FileUtilities.writeJsonElementInFile(jsonObject, FileUtilities.SAVED_LEADER_CARD_DATA_PATH);
    }

    /**
     * This method loads from a Json file the ID of the Cards of the Leaders Cards
     */
    public void loadSavedData() {
        leaders.clear();
        JsonElement fileElement = FileUtilities.getJsonElementFromFile(FileUtilities.SAVED_LEADER_CARD_DATA_PATH);
        assert fileElement != null;
        JsonArray jsonArrayOfLeaderIds = Parser.extractFromField(fileElement, "leaders").getAsJsonArray();
        for (JsonElement el : jsonArrayOfLeaderIds) {
            String cardId = el.getAsString();
            leaders.add(generator.generateLeaderCardFromId(cardId));
        }
    }
}