package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.ClientApp;
import it.polimi.ingsw.client.events.send.*;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.LeaderCard;
import it.polimi.ingsw.client.model.Marble;
import it.polimi.ingsw.client.model.PersonalBoard;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;
import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.commons.enums.StorableResourceEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class listens to inputs from the terminal and through the information taken from the users,
 * creates the specific Event to send to the Server, for that it notify the class NetworkHandler.
 */
public class CLICommandListener implements CommandListener {
    private CommandListenerObserver commandListenerObserver;
    private final Scanner scanner = new Scanner(System.in);
    private String nickname;

    private static final int LEADER_CARDS_TO_CHOOSE = 2;
    private static final int MAX_MARKET_ARROW_ID = 6;
    private static final int MIN_MARKET_ARROW_ID = 0;
    private static final int MAX_CARD_LEVEL = 3;
    private static final int MIN_CARD_LEVEL = 1;
    private static final String INVALID_MSG = "Invalid input";

    /**
     * Asks to the Player the desired game mode: online or local.
     *
     * @return the answer of the Player
     */
    protected String askGameMode() {
        String answer;
        do {
            CLI.render("Do you want to play online or locally? Type ONLINE or LOCAL: ");
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        } while (!answer.equals("ONLINE") && !answer.equals("LOCAL"));
        return answer;
    }

    /**
     * Asks to the Player the desired IP address of the remote server.
     *
     * @return the answer of the Player
     */
    protected String askIP() {
        CLI.render("Insert a valid IP:");
        return scanner.nextLine();
    }

    /**
     * Asks to the Player the desired port of the remote server.
     *
     * @return the answer of the Player
     */
    protected int askPort() {
        int port = -1;
        CLI.render("Insert a valid port: ");
        do {
            try {
                port = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                CLI.renderError("Invalid port. Please re-insert: ");
            }
        } while (port < 0);
        return port;
    }

    /**
     * Asks to the Player if it wants to change network settings (IP and port of the server).
     *
     * @return the answer of the Player
     */
    protected String askForNetworkSettingsChanges() {
        CLI.render("Default IP address is " + ClientApp.DEFAULT_IP + " and Default port is " + ClientApp.DEFAULT_PORT + ". \nType CHANGE if you want to change network settings, else type OK: ");
        return scanner.nextLine().toUpperCase(Locale.ROOT);
    }

    /**
     * This method is used to ask a player for the credentials he wish to use in game
     */
    protected void askCredentials() {
        CLI.render("Insert a nickname:");
        String nickname = scanner.nextLine();
        this.nickname = nickname;
        commandListenerObserver.setNickname(nickname);

        CLI.render("Insert a password:");
        String password = scanner.nextLine();

        notifyObservers(new LoginEvent(nickname, password));
    }

    /**
     * This method is used to ask a player the number of players he wish to play against
     *
     * @param payload the valid numbers to choose from
     */
    protected void askNumberOfPlayers(String payload) {
        CLI.render("Choose the number of players (" + payload + ") :");
        while (true) {
            String numberOfPlayers = scanner.nextLine();
            try {
                notifyObservers(new SelectNumberPlayersEvent(Integer.parseInt(numberOfPlayers)));
                break;
            } catch (NumberFormatException e) {
                CLI.renderError("Please re-insert a valid number");
            }
        }
    }

    /**
     * This method is used to ask a player his setupChoices
     *
     * @param leaderCardsIDs    IDs of the cards he can choose from
     * @param numberOfResources number of resources he can choose
     */
    protected void askSetupChoice(List<String> leaderCardsIDs, int numberOfResources) {

        List<Integer> chosenIndexes = askLeaderCardsChoice(leaderCardsIDs);
        if (chosenIndexes == null) {
            notifyObservers(new ChosenSetupEvent(null, null));
        } else {
            notifyObservers(new ChosenSetupEvent(chosenIndexes, askResourcesChoice(numberOfResources)));
        }
    }

    /**
     * This method is used to ask a player his setup leadersChoices
     *
     * @param leaderCardsIDs IDS of the leaders he can choose from
     * @return the list of chosen leadersCardIDs
     */
    private List<Integer> askLeaderCardsChoice(List<String> leaderCardsIDs) {
        List<Integer> chosenIndexes = new ArrayList<>();
        List<Printable> toChoose = leaderCardsIDs.stream().map(LeaderCard::new).collect(Collectors.toList());

        for (int i = 0; i < LEADER_CARDS_TO_CHOOSE; i++) {
            CLI.render("Choose a " + AnsiUtilities.CYAN + "LeaderCard" + AnsiUtilities.RESET + ": ");
            String row = "";
            for (int j = 0; j < leaderCardsIDs.size(); j++) {
                if (chosenIndexes.contains(j))
                    row = PrintableScene.concatenateStrings(row, AnsiUtilities.GREEN_BACKGROUND + ">> " + "[" + j + "] :  <<" + AnsiUtilities.RESET + toChoose.get(j).getEmptySpace() + "\t");
                else
                    row = PrintableScene.concatenateStrings(row, AnsiUtilities.WHITE_BRIGHT + "[" + j + "]: " + AnsiUtilities.RESET + toChoose.get(j).getEmptySpace() + "\t");
            }
            CLI.render(PrintableScene.addStringToTop(PrintableScene.concatenatePrintables(toChoose, "    " + "\t"), row));
            System.out.println();

            String choice = scanner.nextLine();
            try {
                chosenIndexes.add(Integer.parseInt(choice));
                if (chosenIndexes.stream().distinct().count() < chosenIndexes.size())
                    throw new NumberFormatException();
                leaderCardsIDs.get(Integer.parseInt(choice)); //used to trigger IndexOutOfBoundsException
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                CLI.renderError("Invalid index: please re-choice from scratch");
                return null;
            }
        }
        CLI.clearView();

        CLI.render(Board.getBoard().getPrintableMarketAndGrid());
        CLI.render(AnsiUtilities.GREEN + "Valid LeaderCards choices!" + AnsiUtilities.RESET);
        CLI.render(AnsiUtilities.WHITE_BOLD_BRIGHT + "Your LeaderCards: " + AnsiUtilities.RESET);
        toChoose.clear();
        chosenIndexes.forEach(i -> toChoose.add(new LeaderCard(leaderCardsIDs.get(i))));
        CLI.render(PrintableScene.concatenatePrintables(toChoose, "\t\t"));
        System.out.println();
        return chosenIndexes;
    }

    /**
     * This method is used to ask a player the resources he wish to start with
     *
     * @param numberOfResources number of resources he can choose
     * @return a list containing the chosen resources colors
     */
    private List<String> askResourcesChoice(int numberOfResources) {

        List<String> chosenResourcesColor = new ArrayList<>();

        if (numberOfResources != 0) {
            while (chosenResourcesColor.size() < numberOfResources) {
                CLI.render("Choose a " + AnsiUtilities.CYAN + "resource" + AnsiUtilities.RESET + ": ");
                for (int j = 0; j < StorableResourceEnum.values().length; j++) {
                    System.out.print(AnsiUtilities.WHITE_BRIGHT + "[" + j + "]: " + AnsiUtilities.RESET + Marble.getPrintable(StorableResourceEnum.values()[j].toString()) + "\t\t");
                }
                System.out.println();
                String choice = scanner.nextLine();
                try {
                    chosenResourcesColor.add(StorableResourceEnum.values()[Integer.parseInt(choice)].toString());
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    CLI.clearView();
                    CLI.renderError("Invalid index: please re-choice from scratch");
                    return null;
                }
            }
            CLI.render(AnsiUtilities.GREEN + "Valid resources choices!" + AnsiUtilities.RESET);
        }
        if (Board.getBoard().getAllPersonalBoards().size() > 1) {
            System.out.print("Please wait for other players' choices ");
            CLI.showThreePointsAnimation();
        }
        return chosenResourcesColor;
    }

    /**
     * This method is used to ask a player where he wish to place a new devCard
     */
    public void askCardPlacement() {
        int choice = -1;
        while (choice < 1 || choice > 3) {
            CLI.render("Select the slot where you wish to place your new card (1, 2 or 3)");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                CLI.renderError(INVALID_MSG);
            }
        }
        notifyObservers(new CardPlacementActionEvent(choice));
    }

    /**
     * This method is used to ask a player the transformation he wants to apply to his white resources
     *
     * @param numberOfTransformation  number of white resources to transform
     * @param possibleTransformations possible colors in which the white resources can transform
     */
    public void askResourceTransformation(int numberOfTransformation, List<String> possibleTransformations) {
        List<String> transformations = new ArrayList<>();
        CLI.render("UH OH...Looks like your white marbles are evolving\nChoose the color you prefer for this " + numberOfTransformation + " marbles");
        for (int i = 0; i < numberOfTransformation; i++) {
            CLI.render("Resource in slot n°" + (i + 1) + " can be: ");
            for (int j = 0; j < possibleTransformations.size(); j++) {
                String color = possibleTransformations.get(j);
                System.out.print("[" + (j + 1) + "]" + Marble.getPrintable(color.toUpperCase(Locale.ROOT)) + " \t\t");
            }
            CLI.render("");
            int choice = -1;
            while (choice < 1 || choice > possibleTransformations.size()) {
                CLI.render("Select between 1 and 2:");
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    CLI.renderError(INVALID_MSG);
                }
            }
            transformations.add(possibleTransformations.get(choice - 1));
        }
        notifyObservers(new TransformationActionEvent(transformations));
    }

    /**
     * This method is used to ask a player with which action he wish to start his turn (market-buy-production-see-leader)
     *
     * @return the player choice
     */
    public String askFirstAction() {
        String answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        while (!(answer.equals(CommandsEnum.MARKET.toString()) || answer.equals(CommandsEnum.BUY.toString()) || answer.equals(CommandsEnum.PRODUCTION.toString()) || answer.equals(CommandsEnum.SEE.toString()) || answer.equals(CommandsEnum.LEADER.toString()))) {
            CLI.renderError("Invalid action, try again");
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        }
        return answer;
    }

    /**
     * This method is used to ask a player which column or row in the marketTray he wish to take
     *
     * @return false if the player decided to go back to the starting choice
     */
    public boolean askMarketAction() {
        int choice = -1;
        do {
            CLI.render("Select an arrow (from 0 to 6) or type " + CommandsEnum.BACK + " to change action: ");
            try {
                String input = scanner.nextLine().toUpperCase(Locale.ROOT);
                if (input.equals(CommandsEnum.BACK.toString()))
                    return false;
                else
                    choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                CLI.renderError(INVALID_MSG);
            }
        } while (choice < MIN_MARKET_ARROW_ID || choice > MAX_MARKET_ARROW_ID);
        notifyObservers(new MarketActionEvent(choice));
        return true;
    }

    /**
     * This method is used to ask a player where he wish to place his new resources
     * After the swaps are inserted he can then choose to submit them or to show how they will affect the warehouse
     */
    public void askResourcePlacement() {
        CLI.render("Write your swaps (es: 0,4,1,5...) or type " + CommandsEnum.DONE + " to end swaps:");
        List<Integer> swaps = new ArrayList<>();
        String answer;
        do {
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
            if (answer.equals(CommandsEnum.DONE.toString()) || answer.equals(CommandsEnum.REFRESH.toString()))
                break;
            try {
                //regex means any number of spaces before and after the comma
                swaps.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).collect(Collectors.toList()));
                CLI.render("Swaps saved. Type " + CommandsEnum.REFRESH + " to update the view or " + CommandsEnum.DONE + " to end the placement");
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                CLI.renderError(INVALID_MSG);
            }
        } while (!answer.equals(CommandsEnum.REFRESH.toString()) && !answer.equals(CommandsEnum.DONE.toString()));
        notifyObservers(new ResourcesPlacementActionEvent(swaps, answer.equals(CommandsEnum.DONE.toString())));
    }

    /**
     * This method is used to ask a player what he wishes to do in his endTurn phase(leader action/end)
     *
     * @return true if the player wishes to perform a leader action
     */
    public boolean askEndAction() {
        CLI.render("Type " + CommandsEnum.DONE + " to end your turn or " + CommandsEnum.LEADER + " to perform a leader action");
        String answer = scanner.nextLine().toUpperCase(Locale.ROOT);

        while (!(answer.equals(CommandsEnum.DONE.toString()) || answer.equals(CommandsEnum.LEADER.toString()))) {
            CLI.renderError(INVALID_MSG);
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        }
        if (answer.equals(CommandsEnum.DONE.toString())) {
            notifyObservers(new EndTurnActionEvent());
            return false;
        }
        return true;
    }

    /**
     * This method is used to ask a player what leaderActon he wishes to perform(discard/activate)
     *
     * @return false if the player has no leaders in his hand
     */
    public boolean askLeaderAction() {
        List<String> hand = Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders();
        int index = -1;
        if (hand.size() != 0) {
            while (index < 0 || index > hand.size() - 1) {
                System.out.println("Choose the Leader Card slot by index (0 or 1)");
                try {
                    index = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    CLI.renderError(INVALID_MSG);
                }
            }
            CLI.render("Type " + CommandsEnum.ACTIVATE + " to activate the card or " + CommandsEnum.DISCARD + " to discard the card: ");
            String discard = scanner.nextLine().toUpperCase(Locale.ROOT);
            while (!discard.equals(CommandsEnum.ACTIVATE.toString()) && !discard.equals(CommandsEnum.DISCARD.toString())) {
                CLI.renderError(INVALID_MSG);
                discard = scanner.nextLine().toUpperCase(Locale.ROOT);
            }
            notifyObservers(new LeaderActionEvent(hand.get(index), discard.equals(CommandsEnum.DISCARD.toString())));
            return true;
        }
        return false;
        //notifyObservers(new LeaderActionEvent(null,true));
    }

    /**
     * This method is used to ask a player which card he wishes to buy and the resources he wish to use
     *
     * @return false if the player wishes to go back to the start turn phase
     */
    public boolean askBuyAction() {
        //PersonalBoard player = Board.getBoard().getPlayerByNickname(nickname);
        int level = -1;
        String color = "";
        String answer;
        List<Integer> resources = new ArrayList<>();
        while (level < MIN_CARD_LEVEL || level > MAX_CARD_LEVEL) {
            CLI.render("Choose the level of the card you want to buy (between " + MIN_CARD_LEVEL + " and " + MAX_CARD_LEVEL + ") or type " + CommandsEnum.BACK + " to change action:");
            try {
                answer = scanner.nextLine().toUpperCase(Locale.ROOT);
                if (answer.equals(CommandsEnum.BACK.toString()))
                    return false;
                level = Integer.parseInt(answer);
            } catch (NumberFormatException e) {
                CLI.renderError(INVALID_MSG);
            }
        }
        while (!Arrays.stream(CardColorsEnum.values()).map(Enum::toString).collect(Collectors.toList()).contains(color.toUpperCase(Locale.ROOT))) {
            CLI.render("Choose the color of the card to buy (green, blue, yellow or purple)");
            color = scanner.nextLine();
        }

        CLI.render("Choose which resources do you want to use to pay the card (ex: 4,13, ... ,9)");
        answer = scanner.nextLine();
        while (true) {
            try {
                //Only natural numbers are accepted and any duplicate are ignored
                resources.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).filter(n -> n > 0).collect(Collectors.toSet()));
                break;
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                CLI.renderError(INVALID_MSG);
            }
            answer = scanner.nextLine();
        }
        notifyObservers(new BuyActionEvent(color, level, resources));
        return true;
    }

    /**
     * This method is used to ask a player which cards and which resources he wishes to use in his productions
     *
     * @return false if the player wishes to go back to the start turn phase
     */
    public boolean askProductionAction() {
        Map<Integer, List<Integer>> inResourcesForEachProductions = new HashMap<>();
        Map<Integer, String> outResourcesForEachProductions = new HashMap<>();
        Set<Integer> inResourcesStory = new HashSet<>();
        Set<Integer> indexesStory = new HashSet<>();
        List<Integer> inResources;

        CLI.render("Choose the Production Slot you want to activate (from 0 to 5). Type " + CommandsEnum.DONE + " when finished or " + CommandsEnum.BACK + " to change action:");
        String answer = scanner.nextLine().toUpperCase(Locale.ROOT);

        if (answer.equals(CommandsEnum.BACK.toString()))
            return false;

        while (!answer.equals(CommandsEnum.DONE.toString())) {
            int index;
            String outResource = null;
            //instantiated here to be set as null for every new card
            try {
                index = Integer.parseUnsignedInt(answer);
                if (indexesStory.contains(index)) {
                    CLI.renderError("You have already used this card, try a new one");
                    answer = scanner.nextLine().toUpperCase(Locale.ROOT);
                    continue;
                }
                //used to throw an exception if card is not owned
                indexesStory.add(index);
            } catch (NumberFormatException e) {
                CLI.renderError("Couldn't get the selected card, try again");
                answer = scanner.nextLine().toUpperCase(Locale.ROOT);
                continue;
            }

            CLI.render("Choose the resources you wish to use (ex: 4,13, ... ,9)");
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
            while (true) {
                //instantiated here to be reset if failed input
                inResources = new ArrayList<>();
                try {
                    //Only natural numbers are accepted and any duplicate are ignored
                    inResources.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).filter(n -> n > 0).collect(Collectors.toSet()));
                    //if a resource is repeated, ask to re-choose resources
                    if (inResources.stream().anyMatch(inResourcesStory::contains)) {
                        CLI.renderError("This resources were already used, please re-choose the resources you wish to use (ex: 4,13, ... ,9)");
                    } else
                        break;
                } catch (NumberFormatException e) {
                    CLI.renderError(INVALID_MSG);
                }
                answer = scanner.nextLine().toUpperCase(Locale.ROOT);
            }

            if (index == 0 || index >= 4) {
                do {
                    CLI.render("Choose the resource you wish to produce (ex: blue or gray...)");
                    outResource = scanner.nextLine().toUpperCase(Locale.ROOT);
                } while (!Arrays.stream(StorableResourceEnum.values()).map(Enum::toString).collect(Collectors.toList()).contains(outResource));
            }
            inResourcesStory.addAll(inResources);
            inResourcesForEachProductions.put(index, inResources);
            outResourcesForEachProductions.put(index, outResource);
            CLI.render("Choose the Production Slot you want to activate (from 0 to 5). Type " + CommandsEnum.DONE + " when finished or " + CommandsEnum.BACK + " to change action:");
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        }
        notifyObservers(new ProductionActionEvent(inResourcesForEachProductions, outResourcesForEachProductions));
        return true;
    }

    /**
     * This method is used to ask a player what part of the board he wishes to see in particular(grids-players)
     *
     * @return the player decision
     */
    public String askSeeChoice() {
        CLI.render("Type GRIDS to see the Market Tray and the Development Cards Grid or PLAYER to see a player. Type DONE to change action.");
        String answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        while (!answer.equals(CommandsEnum.GRIDS.toString()) && !answer.equals(CommandsEnum.DONE.toString()) && !answer.equals(CommandsEnum.PLAYER.toString())) {
            CLI.renderError(INVALID_MSG);
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        }
        return answer;

    }

    /**
     * This method is used to ask a player which enemyPlayer board che wishes to see
     *
     * @return the nickname of the selected player
     */
    public String askSeePlayerChoice() {
        List<String> nicknames = Board.getBoard().getAllPersonalBoards().stream().map(PersonalBoard::getNickname).collect(Collectors.toList());
        CLI.render("Players you can see are: " + nicknames.stream().filter(n -> !n.equals(nickname)).collect(Collectors.toList()) + ". Choose one:");
        String answer = scanner.nextLine();
        while (!nicknames.contains(answer)) {
            CLI.renderError(answer + " does not exist, try again");
            answer = scanner.nextLine();
        }
        return answer;
    }


    /**
     * This method is used to notify the commandListenerObserver of the player choices
     *
     * @param eventToServer event containing the player choices data
     */
    @Override
    public void notifyObservers(EventToServer eventToServer) {
        commandListenerObserver.update(eventToServer);
    }

    /**
     * This method is used to register a commandListenerObserver as observer of this class
     *
     * @param commandListenerObserver observer interested in the player actions
     */
    @Override
    public void registerObservers(CommandListenerObserver commandListenerObserver) {
        this.commandListenerObserver = commandListenerObserver;
    }
}
