package it.polimi.ingsw.server.model.gameBoard;

import it.polimi.ingsw.server.model.cards.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DeckLeader {
    private List<LeaderCard> cards;
    private final CardsGenerator cardGenerator = new CardsGenerator();


    public DeckLeader() {
        cards = cardGenerator.generateLeaderCards();
    }

    /**
     * shuffle all the LeaderCards currently in the deck
     */
    public void shuffle(){
        Collections.shuffle(cards);
    }


    /**
     * Draws the top 4 cards of the deck removing them
     * @return a{@link ArrayList<LeaderCard> of the cards drawn}
     */
    public List<LeaderCard> draw(){
        if(cards.size()<4){
            throw new IndexOutOfBoundsException();
        }
        List<LeaderCard> drawn = new ArrayList<>();
        for(int i = 0;i<4;i++) {
            drawn.add(cards.get(i));
        }
        cards.removeAll(drawn);
        return drawn;
    }
}
