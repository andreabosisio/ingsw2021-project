package it.polimi.ingsw;

import it.polimi.ingsw.client.model.FaithTrack;
import it.polimi.ingsw.client.model.MarketTray;
import com.google.gson.Gson;

import it.polimi.ingsw.client.model.DevelopmentCardsGrid;
import it.polimi.ingsw.client.model.Marble;
import it.polimi.ingsw.server.events.receive.MarketReceiveEvent;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {

        MarketTray marketTray = new MarketTray(new it.polimi.ingsw.server.model.gameBoard.MarketTray().toStringList());
        List<String> marketRows = marketTray.getPrintable();
        for(String marketRow : marketRows)
            System.out.println(marketRow);

        Boolean[] booleans = {false, false, false};

        FaithTrack faithTrack = new FaithTrack(new HashMap<String, Integer>(){{
            put("p1", 0);
            put("p2", 1);
            put("p3", 1);
            put("p4", 2);
        }}, new HashMap<String, Boolean[]>() {{
            put("p1", booleans);
            put("p2", booleans);
            put("p3", booleans);
            put("p4", booleans);
        }});
        faithTrack.getPrintable().forEach(System.out::println);

        FaithTrack faithTrack2 = new FaithTrack(new HashMap<String, Integer>(){{
            put("p1", 3);
            put("p2", 16);
            put("p3", 8);
            put("p4", 8);
        }}, new HashMap<String, Boolean[]>(){{

            put("p1", booleans);

            Boolean[] bool2 = {true, true, false};
            put("p2", bool2);

            Boolean[] bool34 = {true, false, false};
            put("p3", bool34);
            put("p4", bool34);
        }});
        faithTrack2.getPrintable().forEach(System.out::println);




        //System.out.println(AsciiArts.EMPTY_RES);

        Scanner scanner = new Scanner(System.in);
        int numberOfTransformation = 3;
        List<String> possibleTransformations = new ArrayList<String>() {{
            add("YELLOW");
            add("GRAY");
        }};

        DevelopmentCardsGrid cardsGrid = new DevelopmentCardsGrid(GameBoard.getGameBoard().getDevelopmentCardsGrid().getAvailableCards().stream().map(DevelopmentCard::getID).collect(Collectors.toList()));
        cardsGrid.setDevelopmentCard("YELLOW", 3, "empty");

        List<String> cardsGridList = cardsGrid.getPrintableDevelopmentCardsGrid();
        for(String riga : cardsGridList)
            System.out.println(riga);
        /*
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("1");
        arrayList.add("2");
        arrayList.add("3");
        arrayList.add("4");
        arrayList.add("5");
        arrayList.subList(2, arrayList.size()).clear();
        System.out.println(arrayList.size());


         */



        /*
        //askResourceTransformation code
        List<String> transformations = new ArrayList<>();
        System.out.println("Looks like your white marbles are evolving\nChoose the color you prefer for this "+numberOfTransformation+" marbles");
        for (int i = 0; i < numberOfTransformation; i++) {
            System.out.print("Resource n" + (i + 1) + " can be: ");
            for(int j = 0;j<possibleTransformations.size();j++){
                String color = possibleTransformations.get(j);
                System.out.print((j+1)+Marble.getAsciiMarbleByColor(color.toUpperCase(Locale.ROOT))+" ");
            }
            System.out.print("\n");
            int choice = -1;
            while (choice<1||choice>possibleTransformations.size()){
                System.out.println("Select between 1 and 2");
                try {
                    choice=Integer.parseInt(scanner.nextLine());
                }catch (NumberFormatException e){
                    System.out.println("Not a number");
                }
            }
            transformations.add(possibleTransformations.get(choice-1));
        }
        System.out.println("you selected"+transformations);


         */
        MarketReceiveEvent event = new MarketReceiveEvent("matteo",1);
        Gson gson = new Gson();
        String json = gson.toJson(event);
        System.out.println(json);

/*
        for (int i = 0; i < 3; i++) {
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
        */
    }
}

