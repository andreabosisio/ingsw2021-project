package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.events.send.*;
import it.polimi.ingsw.client.model.*;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;

import java.util.*;
import java.util.stream.Collectors;


/**
 * This class listens to inputs from the terminal
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
    private static final String INVALID = "Invalid input";

    //todo enum del server?
    //Todo same thing for RESOURCE_COLORS (create enum)
    /*
    private static final List<String> CARD_COLORS = new ArrayList<String>(){{
        add("GREEN");
        add("PURPLE");
        add("YELLOW");
        add("BLUE");
    }};
    private static final List<String> RESOURCE_COLORS = new ArrayList<String>(){{
        add("GRAY");
        add("YELLOW");
        add("PURPLE");
        add("BLUE");
    }};
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
        List<Printable> toChoose = leaderCardsIDs.stream().map(LeaderCard::new).collect(Collectors.toList());

        for(int i = 0; i < LEADER_CARDS_TO_CHOOSE; i++) {
            CLI.render("Choose a " + AnsiEnum.CYAN + "LeaderCard" + AnsiEnum.RESET + ": ");
            String row = "";
            for (int j = 0; j < leaderCardsIDs.size(); j++) {
                if(chosenIndexes.contains(j))
                    row = PrintableScene.concatenateString(row, AnsiEnum.GREEN_BACKGROUND + ">> " + "[" + j + "] :  <<" + AnsiEnum.RESET + toChoose.get(j).getEmptySpace() + "\t");
                else
                    row = PrintableScene.concatenateString(row, AnsiEnum.WHITE_BRIGHT + "[" + j + "]: " + AnsiEnum.RESET + toChoose.get(j).getEmptySpace() + "\t");
            }
            CLI.render(PrintableScene.addStringToTop(PrintableScene.concatenatePrintable(toChoose, "    " + "\t"), row));
            System.out.println();

            String choice = scanner.nextLine();
            try {
                chosenIndexes.add(Integer.parseInt(choice));
                if(chosenIndexes.stream().distinct().count() < chosenIndexes.size())
                    throw new NumberFormatException();
                leaderCardsIDs.get(Integer.parseInt(choice)); //used to trigger IndexOutOfBoundsException
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                CLI.renderError("Invalid index: please re-choice from scratch");
                return null;
            }
        }
        CLI.clearView();

        CLI.render(Board.getBoard().getPrintableMarketAndGrid());
        CLI.render(AnsiEnum.GREEN + "Valid LeaderCards choices!" + AnsiEnum.RESET);
        CLI.render(AnsiEnum.WHITE_BOLD_BRIGHT + "Your LeaderCards: " + AnsiEnum.RESET);
        toChoose.clear();
        chosenIndexes.forEach(i -> toChoose.add(new LeaderCard(leaderCardsIDs.get(i))));
        CLI.render(PrintableScene.concatenatePrintable( toChoose, "\t\t"));
        System.out.println();
        return chosenIndexes;
    }

    private List<String> askResourcesChoice(int numberOfResources) {

        List<String> chosenResourcesColor = new ArrayList<>();

        if(numberOfResources != 0) {
            while (chosenResourcesColor.size() < numberOfResources) {
                CLI.render("Choose a " + AnsiEnum.CYAN + "resource" + AnsiEnum.RESET + ": ");
                for (int j = 0; j < StorableResourceEnum.values().length; j++) {
                    System.out.print(AnsiEnum.WHITE_BRIGHT + "[" + j + "]: " + AnsiEnum.RESET + Marble.getPrintable(StorableResourceEnum.values()[j].toString()) + "\t\t");
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
            CLI.render(AnsiEnum.GREEN + "Valid resources choices!" + AnsiEnum.RESET);
        }
        if(Board.getBoard().getAllPersonalBoards().size() > 1) {
            System.out.print("Please wait for other players' choices ");
            CLI.showThreePointsAnimation();
        }
        return chosenResourcesColor;
    }

    public void askCardPlacement(){
        int choice = -1;
        while(choice < 1|| choice > 3){
            CLI.render("Select the slot where you wish to place your new card (1, 2 or 3)");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                CLI.renderError(INVALID);
            }
        }
        notifyObservers(new CardPlacementActionEvent(choice));
    }

    public void askResourceTransformation(int numberOfTransformation, List<String> possibleTransformations){
        List<String> transformations = new ArrayList<>();
        CLI.render("UH OH...Looks like your white marbles are evolving\nChoose the color you prefer for this "+numberOfTransformation+" marbles");
        for (int i = 0; i < numberOfTransformation; i++) {
            System.out.print("Resource in slot n°" + (i + 1) + " can be: ");
            for(int j = 0; j < possibleTransformations.size(); j++){
                String color = possibleTransformations.get(j);
                System.out.print((j+1) + Marble.getPrintable(color.toUpperCase(Locale.ROOT))+" ");
            }
            System.out.print("\n");
            int choice = -1;
            while (choice<1||choice>possibleTransformations.size()){
                CLI.render("Select between 1 and 2");
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                }catch (NumberFormatException e){
                    CLI.renderError(INVALID);
                }
            }
            transformations.add(possibleTransformations.get(choice-1));
        }
        notifyObservers(new TransformationActionEvent(transformations));
    }

    public String askFirstAction(){
        String answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        while (!(answer.equals(CommandsEnum.MARKET.toString()) || answer.equals(CommandsEnum.BUY.toString()) || answer.equals(CommandsEnum.PRODUCTION.toString()) || answer.equals(CommandsEnum.SEE.toString()) || answer.equals(CommandsEnum.LEADER.toString()))){
            CLI.renderError("Invalid action, try again");
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        }
        return answer;
    }

    public boolean askMarketAction(){
        int choice = -1;
        CLI.render("Select an arrow (from 0 to 6) or type "+ CommandsEnum.BACK +" to change action: ");
        while(choice < MIN_MARKET_ARROW_ID || choice > MAX_MARKET_ARROW_ID){
            try {
                String input = scanner.nextLine().toUpperCase(Locale.ROOT);
                if(input.equals(CommandsEnum.BACK.toString()))
                    return false;
                else
                    choice = Integer.parseInt(input);
            } catch (NumberFormatException e){
                CLI.renderError(INVALID);
            }
        }
        notifyObservers(new MarketActionEvent(choice));
        return true;
    }

    public void askResourcePlacement(){
        CLI.render("Write your swaps (es: 0,4,1,5...) or type "+CommandsEnum.DONE+" for no swaps:");
        List<Integer> swaps = new ArrayList<>();
        String answer;
        do {
             answer = scanner.nextLine().toUpperCase(Locale.ROOT);
             if(answer.equals(CommandsEnum.DONE.toString()) || answer.equals(CommandsEnum.REFRESH.toString()))
                 break;
            try {
                //regex means any number of spaces before and after the comma
                swaps.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).collect(Collectors.toList()));
                CLI.render("Swaps saved. Type "+CommandsEnum.REFRESH+" to update the view or "+CommandsEnum.DONE+" to end the placement");
            }catch (IndexOutOfBoundsException | NumberFormatException e){
                CLI.renderError(INVALID);
            }
        } while(!answer.equals(CommandsEnum.REFRESH.toString()) && !answer.equals(CommandsEnum.DONE.toString()));
        notifyObservers(new ResourcesPlacementActionEvent(swaps, answer.equals(CommandsEnum.DONE.toString())));
    }

    //return true if leader action
    public boolean askEndAction(){
        CLI.render("Type "+ CommandsEnum.DONE +" to end your turn or "+ CommandsEnum.LEADER +" to perform a leader action");
        String answer = scanner.nextLine().toUpperCase(Locale.ROOT);

        while (!(answer.equals(CommandsEnum.DONE.toString()) || answer.equals(CommandsEnum.LEADER.toString()))){
            CLI.renderError(INVALID);
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        }
        if(answer.equals(CommandsEnum.DONE.toString())){
            notifyObservers(new EndTurnActionEvent());
            return false;
        }
        return true;
    }

    public boolean askLeaderAction(){
        List<String> hand = Board.getBoard().getPersonalBoardOf(nickname).getHandLeaders();
        int index = -1;
        if(hand.size() != 0){
            while (index < 0 || index > hand.size() - 1){
                System.out.println("Choose the Leader Card slot by index (0 or 1)");
                try {
                    index = Integer.parseInt(scanner.nextLine());
                }catch (NumberFormatException e){
                    CLI.renderError(INVALID);
                }
            }
            CLI.render("Type " + CommandsEnum.ACTIVATE + " to activate the card or " + CommandsEnum.DISCARD + " to discard the card: ") ;
            String discard = scanner.nextLine().toUpperCase(Locale.ROOT);
            while (!discard.equals(CommandsEnum.ACTIVATE.toString()) && !discard.equals(CommandsEnum.DISCARD.toString())){
                CLI.renderError(INVALID);
                discard = scanner.nextLine().toUpperCase(Locale.ROOT);
            }
            notifyObservers(new LeaderActionEvent(hand.get(index), discard.equals(CommandsEnum.DISCARD.toString())));
            return true;
        }
        return false;
        //notifyObservers(new LeaderActionEvent(null,true));
    }

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
                if(answer.equals(CommandsEnum.BACK.toString()))
                    return false;
                level = Integer.parseInt(answer);
            } catch (NumberFormatException e) {
                CLI.renderError(INVALID);
            }
        }
        while (Arrays.stream(CardColorsEnum.values()).map(Enum::toString).collect(Collectors.toList()).contains(color.toUpperCase(Locale.ROOT))) {
            CLI.render("Choose the color of the card to buy (green, blue, yellow or purple)");
            color = scanner.nextLine();
        }

        CLI.render("Choose which resources do you want to use to pay the card (ex: 4,13, ... ,9)");
        answer = scanner.nextLine();
        while (true) {
            try {
                //Only natural numbers are accepted and any duplicate are ignored
                resources.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).filter(n->n>0).collect(Collectors.toSet()));
                break;
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                CLI.renderError(INVALID);
            }
            answer = scanner.nextLine();
        }
        notifyObservers(new BuyActionEvent(color,level,resources));
        return true;
    }

    //todo test and/or cut in smaller functions
    public boolean askProductionAction() {
        Map<Integer, List<Integer>> inResourcesForEachProductions = new HashMap<>();
        Map<Integer, String> outResourcesForEachProductions = new HashMap<>();
        Set<Integer> inResourcesStory = new HashSet<>();
        Set<Integer> indexesStory = new HashSet<>();
        List<Integer> inResources;
        CLI.render("Choose the Production Slot you want to activate (from 0 to 5). Type " + CommandsEnum.DONE + " when finished or "+CommandsEnum.BACK+" to change action:");
        String answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        if(answer.equals(CommandsEnum.BACK.toString()))
            return false;
        while (!answer.equals(CommandsEnum.DONE.toString())){
            int index;
            String outResource = null;
            //instantiated here to be set as null for every new card
            try {
                index = Integer.parseUnsignedInt(answer);
                if(indexesStory.contains(index)){
                    CLI.renderError("You have already used this card, try a new one");
                    answer = scanner.nextLine().toUpperCase(Locale.ROOT);
                    continue;
                }
                //used to launch exception if card is not owned
                indexesStory.add(index);
            }catch (NumberFormatException e){
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
                    inResources.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).filter(n->n>0).collect(Collectors.toSet()));
                    //if a resource is repeated, ask to re-choose resources
                    if(inResources.stream().anyMatch(inResourcesStory::contains)){
                        CLI.renderError("This resources were already used");
                    }
                    else
                        break;
                } catch (NumberFormatException e) {
                    CLI.renderError(INVALID);
                }
                answer = scanner.nextLine().toUpperCase(Locale.ROOT);
            }
            if(index == 0 || index >= 4){
                do {
                    CLI.render("Choose the resource you wish to produce (ex: blue or gray...)");
                    outResource = scanner.nextLine().toUpperCase(Locale.ROOT);
                } while(!Arrays.stream(StorableResourceEnum.values()).map(Enum::toString).collect(Collectors.toList()).contains(outResource));
            }
            inResourcesStory.addAll(inResources);
            inResourcesForEachProductions.put(index,inResources);
            outResourcesForEachProductions.put(index,outResource);
            CLI.render("Choose the Production Slot you want to activate (from 0 to 5). Type " + CommandsEnum.DONE + " when finished or "+CommandsEnum.BACK+" to change action:");
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        }
        notifyObservers(new ProductionActionEvent(inResourcesForEachProductions, outResourcesForEachProductions));
        return true;
    }

    public String askSeeChoice(){
        CLI.render("Type GRIDS to see the Market Tray and the Development Cards Grid or PLAYER to see a player. Type DONE to change action.");
        String answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        while(!answer.equals(CommandsEnum.GRIDS.toString()) && !answer.equals(CommandsEnum.DONE.toString()) && !answer.equals(CommandsEnum.PLAYER.toString())){
            CLI.renderError(INVALID);
            answer = scanner.nextLine().toUpperCase(Locale.ROOT);
        }
        return answer;

    }

    public String askSeePlayerChoice(){
        List<String> nicknames = Board.getBoard().getAllPersonalBoards().stream().map(PersonalBoard::getNickname).collect(Collectors.toList());
        CLI.render("Players you can see are: " + nicknames.stream().filter(n -> !n.equals(nickname)).collect(Collectors.toList()) + ". Choose one:");
        String answer = scanner.nextLine();
        while (!nicknames.contains(answer)){
            CLI.renderError(answer + " does not exist, try again");
            answer = scanner.nextLine();
        }
        return answer;
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
