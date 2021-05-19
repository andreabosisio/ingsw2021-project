package it.polimi.ingsw;

import com.google.gson.Gson;

import it.polimi.ingsw.client.model.DevelopmentCardsGrid;
import it.polimi.ingsw.client.model.Marble;
import it.polimi.ingsw.server.events.receive.MarketReceiveEvent;
import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.gameBoard.GameBoard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
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
        //askCardPlacement code
        int choice = -1;
        while(choice<1||choice>3){
            System.out.println("Select where you wish to place your new card(1-2-3)");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("invalid input");
            }
        }
        System.out.println("sendEvent called here");


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
    }
}

