package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.events.send.*;
import it.polimi.ingsw.client.model.Board;
import it.polimi.ingsw.client.model.LeaderCard;
import it.polimi.ingsw.client.model.Marble;
import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.client.utils.CommandListener;
import it.polimi.ingsw.client.utils.CommandListenerObserver;

import java.util.*;
import java.util.stream.Collectors;


/**
 * This class listens to inputs from the terminal
 */
public class CLICommandListener implements CommandListener {
    CommandListenerObserver commandListenerObserver;
    private final Scanner scanner = new Scanner(System.in);
    private String nickname;

    private static final int LEADER_CARDS_TO_CHOOSE = 2;
    private static final int MAX_MARKET_ARROW_ID = 6;
    private static final int MIN_MARKET_ARROW_ID = 0;
    private static final int MAX_CARD_LEVEL = 3;
    private static final int MIN_CARD_LEVEL = 1;
    private static final String INVALID = "Invalid input";
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
        CLI.render("Choose number of players (" + payload + ") :");

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

        List<Marble> storableMarbles = new ArrayList<Marble>(){{
            add(new Marble("YELLOW"));
            add(new Marble("GRAY"));
            add(new Marble("PURPLE"));
            add(new Marble("BLUE"));
        }};
        List<String> chosenResourcesColor = new ArrayList<>();

        if(numberOfResources != 0) {
            while (chosenResourcesColor.size() < numberOfResources) {
                CLI.render("Choose a " + AnsiEnum.CYAN + "resource" + AnsiEnum.RESET + ": ");
                for (int j = 0; j < storableMarbles.size(); j++) {
                    System.out.print(AnsiEnum.WHITE_BRIGHT + "[" + j + "]: " + AnsiEnum.RESET + Marble.getPrintable(storableMarbles.get(j).getColor()) + "\t\t");
                }
                System.out.println();
                String choice = scanner.nextLine();
                try {
                    chosenResourcesColor.add(storableMarbles.get(Integer.parseInt(choice)).getColor());
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    CLI.clearView();
                    CLI.renderError("Invalid index: please re-choice from scratch");
                    return null;
                }
            }
            CLI.render(AnsiEnum.GREEN + "Valid resources choices!" + AnsiEnum.RESET);
        }
        if(Board.getBoard().getPlayers().size() > 1) {
            System.out.print("Please wait for other players' choices ");
            CLI.showThreePointsAnimation();
        }
        return chosenResourcesColor;
    }

    public void askCardPlacement(){
        int choice = -1;
        while(choice < 1|| choice > 3){
            CLI.render("Select where you wish to place your new card(1-2-3)");
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
        CLI.render("Looks like your white marbles are evolving\nChoose the color you prefer for this "+numberOfTransformation+" marbles");
        for (int i = 0; i < numberOfTransformation; i++) {
            System.out.print("Resource n" + (i + 1) + " can be: ");
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
        String answer = scanner.nextLine();
        while (!(answer.equals("market")||answer.equals("buy")||answer.equals("production")||answer.equals("see")||answer.equals("leader"))){
            CLI.renderError("invalid action, try again");
            answer = scanner.nextLine();
        }
        return answer;
    }

    public void askMarketAction(){
        int choice = -1;
        CLI.render("Select which row or column you want to take");
        while(choice < MIN_MARKET_ARROW_ID || choice > MAX_MARKET_ARROW_ID){
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                CLI.renderError(INVALID);
            }
        }
        notifyObservers(new MarketActionEvent(choice));
    }

    public void askResourcePlacement(){
        CLI.render("Write your swaps(es:0,4,1,5), refresh to update, done to end the placement");
        List<Integer> swaps = new ArrayList<>();
        String answer = scanner.nextLine();
        while(!answer.toLowerCase(Locale.ROOT).equals("refresh")&&!answer.toLowerCase(Locale.ROOT).equals("done")){
            try {
                //regex means any number of spaces before and after the comma
                swaps.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).collect(Collectors.toList()));
                System.out.println("swaps saved, add more or type done/refresh");
                answer = scanner.nextLine();
            }catch (IndexOutOfBoundsException | NumberFormatException e){
                CLI.renderError(INVALID);
                answer = scanner.nextLine();
            }
        }
        CLI.render("Will try to perform this swaps: "+swaps);
        notifyObservers(new ResourcesPlacementActionEvent(swaps,answer.equals("done")));
    }

    //return true if leader action
    public boolean askEndAction(){
        CLI.render("what do you wish to do?(end(to end your turn), leader(to do a leader action))");
        String answer = scanner.nextLine();
        while (!(answer.equals("end")||answer.equals("leader"))){
            CLI.renderError("invalid action, try again");
            answer = scanner.nextLine();
        }
        if(answer.equals("end")){
            notifyObservers(new EndTurnActionEvent());
            return false;
        }
        return true;
    }

    public boolean askLeaderAction(){
        List<String> hand = Board.getBoard().getPlayerByNickname(nickname).getHandLeaders();
        int index = -1;
        if(hand.size()!=0){
            while (index<0||index>hand.size()-1){
                System.out.println("Choose the Leader card by index(0 or 1)");
                try {
                    index = Integer.parseInt(scanner.nextLine());
                }catch (NumberFormatException e){
                    CLI.renderError(INVALID);
                }
            }
            CLI.render("What do you wish to do with this card?(activate/discard)");
            String discard = scanner.nextLine().toLowerCase(Locale.ROOT);
            while (!discard.equals("activate")&&!discard.equals("discard")){
                CLI.renderError(INVALID);
                discard = scanner.nextLine().toLowerCase(Locale.ROOT);
            }
            notifyObservers(new LeaderActionEvent(hand.get(index),discard.equals("discard")));
            return true;
        }
        return false;
        //notifyObservers(new LeaderActionEvent(null,true));
    }

    public void askBuyAction() {
        //Player player = Board.getBoard().getPlayerByNickname(nickname);
        int level = -1;
        String color = "";
        String answer;
        List<Integer> resources = new ArrayList<>();
        while (level < MIN_CARD_LEVEL || level > MAX_CARD_LEVEL) {
            CLI.render("Choose the level of the card you want to buy(between " + MIN_CARD_LEVEL + " and " + MAX_CARD_LEVEL + ")");
            try {
                level = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                CLI.renderError(INVALID);
            }
        }
        while (!CARD_COLORS.contains(color.toUpperCase(Locale.ROOT))) {
            CLI.render("Choose the color of the card to buy(green,blue,yellow,purple)");
            color = scanner.nextLine();
        }

        CLI.render("Choose which resources you want to use to pay the card(1,2,3)");
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
    }


    //todo test and/or cut in smaller functions
    public void askProductionAction(){
        Player player = Board.getBoard().getPlayerByNickname(nickname);
        Map<Integer, List<Integer>> inResourcesForEachProductions = new HashMap<>();
        Map<Integer, String> outResourcesForEachProductions = new HashMap<>();
        Set<Integer> inResourcesStory = new HashSet<>();
        Set<Integer> indexesStory = new HashSet<>();
        List<Integer> inResources;
        CLI.render("Choose which production you want to activate(0...5), type done when finished");
        String answer = scanner.nextLine();
        while (!answer.toLowerCase(Locale.ROOT).equals("done")){
            int index;
            String outResource = null;
            //instantiated here to be set as null for every new card
            try {
                index = Integer.parseUnsignedInt(answer);
                if(indexesStory.contains(index)){
                    CLI.renderError("This card already produced,try a new one");
                    answer = scanner.nextLine();
                    continue;
                }
                //used to launch exception if card is not owned
                indexesStory.add(index);
            }catch (NumberFormatException e){
                CLI.renderError("Couldn't get the selected card, try again");
                answer = scanner.nextLine();
                continue;
            }
            CLI.render("Choose the resources you wish to use");
            answer = scanner.nextLine();
            while (true) {
                //instantiated here to be reset if failed input
                inResources = new ArrayList<>();
                try {
                    //Only natural numbers are accepted and any duplicate are ignored
                    inResources.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).filter(n->n>0).collect(Collectors.toSet()));
                    //if a resource is repeated ask to re choose resources
                    if(inResources.stream().anyMatch(inResourcesStory::contains)){
                        CLI.renderError("This res where already used");
                    }
                    else
                        break;
                } catch (NumberFormatException e) {
                    CLI.renderError(INVALID);
                }
                answer = scanner.nextLine();
            }
            if(index==0||index>=4){
                CLI.render("Choose the resource you wish to produce");
                outResource = scanner.nextLine().toUpperCase(Locale.ROOT);
                while (!RESOURCE_COLORS.contains(outResource)) {
                    CLI.render("Choose the resource you wish to produce");
                    outResource = scanner.nextLine().toUpperCase(Locale.ROOT);
                }
            }
            inResourcesStory.addAll(inResources);
            inResourcesForEachProductions.put(index,inResources);
            outResourcesForEachProductions.put(index,outResource);
            CLI.render("Choose which production you want to activate(0...5), Type done when finished");
            answer = scanner.nextLine();
        }
        notifyObservers(new ProductionActionEvent(inResourcesForEachProductions,outResourcesForEachProductions));
    }

    public String askSeeChoice(){
        CLI.render("What do you wish to see(grids/done/player)?");
        String answer = scanner.nextLine();
        while(!answer.equals("grids")&&!answer.equals("done")&&!answer.equals("player")){
            CLI.renderError(INVALID);
            answer = scanner.nextLine();
        }
        return answer;

    }

    public String askSeePlayerChoice(){
        List<String> nicknames = Board.getBoard().getPlayers().stream().map(Player::getNickname).collect(Collectors.toList());
        CLI.render("Players to see are: "+nicknames);
        String answer = scanner.nextLine();
        while (!nicknames.contains(answer)){
            CLI.renderError("Player does not exist, try again");
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
