package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CardColorEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GenerateCardsTest {

    @Test
    void generateDevelopmentCardsTest() {
        List<DevelopmentCard> developmentCards;
        GenerateCards generator = new GenerateCards();
        developmentCards=generator.generateDevelopmentCards();
        //create 2 maps, one by color and one by level
        Map<CardColorEnum, Integer> mapLevelToColor = developmentCards.stream().collect(
                Collectors.groupingBy(DevelopmentCard::getColor,
                        Collectors.collectingAndThen(
                                Collectors.mapping(DevelopmentCard::getLevel, Collectors.toSet()),
                                Set::size)));
        Map<Integer,Integer> mapColorToLevel = developmentCards.stream().collect(
                Collectors.groupingBy(DevelopmentCard::getLevel,
                        Collectors.collectingAndThen(
                                Collectors.mapping(DevelopmentCard::getColor, Collectors.toSet()),
                                Set::size)));
        //check that every color has 3 different levels
        mapLevelToColor.forEach((key, value) -> assertEquals(3,value));
        //check that every level can be of 4 different color
        mapColorToLevel.forEach((key, value) -> assertEquals(4,value));

    }

    @Test
    void generateLeaderCardsTest() {
        List<LeaderCard> leaderCards;
        GenerateCards generator = new GenerateCards();
        leaderCards=generator.generateLeaderCards();
        //check that there are 16 leaderCards of the 4 expected types (4 foreach)
        assertEquals(16,leaderCards.size());
        assertEquals(4,leaderCards.stream().filter(leader->leader instanceof LeaderCardDiscount).count());
        assertEquals(4,leaderCards.stream().filter(leader->leader instanceof LeaderCardMarket).count());
        assertEquals(4,leaderCards.stream().filter(leader->leader instanceof LeaderCardWarehouse).count());
        assertEquals(4,leaderCards.stream().filter(leader->leader instanceof LeaderCardProduction).count());
    }

    @Test
    void getDevCardsAsGridTest() {
        List<DevelopmentCard> developmentCards;
        List<Map<CardColorEnum,List<DevelopmentCard>>> mapByLevel = new ArrayList<>();
        GenerateCards generator = new GenerateCards();
        developmentCards=generator.generateDevelopmentCards();
        for (int i = 1;i<=3;i++){
            mapByLevel.add(generator.getDevCardsAsGrid(developmentCards,i));
        }
        //check that every level has 4 different colors
        mapByLevel.forEach((element)->assertEquals(4,element.size()));
        //check that for every level every color has 4 different cards
        mapByLevel.forEach((element)->element.forEach((key, value)->assertEquals(4,value.size())));

    }
}