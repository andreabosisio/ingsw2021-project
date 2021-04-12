package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.cards.DevelopmentCard;
import it.polimi.ingsw.server.model.cards.LeaderCard;
import it.polimi.ingsw.server.model.cards.ProductionCard;
import it.polimi.ingsw.server.model.gameBoard.EndGameObserver;
import it.polimi.ingsw.server.model.gameBoard.EndGameSubject;
import it.polimi.ingsw.server.model.gameBoard.faithtrack.FaithTrack;
import it.polimi.ingsw.server.model.resources.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PersonalBoard implements EndGameSubject {
    private List<LeaderCard> activeLeaderCards;
    private List<List<ProductionCard>> deckProduction;
    private FaithTrack faithTrack;
    private Warehouse warehouse;
    private EndGameObserver endGameObserver;

    public PersonalBoard() {
        warehouse = new Warehouse();
        deckProduction = new ArrayList<>();
        //4list: 1 for basicPower and 3 for devCard
        IntStream.range(0,4).forEach(i->deckProduction.add(new ArrayList<>()));
        activeLeaderCards = new ArrayList<>();
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Get all the currently active LeaderCards
     * @return List</LeaderCard> of active LeaderCards
     */
    public List<LeaderCard> getActiveLeaderCards() {
        return activeLeaderCards;
    }


    /**
     * set player's faithTrack
     * @param faithTrack faithTrack to set
     * @return true if successful, false if faithTrack was already assigned
     */
    public boolean setFaithTrack(FaithTrack faithTrack){
        if(this.faithTrack==null){
            this.faithTrack=faithTrack;
            return true;
        }
        return false;
    }

    public List<ProductionCard>  getAvailableProduction(){
        //todo return productioncard that can be activated
        //fixme right now it returns highest level of every column for testing(including LeaderCards)(safe to change: all tests with this method have been removed)
        List<ProductionCard> toReturn = new ArrayList<>();
        deckProduction.stream().filter(el->el.size()>0).forEach(element->toReturn.add(element.get(0)));
        return toReturn;
    }


    public List<List<Resource>> getResForChosenProduction(int chosenProductionID){
        //todo return res da warehouse/deposit
        return new ArrayList<>();
    }

    /**
     * Get the possible placement of a Development card
     * @param card card to place
     * @return List</Integer> of indexes where the card could be placed
     */
    public List<Integer> getAvailablePlacement(DevelopmentCard card) {
        List<Integer> toReturn = new ArrayList<>();
        for(int i = 1;i<4;i++){
            if(deckProduction.get(i).size()==card.getLevel()-1){
                toReturn.add(i);
            }
        }
        return toReturn;
    }

    /**
     * place new DevCard at index[0] in specified position (only accept position between 1 and 3 )
     * and calls the method notifyEndGameObserver if the Player purchased his seventh card
     * @param pos placement position
     * @param card development card to place
     * @return true if placed correctly
     */
    public boolean setNewDevCard(int pos,DevelopmentCard card){
        if(pos<1 || pos>3 || deckProduction.stream().anyMatch(el -> el.contains(card)) ) {
            return false;
        }
        //check that pos is compliant with rules of placement
        if(getAvailablePlacement(card).contains(pos)) {
            //check that if there is a card under, it is acceptable to place the new one over it
            if (deckProduction.get(pos).size() > 0) {
                DevelopmentCard last = (DevelopmentCard) deckProduction.get(pos).get(0);
                if (last.getLevel() >= card.getLevel()) {
                    return false;
                }
            }
            deckProduction.get(pos).add(0, card);
            int numberOfDevCards = 0;
            for(int i = 1; i < 4; i++)
                numberOfDevCards = numberOfDevCards + deckProduction.get(i).size();

            if (numberOfDevCards == 7)
                this.notifyEndGameObserver();
            return true;
        }
        return false;
    }

    /**
     * Add a LeaderCard to the list of active ProductionCards
     * @param leader LeaderCard to place
     * @return true if successfully placed
     */
    public boolean setNewDevCard(ProductionCard leader){
        if(activeLeaderCards.contains(leader) || (deckProduction.size() >= 6)) {
            return false;
        }
        List<ProductionCard> newLevel = new ArrayList<>();
        newLevel.add(leader);
        deckProduction.add(newLevel);
        return true;
    }

    /**
     * Add leaderCard to the list of active LeaderCards
     * @param card card to activate
     * @return true if successfully activated
     */
    public boolean addToActiveLeaders(LeaderCard card){
        if(!activeLeaderCards.contains(card) && activeLeaderCards.size()<2) {
            activeLeaderCards.add(card);
            return true;
        }
        return false;
    }

    /**
     * get the total number of endGame points for everything currently on the board
     * @return total number of points
     */
    public int getPoints(){
        //todo count all player points and return them
        //remember to count leader in deck and in activation only once!
        return 0;
    }

    /**
     * get the number of Resources left in the supply
     * @return the total number of Resources left
     */
    // TODO: Return the total number of resources
    public int getResourcesLeft() {
        return 0;
    }

    /**
     * This method is used to register an observer
     *
     * @param endGameObserver is the object to add.
     */
    @Override
    // TODO: The TurnLogic set the Observer of this class!!
    public void registerEndGameObserver(EndGameObserver endGameObserver) {
        this.endGameObserver = endGameObserver;
    }

    /**
     * This method calls the method update of the Observer.
     * Its task is to notify the class MultiPlayerCheckWinner
     * when the Player buys is seventh Development Card.
     */
    @Override
    public void notifyEndGameObserver() {
        endGameObserver.update();
    }
}