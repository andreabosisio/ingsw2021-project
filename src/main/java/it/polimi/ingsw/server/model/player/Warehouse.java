package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.server.model.resources.NonStorableResources;
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
     *
     * Graphical description of the indexes:
     * Market resources:    0 1 2 3
     *
     * First depot:                  4
     * Second depot:             5       6
     * Third depot:          7       8       9
     *
     * 1° extra slots:      10 11
     * 2° extra slots:      12 13
     * StrongBox:           14,15,...
     */

    public class Warehouse {

        /**
         * Index of the position where starts the zone of the slots containing the new resources taken from the MarketTray.
         */
        final int startResourcesFromMarketSlotsZone = 0;
        final int availableResourcesFromMarketSlots = 4;

        /**
         * Index of the position where starts the zone of the warehouse depots.
         */
        final int startWarehouseDepotsZone = 4;
        final int startFirstDepot = 4;
        final int startSecondDepot = 5;
        final int startThirdDepot = 7;
        final int availableDepotsSlots = 6;

        /**
         * Index of the position where starts the first zone of the extra slots provided by the ProductionLeaderCard.
         */
        final int startFirstExtraSlotsZone = 10;
        final int availableFirstExtraSlots = 2;

        /**
         * Index of the position where starts the second zone of the extra slots provided by the ProductionLeaderCard.
         */
        final int startSecondExtraSlotsZone = 12;
        final int availableSecondExtraSlots = 2;


        /**
         * Index of the position where starts the zone of the StrongBox.
         */
        final int startStrongBoxZone = availableResourcesFromMarketSlots + availableDepotsSlots + availableFirstExtraSlots + availableSecondExtraSlots;


        private final MarketSlots resourcesFromMarket = new MarketSlots();


        private final List<Depot> depots = new ArrayList<Depot>(){{
            add(new Depot(startSecondDepot - startFirstDepot));
            add(new Depot(startThirdDepot - startSecondDepot));
            add(new Depot(startFirstExtraSlotsZone - startThirdDepot));
        }};

        private final List<ExtraSlots> extraSlots = new ArrayList<ExtraSlots>(0){{
            add(new ExtraSlots());
            add(new ExtraSlots());
        }};

        private final StrongBox strongBox = new StrongBox();

        private Map<Integer, TranslatedPositionTuple> positionMap = new HashMap<>();

        /**
         * Verify if the given position is in the zone of the slots containing the new resources taken from the MarketTray.
         *
         * @param position that represents a slot containing a Resource
         * @return true if the given position represents a slot of the described zone.
         */
        boolean isInResourcesFromMarketSlotsZone(int position){
            return position >= startResourcesFromMarketSlotsZone && position < startWarehouseDepotsZone;
        }

        /**
         * Verify if the given position is in the zone of the depots.
         *
         * @param position that represents a slot containing a Resource
         * @return true if the given position represents a slot of the described zone.
         */
        boolean isInDepotsZone(int position){
            return position >= startWarehouseDepotsZone && position < startFirstExtraSlotsZone;
        }

        /**
         * Verify if the given position is in the first zone of the extra slots provided by a WarehouseLeaderCard.
         *
         * @param position that represents a slot containing a Resource
         * @return true if the given position represents a slot of the described zone.
         */
        boolean isInFirstExtraSlotsZone(int position){
            return (position >= startFirstExtraSlotsZone && position < startSecondExtraSlotsZone);
        }

        /**
         * Verify if the given position is in the second zone of the extra slots provided by a WarehouseLeaderCard.
         *
         * @param position that represents a slot containing a Resource
         * @return true if the given position represents a slot of the described zone.
         */
        boolean isInSecondExtraSlotsZone(int position){
            return (position >= startSecondExtraSlotsZone && position < startStrongBoxZone);
        }

        /**
         * Verify if the given position is in the StrongBox.
         *
         * @param position that represents a slot containing a Resource
         * @return true if the given position represents a slot of the described zone.
         */
        boolean isInStrongBoxZone(int position){
            return position >= startStrongBoxZone;
        }

        /**
         * Activate the extra slots provided by an WarehouseLeaderCard.
         *
         * @param slotsType the resource's type of the extra slots
         * @return true if the extra slots can be activated
         */
        public boolean addExtraSlots(Resource slotsType){
            for(ExtraSlots remainingExtraSlots : extraSlots){
                if (!remainingExtraSlots.getIsActivated()){
                    remainingExtraSlots.activateExtraSlots(slotsType);
                    return true;
                }
            }
            return false;
        }

        /**
         * Verify if the player has activated the indicated extra slots zone
         * @param extraSlotsZoneIndex index of the extra slots zone that has to be verified
         * @return true if the extra slots zone to verify is activated
         */
        public boolean hasExtraSlots(int extraSlotsZoneIndex){
            return extraSlots.get(extraSlotsZoneIndex).getIsActivated();
        }

        /**
         * Map the given position into a TranslatedPositionTuple that has the correct zone of the given position and the
         * shifted position for that zone.
         *
         * @param position that has to be translated into this Warehouse storage logic
         * @return true if the given position has been correctly translated
         * @throws InvalidIndexException if position is negative
         * @throws NonAccessibleSlotException if the position represent a slot that's not accessible
         */
        private boolean translatePosition(int position) throws InvalidIndexException, NonAccessibleSlotException {
            if(isInResourcesFromMarketSlotsZone(position))
                positionMap.put(position, new TranslatedPositionTuple(position, resourcesFromMarket));
            else if(isInDepotsZone(position))
                if(position < startSecondDepot) //first depot
                    positionMap.put(position, new TranslatedPositionTuple(position - startFirstDepot, depots.get(0)));
                else if(position < startThirdDepot) //second depot
                    positionMap.put(position, new TranslatedPositionTuple(position - startSecondDepot, depots.get(1)));
                else //third depot
                    positionMap.put(position, new TranslatedPositionTuple(position - startThirdDepot, depots.get(2)));
            else if(isInFirstExtraSlotsZone(position)) //first extra slots
                if(hasExtraSlots(0)) //first extra slots are activated
                    positionMap.put(position, new TranslatedPositionTuple(position - startFirstExtraSlotsZone, extraSlots.get(0)));
                else
                    throw new NonAccessibleSlotException(); //extra slots have not been activated yet
            else if(isInSecondExtraSlotsZone(position)) //second extra slots
                if(hasExtraSlots(1)) //second extra slots are activated
                    positionMap.put(position, new TranslatedPositionTuple(position - startSecondExtraSlotsZone, extraSlots.get(1)));
                else
                    throw new NonAccessibleSlotException(); //extra slots have not been activated yet
            else if(isInStrongBoxZone(position))
                positionMap.put(position, new TranslatedPositionTuple(position - startStrongBoxZone, strongBox));
            else if(position < 0)
                throw new InvalidIndexException("Negative position doesn't exist"); //invalid position
            return true;
        }

        /**
         * Store the resourceToStock in the StrongBox.
         *
         * @param producedResource to store
         * @return true if the resourceToStock has been correctly stored
         * @throws NonStorableResourceException if the given resource is one of the NonStorableResources
         */
        public boolean addResourceToStrongBox(Resource producedResource) throws NonStorableResourceException {
            if(NonStorableResources.getNonStorableResources().contains(producedResource))
                throw new NonStorableResourceException();
            return strongBox.addResource(producedResource);
        }

        /**
         * Store the resources taken from the MarketTray in the ResourcesFromMarketSlotsZone.
         *
         * @param newResources taken from the MarketTray
         * @return the resources has been stored successfully
         * @throws NonStorableResourceException if the given resources contains one of the NonStorableResources
         */
        public boolean addResourceFromMarket(List<Resource> newResources) throws NonStorableResourceException {
            if(newResources.size() > availableResourcesFromMarketSlots)
                return false;
            if(!Collections.disjoint(newResources, NonStorableResources.getNonStorableResources()))
                throw new NonStorableResourceException();
            return resourcesFromMarket.addResources(newResources);
        }

        /**
         * Take the resource stored in the given position.
         *
         * @param position of the resource to be taken
         * @return the chosen resource
         * @throws EmptySlotException if chosen the slot is empty
         * @throws InvalidIndexException if the position is negative
         * @throws NonAccessibleSlotException if the position represent a slot that's not accessible
         */
        public Resource getResource(int position) throws EmptySlotException, InvalidIndexException, NonAccessibleSlotException {
            if(!isInResourcesFromMarketSlotsZone(position)){ //cannot take resources from ResourcesFromMarketSlotsZone
                if(!positionMap.containsKey(position)){
                    translatePosition(position);
                }
                Resource chosenResource = positionMap.get(position).getResource();
                if(chosenResource == null){
                    throw new EmptySlotException();
                }else{
                    return chosenResource;
                }
            }
            throw new NonAccessibleSlotException();
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
         * @throws InvalidIndexException if one of the given position is negative
         * @throws EmptySlotException if one of the selected slots is empty
         * @throws NonAccessibleSlotException if one of the selected slots is non accessible
         */
        public boolean swap(int initPosition, int finalPosition) throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
            //can only swap a Resource that's just been taken from the MarketTray
            // or a Resource that's already stored on a depot or in an extra slot
            if(isInStrongBoxZone(initPosition))
                return false;
            //can only swap a Resource to a depot or to an extra slot
            if(isInResourcesFromMarketSlotsZone(finalPosition) || isInStrongBoxZone(finalPosition))
                return false;

            if(!positionMap.containsKey(initPosition))
                translatePosition(initPosition);
            if(!positionMap.containsKey(finalPosition))
                translatePosition(finalPosition);

            Resource tempResource = positionMap.get(initPosition).getResource();
            if(tempResource == null) {
                throw new EmptySlotException();
            }
            return
                positionMap.get(initPosition).setResource(positionMap.get(finalPosition).getResource()) &&
                    positionMap.get(finalPosition).setResource(tempResource);

        }

        /**
         * Verify if each warehouse depots contains only a single type of Resource and different from the others;
         * Verify if each extra slots contains only the required type of Resource provided by the WarehouseLeaderCard.
         *
         * @return true if the resources stored in the Warehouse depots and in the extra slots are correctly organized
         */
        public boolean isLegalReorganization() {
            for (Depot depot : depots) {
                if (!depot.isLegal())
                    return false;
            }
            if(!depots.stream().map(Depot::getTypeOfResources).filter(Objects::nonNull).distinct().collect(Collectors.toList()).
                    equals(depots.stream().map(Depot::getTypeOfResources).filter(Objects::nonNull).collect(Collectors.toList()))){
                return false;
            }

            for(ExtraSlots toCheck : extraSlots)
                if(toCheck.getIsActivated())
                    if(!toCheck.isLegal())
                        return false;

            return true;
        }

        /**
         * Report how many new resources taken from the MarketTray has not been stored in the depots by the player.
         *
         * @return the number of the resources in this container
         */
        public int getNumberOfRemainedResources() {
            return this.resourcesFromMarket.getNumberOfRemainedResources();
        }
    }
