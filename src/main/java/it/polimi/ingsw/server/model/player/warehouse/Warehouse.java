package it.polimi.ingsw.server.model.player.warehouse;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.EmptySlotException;
import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.resources.Resource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the Warehouse slots.
 * Market resources:        Position 0,1,2,3
 * First depot:             Position 4
 * Second depot:            Positions 5,6
 * Third depot:             Positions 7,8,9
 * 1° extra slots:          Positions 10,11
 * 2° extra slots:          Positions 12,13
 * <p>
 * Graphical description of the indexes:
 * Market resources:    0 1 2 3
 * <p>
 * First depot:                  4
 * Second depot:             5       6
 * Third depot:          7       8       9
 * <p>
 * 1° extra slots:      10 11
 * 2° extra slots:      12 13
 * StrongBox:           14,15,...
 */
public class Warehouse {

    /**
     * Index of the position where starts the zone of the slots containing the new resources taken from the MarketTray.
     */
    private final int startResourcesFromMarketSlotsZone = 0;
    private final int availableResourcesFromMarketSlots = 4;

    /**
     * Index of the position where starts the zone of the warehouse depots.
     */
    private final int startWarehouseDepotsZone = 4;
    private final int startFirstDepot = 4;
    private final int startSecondDepot = 5;
    private final int startThirdDepot = 7;
    private final int availableDepotsSlots = 6;

    /**
     * Index of the position where starts the first zone of the extra slots provided by the ProductionLeaderCard.
     */
    private final int startFirstExtraSlotsZone = 10;
    private final int availableFirstExtraSlots = 2;

    /**
     * Index of the position where starts the second zone of the extra slots provided by the ProductionLeaderCard.
     */
    private final int startSecondExtraSlotsZone = 12;
    private final int availableSecondExtraSlots = 2;


    /**
     * Index of the position where starts the zone of the StrongBox.
     */
    private final int startStrongBoxZone = availableResourcesFromMarketSlots + availableDepotsSlots + availableFirstExtraSlots + availableSecondExtraSlots;


    private final MarketSlots resourcesFromMarket = new MarketSlots();


    private final List<Depot> depots = new ArrayList<>() {{
        add(new Depot(startSecondDepot - startFirstDepot));
        add(new Depot(startThirdDepot - startSecondDepot));
        add(new Depot(startFirstExtraSlotsZone - startThirdDepot));
    }};

    private final List<ExtraSlots> extraSlots = new ArrayList<>() {{
        add(new ExtraSlots());
        add(new ExtraSlots());
    }};

    private final StrongBox strongBox = new StrongBox();

    private final Map<Integer, TranslatedPosition> positionMap = new HashMap<>();

    /**
     * Verify if the given position is in the zone of the slots containing the new resources taken from the MarketTray.
     *
     * @param position that represents a slot containing a Resource
     * @return true if the given position represents a slot of the described zone.
     */
    private boolean isInResourcesFromMarketSlotsZone(int position) {
        return position >= startResourcesFromMarketSlotsZone && position < startWarehouseDepotsZone;
    }

    /**
     * Verify if the given position is in the zone of the depots.
     *
     * @param position that represents a slot containing a Resource
     * @return true if the given position represents a slot of the described zone.
     */
    private boolean isInDepotsZone(int position) {
        return position >= startWarehouseDepotsZone && position < startFirstExtraSlotsZone;
    }

    /**
     * Verify if the given position is in the first zone of the extra slots provided by a WarehouseLeaderCard.
     *
     * @param position that represents a slot containing a Resource
     * @return true if the given position represents a slot of the described zone.
     */
    private boolean isInFirstExtraSlotsZone(int position) {
        return (position >= startFirstExtraSlotsZone && position < startSecondExtraSlotsZone);
    }

    /**
     * Verify if the given position is in the second zone of the extra slots provided by a WarehouseLeaderCard.
     *
     * @param position that represents a slot containing a Resource
     * @return true if the given position represents a slot of the described zone.
     */
    private boolean isInSecondExtraSlotsZone(int position) {
        return (position >= startSecondExtraSlotsZone && position < startStrongBoxZone);
    }

    /**
     * Verify if the given position is in the StrongBox.
     *
     * @param position that represents a slot containing a Resource
     * @return true if the given position represents a slot of the described zone.
     */
    private boolean isInStrongBoxZone(int position) {
        return position >= startStrongBoxZone;
    }

    /**
     * Activate the extra slots provided by an WarehouseLeaderCard.
     *
     * @param slotsType the resource's type of the extra slots
     * @return true if the extra slots can be activated
     */
    public boolean addExtraSlots(Resource slotsType) {
        for (ExtraSlots remainingExtraSlots : extraSlots) {
            if (!remainingExtraSlots.isActivated()) {
                remainingExtraSlots.activateExtraSlots(slotsType);
                return true;
            }
        }
        return false;
    }

    /**
     * Verify if the player has activated the indicated extra slots zone
     *
     * @param extraSlotsZoneIndex index of the extra slots zone that has to be verified
     * @return true if the extra slots zone to verify is activated
     */
    public boolean hasExtraSlots(int extraSlotsZoneIndex) {
        return extraSlots.get(extraSlotsZoneIndex).isActivated();
    }

    /**
     * Map the given position into a TranslatedPosition that has the correct zone of the given position and the
     * shifted position for that zone.
     *
     * @param position that has to be translated into this Warehouse storage logic
     * @return true if the given position has been correctly translated
     * @throws InvalidIndexException      if position is negative
     * @throws NonAccessibleSlotException if the position represents a slot that's not accessible
     */
    private boolean translatePosition(int position) throws InvalidIndexException, NonAccessibleSlotException {
        if (isInResourcesFromMarketSlotsZone(position))
            positionMap.put(position, new TranslatedPosition(position, resourcesFromMarket));
        else if (isInDepotsZone(position))
            if (position < startSecondDepot) //first depot
                positionMap.put(position, new TranslatedPosition(position - startFirstDepot, depots.get(0)));
            else if (position < startThirdDepot) //second depot
                positionMap.put(position, new TranslatedPosition(position - startSecondDepot, depots.get(1)));
            else //third depot
                positionMap.put(position, new TranslatedPosition(position - startThirdDepot, depots.get(2)));
        else if (isInFirstExtraSlotsZone(position)) //first extra slots
            if (hasExtraSlots(0)) //first extra slots are activated
                positionMap.put(position, new TranslatedPosition(position - startFirstExtraSlotsZone, extraSlots.get(0)));
            else
                throw new NonAccessibleSlotException("First extra slots are not active yet"); //extra slots have not been activated yet
        else if (isInSecondExtraSlotsZone(position)) //second extra slots
            if (hasExtraSlots(1)) //second extra slots are activated
                positionMap.put(position, new TranslatedPosition(position - startSecondExtraSlotsZone, extraSlots.get(1)));
            else
                throw new NonAccessibleSlotException("Second extra slots are not active yet"); //extra slots have not been activated yet
        else if (isInStrongBoxZone(position))
            positionMap.put(position, new TranslatedPosition(position - startStrongBoxZone, strongBox));
        else if (position < 0)
            throw new InvalidIndexException("Negative position are not allowed"); //invalid position
        return true;
    }

    /**
     * Translate a missing position and return it.
     *
     * @param position which is never been translated
     * @return the new TranslatedPosition
     * @throws InvalidIndexException      if position is negative
     * @throws NonAccessibleSlotException if the position represents a slot that's not accessible
     */
    private TranslatedPosition getTranslatedPosition(int position) throws InvalidIndexException, NonAccessibleSlotException {
        //translate the missing position
        translatePosition(position);
        return positionMap.get(position);
    }

    /**
     * Add the chosen resources in the initial part of the game in the depots.
     *
     * @param initialResources chosen in the initial part of the game (max 2)
     * @throws EmptySlotException         if the chosen slot is empty
     * @throws InvalidIndexException      if the given position is negative
     * @throws NonAccessibleSlotException if the given position represents a slot that's not accessible
     */
    public void setupWarehouse(List<Resource> initialResources) throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {

        for (int i = 0; i < availableResourcesFromMarketSlots; i++)
            translatePosition(i);

        addResourcesFromMarket(initialResources);
        //two different resource's type
        if (initialResources.stream().distinct().count() > 1) {
            swap(0, 4);
            swap(1, 5);
            return;
        }
        int i = 0;
        for (Resource res : initialResources) {
            swap(i, i + 5);
            i++;
        }
        //reset market slots
        getNumberOfRemainingResources();
    }

    /**
     * Store the producedResource in the StrongBox.
     *
     * @param producedResource to store
     * @return true if the producedResource has been correctly stored
     */
    public boolean addResourcesToStrongBox(Resource producedResource) {
        strongBox.addResource(producedResource);
        try {
            return this.translatePosition(strongBox.slots.size() - 1 + startStrongBoxZone); //save the position to positionMap
        } catch (InvalidIndexException | NonAccessibleSlotException e) {
            e.printStackTrace();
            return false; //impossible condition
        }
    }

    /**
     * Store all the producedResources in the StrongBox.
     *
     * @param producedResources to store
     * @return true true if all the producedResources has been correctly stored
     */
    public boolean addResourcesToStrongBox(List<Resource> producedResources) {
        for (Resource producedResource : producedResources)
            if (!addResourcesToStrongBox(producedResource))
                return false;
        return true;
    }

    /**
     * Reorder the StrongBox removing empty slots.
     */
    public void reorderStrongBox() {
        strongBox.reorder();
    }

    /**
     * Store the resources taken from the MarketTray in the ResourcesFromMarketSlotsZone.
     *
     * @param newResources taken from the MarketTray
     * @return true the resources has been stored successfully
     */
    public boolean addResourcesFromMarket(List<Resource> newResources) {
        if (newResources.size() > availableResourcesFromMarketSlots)
            return false;
        return resourcesFromMarket.addResources(newResources);
    }

    /**
     * Take and remove the resource stored in the given position.
     *
     * @param position of the resource to be taken
     * @return the chosen resource
     * @throws EmptySlotException         if the chosen slot is empty
     * @throws InvalidIndexException      if the given position is negative
     * @throws NonAccessibleSlotException if the given position represents a slot that's not accessible
     */
    public Resource takeResources(int position) throws EmptySlotException, InvalidIndexException, NonAccessibleSlotException {
        if (!isInResourcesFromMarketSlotsZone(position)) { //cannot take resources from ResourcesFromMarketSlotsZone
            Resource chosenResource = positionMap.getOrDefault(position, getTranslatedPosition(position)).takeResource();
            if (chosenResource == null) {
                throw new EmptySlotException("Slot number " + position + " is empty");
            } else {
                return chosenResource;
            }
        }
        throw new NonAccessibleSlotException("Slot number " + position + " is not accessible");
    }

    /**
     * Take and remove all the resources stored in the given positions.
     *
     * @param positions of the resources to be taken
     * @return a list containing the chosen resources
     * @throws InvalidIndexException      if one the given positions is negative
     * @throws EmptySlotException         if one of the chosen slots is empty
     * @throws NonAccessibleSlotException if one of the given position represents a slot that's not accessible
     */
    public List<Resource> takeResources(List<Integer> positions) throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        List<Resource> chosenResources = new ArrayList<>();
        for (Integer position : positions)
            chosenResources.add(this.takeResources(position));
        return chosenResources;
    }

    /**
     * Get the resources stored in the slots of the given positions without removing them.
     *
     * @param positions of the chosen slots
     * @return a list containing a copy of the chosen resources
     */
    public List<Resource> getResources(List<Integer> positions) {
        return positions.stream().map(p -> {
            try {
                return positionMap.getOrDefault(p, getTranslatedPosition(p)).getResource();
            } catch (InvalidIndexException | NonAccessibleSlotException e) {
                return null;
            }
        }) //forall given positions get the correct resource
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get a map containing all the already used positions and their resources' color
     *
     * @return a map containing all the already used positions and their resources' color
     */
    public Map<Integer, String> getAllPositionsAndResources() {
        return positionMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, p -> {
                    Resource currResource = p.getValue().getResource();
                    if (currResource != null)
                        return currResource.getColor().toString();
                    return ResourcesEnum.EMPTY_RES.toString();
                }));
    }

    /**
     * Get the non null resources stored in the Market slots.
     *
     * @return a list containing the resources from Market
     */
    public List<Resource> getResourcesFromMarket() {
        return resourcesFromMarket.getSlots().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Get all the available resources stored in the Warehouse without removing them.
     *
     * @return a list containing the copy of all the available resources
     */
    public List<Resource> getAllResources() {
        return positionMap.keySet().stream() //forall already given positions saved in positionMap
                .filter(k -> !isInResourcesFromMarketSlotsZone(k)) //cannot take from the MarketSlotsZone
                .map(k -> positionMap.get(k).getResource()) //take the copy of the chosen resource
                .filter(Objects::nonNull) //take only non null resources
                .collect(Collectors.toList());
    }

    /**
     * Swap the resource stored in position initPosition to the finalPosition.
     * Can only swap a Resource that's just been taken from the MarketTray or
     * a Resource that's already stored in a depot or in an extra slot
     * to a depot or to an extra slot.
     *
     * @param initPosition  Position of the selected Resource to swap
     * @param finalPosition Destination position of the swap
     * @return true if the swap can be applied
     * @throws InvalidIndexException      if one of the given position is negative
     * @throws EmptySlotException         if one of the selected slots is empty
     * @throws NonAccessibleSlotException if one of the selected slots is non accessible
     */
    public boolean swap(int initPosition, int finalPosition) throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        //can only swap a Resource that's just been taken from the MarketTray
        // or a Resource that's already stored on a depot or in an extra slot
        if (isInStrongBoxZone(initPosition))
            return false;
        //can only swap a Resource to a depot or to an extra slot
        //if(isInResourcesFromMarketSlotsZone(finalPosition) || isInStrongBoxZone(finalPosition))
        if (isInStrongBoxZone(finalPosition))
            return false;

        Resource initPositionResource = positionMap
                .getOrDefault(initPosition, getTranslatedPosition(initPosition))
                .getResource();
        Resource finalPositionResource = positionMap
                .getOrDefault(finalPosition, getTranslatedPosition(finalPosition))
                .getResource();

        if (initPositionResource == null) {
            throw new EmptySlotException("Cannot swap from an empty slot");
        }

        return positionMap.get(initPosition).setResource(finalPositionResource) &&
                positionMap.get(finalPosition).setResource(initPositionResource);

    }

    /**
     * Verify if each warehouse depots contains only a single type of Resource and different from the others;
     * Verify if each extra slots contains only the required type of Resource provided by the WarehouseLeaderCard.
     *
     * @return true if the resources stored in the Warehouse depots and in the extra slots are correctly organized
     */
    public boolean isProperlyOrdered() {
        //checks if each depots contains only one resource's type
        for (Depot depot : depots) {
            if (!depot.isLegal())
                return false;
        }

        //checks if each depot has a different resource's type
        if (!depots.stream().map(Depot::getResourceType).filter(Objects::nonNull).distinct().collect(Collectors.toList()).
                equals(depots.stream().map(Depot::getResourceType).filter(Objects::nonNull).collect(Collectors.toList()))) {
            return false;
        }

        //checks if each extraSlots contains its only one resource's type
        for (ExtraSlots toCheck : extraSlots)
            if (toCheck.isActivated())
                if (!toCheck.isLegal())
                    return false;

        return true;
    }

    /**
     * Report how many new resources taken from the MarketTray has not been stored in the depots by the player and
     * resets the resources from the MarketTray.
     *
     * @return the number of the resources in this container
     */
    public int getNumberOfRemainingResources() {
        return this.resourcesFromMarket.getNumberOfRemainingResources();
    }

}