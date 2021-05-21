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
    private static final int BASE_POWER_INDEX = 0;
    private static final int FIRST_LEADER_INDEX = 4;
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
        String numberOfPlayers = scanner.nextLine();
        try {
            notifyObservers(new SelectNumberPlayersEvent(Integer.parseInt(numberOfPlayers)));
        } catch (NumberFormatException e) {
            CLI.clearView();
            CLI.render(AnsiEnum.RED + "Please re-insert a valid number" + AnsiEnum.RESET);
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
            CLI.render(PrintableScene.addTopString(PrintableScene.concatenatePrintable(toChoose, "    " + "\t"), row));
            System.out.println();

            String choice = scanner.nextLine();
            try {
                chosenIndexes.add(Integer.parseInt(choice));
                if(chosenIndexes.stream().distinct().count() < chosenIndexes.size())
                    throw new NumberFormatException();
                leaderCardsIDs.get(Integer.parseInt(choice)); //used to trigger IndexOutOfBoundsException
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                CLI.clearView();
                CLI.render(AnsiEnum.RED + "Invalid index: please re-choice from scratch" + AnsiEnum.RESET);
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
                System.out.println("Choose a " + AnsiEnum.CYAN + "resource" + AnsiEnum.RESET + ": ");
                for (int j = 0; j < storableMarbles.size(); j++) {
                    System.out.print(AnsiEnum.WHITE_BRIGHT + "[" + j + "]: " + AnsiEnum.RESET + Marble.getPrintable(storableMarbles.get(j).getColor()) + "\t\t");
                }
                System.out.println();
                String choice = scanner.nextLine();
                try {
                    chosenResourcesColor.add(storableMarbles.get(Integer.parseInt(choice)).getColor());
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    CLI.clearView();
                    System.out.println(AnsiEnum.RED + "Invalid index: please re-choice from scratch" + AnsiEnum.RESET);
                    return null;
                }
            }
            System.out.println(AnsiEnum.GREEN + "Valid resources choices!" + AnsiEnum.RESET);
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
            System.out.println("Select where you wish to place your new card(1-2-3)");
            try {
                choice = Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("invalid input");
            }
        }
        notifyObservers(new CardPlacementActionEvent(choice));
    }

    public void askResourceTransformation(int numberOfTransformation, List<String> possibleTransformations){
        List<String> transformations = new ArrayList<>();
        System.out.println("Looks like your white marbles are evolving\nChoose the color you prefer for this "+numberOfTransformation+" marbles");
        for (int i = 0; i < numberOfTransformation; i++) {
            System.out.print("Resource n" + (i + 1) + " can be: ");
            for(int j = 0; j < possibleTransformations.size(); j++){
                String color = possibleTransformations.get(j);
                System.out.print((j+1) + Marble.getPrintable(color.toUpperCase(Locale.ROOT))+" ");
            }
            System.out.print("\n");
            int choice = -1;
            while (choice<1||choice>possibleTransformations.size()){
                System.out.println("Select between 1 and 2");
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                }catch (NumberFormatException e){
                    System.out.println("Not a number");
                }
            }
            transformations.add(possibleTransformations.get(choice-1));
        }
        notifyObservers(new TransformationActionEvent(transformations));
    }

    public String askFirstAction(){
        String answer = scanner.nextLine();
        while (!(answer.equals("market")||answer.equals("buy")||answer.equals("production")||answer.equals("see")||answer.equals("leader"))){
            System.out.println("invalid action, try again");
            answer = scanner.nextLine();
        }
        return answer;
    }

    public void askMarketAction(){
        int choice = -1;
        System.out.println("Select which row or column you want to take");
        while(choice < MIN_MARKET_ARROW_ID || choice > MAX_MARKET_ARROW_ID){
            try {
                choice = Integer.parseInt(scanner.nextLine());
            }catch (NumberFormatException e){
                System.out.println("invalid input");
            }
        }
        notifyObservers(new MarketActionEvent(choice));
    }

    public void askResourcePlacement(){
        System.out.println("Write your swaps(es:0,4,1,5), refresh to update, done to end the placement");
        List<Integer> swaps = new ArrayList<>();
        String answer = scanner.nextLine();
        while(!answer.toLowerCase(Locale.ROOT).equals("refresh")&&!answer.toLowerCase(Locale.ROOT).equals("done")){
            try {
                //regex means any number of spaces before and after the comma
                swaps.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).collect(Collectors.toList()));
                System.out.println("swaps saved, add more or type done/refresh");
                answer = scanner.nextLine();
            }catch (IndexOutOfBoundsException | NumberFormatException e){
                System.out.println("Invalid input");
                answer = scanner.nextLine();
            }
        }
        System.out.println("Will try to perform this swaps: "+swaps);
        notifyObservers(new ResourcesPlacementActionEvent(swaps,answer.equals("done")));
    }

    //return true if leader action
    public boolean askEndAction(){
        System.out.println("what do you wish to do?(end(to end your turn), leader(to do a leader action))");
        String answer = scanner.nextLine();
        while (!(answer.equals("end")||answer.equals("leader"))){
            System.out.println("invalid action, try again");
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
            //todo print hand
            while (index<0||index>hand.size()-1){
                System.out.println("Choose the Leader card by index(0 or 1)");
                try {
                    index = Integer.parseInt(scanner.nextLine());
                }catch (NumberFormatException e){
                    System.out.println("Not a number");
                }
            }
            System.out.println("What do you wish to do with this card?(activate/discard)");
            String discard = scanner.nextLine().toLowerCase(Locale.ROOT);
            while (!discard.equals("activate")&&!discard.equals("discard")){
                System.out.println("invalid input");
                discard = scanner.nextLine().toLowerCase(Locale.ROOT);
            }
            //todo cancellare system out qui sotto
            //System.out.println("you chose card "+hand.get(index)+" to: "+discard);
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
            System.out.println("Choose the level of the card you want to buy(beetween " + MIN_CARD_LEVEL + " and " + MAX_CARD_LEVEL + ")");
            try {
                level = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("not a number");
            }
        }
        while (!CARD_COLORS.contains(color.toUpperCase(Locale.ROOT))) {
            System.out.println("Choose the color of the card to buy(green,blue,yellow,purple)");
            color = scanner.nextLine();
        }
        //todo print card and warehouse
        System.out.println("Choose which resources you want to use to pay the card(1,2,3)");
        answer = scanner.nextLine();
        while (true) {
            try {
                //regex means any number of spaces before and after the comma
                //Only natural numbers are accepted and any duplicate are ignored
                resources.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).filter(n->n>0).collect(Collectors.toSet()));
                //System.out.println("The current resources selected are "+resources+" type done if finished or add more");
                break;
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("Invalid input");
                answer = scanner.nextLine();
            }
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
        System.out.println("Choose which production you want to activate(0...5), type done when finished");
        String answer = scanner.nextLine();
        while (!answer.toLowerCase(Locale.ROOT).equals("done")){
            int index;
            String outResource = null;
            //instantiated here to be set as null for every new card
            try {
                index = Integer.parseUnsignedInt(answer);
                if(indexesStory.contains(index)){
                    System.out.println("This card already produced,try a new one");
                    answer = scanner.nextLine();
                    continue;
                }
                //used to launch exception if card is not owned
                indexesStory.add(index);
            }catch (NumberFormatException e){
                System.out.println("Couldn't get the selected card, try again");
                answer = scanner.nextLine();
                continue;
            }
            System.out.println("Choose the resources you wish to use");
            answer = scanner.nextLine();
            while (true) {
                //instantiated here to be reset if failed input
                inResources = new ArrayList<>();
                try {
                    //Only natural numbers are accepted and any duplicate are ignored
                    inResources.addAll(Arrays.stream(answer.split("\\s*,\\s*")).map(Integer::parseInt).filter(n->n>0).collect(Collectors.toSet()));
                    //if a resource is repeated ask to re choose resources
                    if(inResources.stream().anyMatch(inResourcesStory::contains)){
                        System.out.println("This res where already used");
                    }
                    else
                        break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid resources");
                }
                answer = scanner.nextLine();
            }
            if(index==0||index>=4){
                System.out.println("Choose the resource you wish to produce");
                outResource = scanner.nextLine().toUpperCase(Locale.ROOT);
                while (!RESOURCE_COLORS.contains(outResource)) {
                    System.out.println("Choose the resource you wish to produce");
                    outResource = scanner.nextLine().toUpperCase(Locale.ROOT);
                }
            }
            inResourcesStory.addAll(inResources);
            inResourcesForEachProductions.put(index,inResources);
            outResourcesForEachProductions.put(index,outResource);
            System.out.println("Choose which production you want to activate(0...5), Type done when finished");
            answer = scanner.nextLine();
        }
        notifyObservers(new ProductionActionEvent(inResourcesForEachProductions,outResourcesForEachProductions));
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
