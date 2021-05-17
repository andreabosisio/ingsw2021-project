package it.polimi.ingsw.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class DevelopmentCardsGrid {
    private List<Map<String, String>> mapByLevel = new ArrayList<>();
    private final String developmentCardsFileName = "src/main/resources/developmentCards.json";
    private final String leaderCardsFileName = "src/main/resources/leaderCards.json";
    private final int CARDS_LEVEL = 3;
    private final int CARDS_COLOR = 2;

    //TODO: Arrives a list of ID's G_3_1, Y_2_4 (the first is the color, the second is the level and the third is the number)
    public DevelopmentCardsGrid(List<String> cardIndexes) {
        firstCardsGrid(cardIndexes);
    }

    public void firstCardsGrid(List<String> cardIndexes) {
        for (int i = 0; i < CARDS_LEVEL; i++) {
            mapByLevel.add(new HashMap<>());
        }

        for (String indexCard : cardIndexes) {
            List<String> splitIndex = Arrays.asList(indexCard.split("_"));
            mapByLevel.get(Integer.parseInt(splitIndex.get(1)) - 1).put(splitIndex.get(0), indexCard);
        }
    }

    public void setDevCard(String color, int level, String id) {
        mapByLevel.get(level - 1).put(color, id);
    }

    public String getPrintable() {

        for (int i = 0; i < CARDS_LEVEL; i++) {
            System.out.println(
                            "╔══════════╗ "  +  "╔══════════╗ " +  "╔══════════╗ " +  "╔══════════╗\n" +
                            "║l        l║ "  +  "║l        l║ " +  "║l        l║ " +  "║l        l║\n" +
                            "║  p p p   ║ "  +  "║  p p p   ║ " +  "║  p p p   ║ " +  "║  p p p   ║\n" +
                            "║──────────║ "  +  "║──────────║ " +  "║──────────║ " +  "║──────────║\n" +
                            "║  i │ o   ║ "  +  "║  i │ o   ║ " +  "║  i │ o   ║ " +  "║  i │ o   ║\n" +
                            "║  i } o   ║ "  +  "║  i } o   ║ " +  "║  i } o   ║ " +  "║  i } o   ║\n" +
                            "║  i │ o   ║ "  +  "║  i │ o   ║ " +  "║  i │ o   ║ " +  "║  i │ o   ║\n" +
                            "║       vv ║ "  +  "║       vv ║ " +  "║       vv ║ " +  "║       vv ║\n" +
                            "╚══════════╝ "  +  "╚══════════╝ " +  "╚══════════╝ " +  "╚══════════╝ ");
        }
        return null;
    }
}
