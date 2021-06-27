package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.cards.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckLeader {
    private final List<LeaderCard> leaders;
    private final CardsGenerator generator = new CardsGenerator();
    private final static String SAVED_CARD_DATA_PATH = "src/main/resources/deckLeaderSaved.json";

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
        try (FileWriter file = new FileWriter(SAVED_CARD_DATA_PATH)) {
            //We can write any JSONArray or JSONObject instance to the file
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("leaders",gson.toJsonTree(leaders.stream().map(LeaderCard::getID).collect(Collectors.toList())));
            gson.toJson(jsonObject,file);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method loads from a Json file the ID of the Cards of the Leaders Cards
     */
    public void loadSavedData() {
        File input = new File(SAVED_CARD_DATA_PATH);
        JsonObject fileObject;
        leaders.clear();
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfLeaderIds = fileObject.get("leaders").getAsJsonArray();
            for(JsonElement el:jsonArrayOfLeaderIds){
                String cardId = el.getAsString();
                leaders.add(generator.generateLeaderCardFromId(cardId));
            }
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("error in the json file format");
            e.printStackTrace();
        }
    }
}