package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.events.send.ChosenSetupEvent;
import it.polimi.ingsw.client.events.send.LoginEvent;
import it.polimi.ingsw.client.events.send.SelectNumberPlayersEvent;
import it.polimi.ingsw.client.events.send.SendEvent;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.Marble;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * This class listens to inputs from the terminal
 */
public class CLICommandListener implements CommandListener {
    CommandListenerObserver commandListenerObserver;
    private final Scanner scanner = new Scanner(System.in);

    private static final int LEADER_CARDS_TO_CHOOSE = 2;

    protected void askCredentials() {

        System.out.println("Insert a nickname:");
        String nickname = scanner.nextLine();
        commandListenerObserver.setNickname(nickname);

        System.out.println("Insert a password:");
        String password = scanner.nextLine();

        notifyObservers(new LoginEvent(nickname, password));
    }

    protected void askNumberOfPlayers(String payload) {
        System.out.println("Choose number of players (" + payload + ") :");
        String numberOfPlayers = scanner.nextLine();
        try {
            notifyObservers(new SelectNumberPlayersEvent(Integer.parseInt(numberOfPlayers)));
        } catch (NumberFormatException e) {
            CLI.clearView();
            System.out.println(AsciiArts.RED + "Please re-insert a valid number" + AsciiArts.RESET);
            //askNumberOfPlayers(payload);
        }
    }

    protected void askSetupChoice(List<String> leaderCardsIDs, int numberOfResources) {

        List<Integer> chosenIndexes = askLeaderCardsChoice(leaderCardsIDs);
        if(chosenIndexes == null) {
            notifyObservers(new ChosenSetupEvent(null, null));
        } else {
            notifyObservers(new ChosenSetupEvent(chosenIndexes, askResourcesChoice(numberOfResources)));
        }

    }

    private List<Integer> askLeaderCardsChoice(List<String> leaderCardsIDs) {
        List<Integer> chosenIndexes = new ArrayList<>();

        System.out.println(Board.getBoard().getMarketTray().getPrintable(CLI.MARKET_X_SHIFT));

        //fixme indexes (now can choose 2 same indexes)
        for(int i = 0; i < LEADER_CARDS_TO_CHOOSE; i++) {
            System.out.println("Choose a " + AsciiArts.CYAN + "LeaderCard" + AsciiArts.RESET + ": ");
            for (int j = 0; j < leaderCardsIDs.size(); j++) {
                if(chosenIndexes.contains(j))
                    System.out.print(AsciiArts.GREEN_BACKGROUND + ">> " + "[" + j + "] : " + leaderCardsIDs.get(j) + " <<" + AsciiArts.RESET + "\t");
                else
                    System.out.print(AsciiArts.WHITE_BRIGHT + "[" + j + "]: " + AsciiArts.RESET + leaderCardsIDs.get(j) + "\t");
            }
            System.out.println();
            String choice = scanner.nextLine();
            try {
                chosenIndexes.add(Integer.parseInt(choice));
                leaderCardsIDs.get(Integer.parseInt(choice)); //used to trigger IndexOutOfBoundsException
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                CLI.clearView();
                System.out.println(AsciiArts.RED + "Please re-insert a valid number" + AsciiArts.RESET);
                return null;
            }
        }
        CLI.clearView();
        System.out.println(AsciiArts.GREEN + "Valid LeaderCards choices!" + AsciiArts.RESET);
        System.out.println(AsciiArts.WHITE_BOLD_BRIGHT + "Your LeaderCards: " + AsciiArts.RESET);
        for (Integer chosenIndex : chosenIndexes)
            System.out.println(leaderCardsIDs.get(chosenIndex));
        return chosenIndexes;
    }

    private List<String> askResourcesChoice(int numberOfResources) {

        System.out.println(Board.getBoard().getMarketTray().getPrintable(CLI.MARKET_X_SHIFT));

        List<Marble> storableMarbles = new ArrayList<Marble>(){{
            add(new Marble("YELLOW"));
            add(new Marble("GRAY"));
            add(new Marble("PURPLE"));
            add(new Marble("BLUE"));
        }};
        List<String> chosenResourcesColor = new ArrayList<>();

        if(numberOfResources == 0)
            return chosenResourcesColor;

        while (chosenResourcesColor.size() < numberOfResources){
            System.out.println("Choose a " + AsciiArts.CYAN + "resource" + AsciiArts.RESET + ": ");
            for (int j = 0; j < storableMarbles.size(); j ++) {
                System.out.print(AsciiArts.WHITE_BRIGHT + "[" + j + "]: " + AsciiArts.RESET + Marble.getAsciiMarbleByColor(storableMarbles.get(j).getColor()) + "\t\t");
            }
            System.out.println();
            String choice = scanner.nextLine();
            try {
                chosenResourcesColor.add(storableMarbles.get(Integer.parseInt(choice)).getColor());
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                CLI.clearView();
                System.out.println(AsciiArts.RED + "Please re-insert a valid number" + AsciiArts.RESET);
                return null;
            }
        }

        System.out.println(AsciiArts.GREEN + "Valid resources choices!" + AsciiArts.RESET);
        return chosenResourcesColor;
    }

    @Override
    public void notifyObservers(SendEvent sendEvent) {
        commandListenerObserver.update(sendEvent);
    }

    @Override
    public void registerObservers(CommandListenerObserver commandListenerObserver) {
        this.commandListenerObserver = commandListenerObserver;
    }
}
