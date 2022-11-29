package com.gempukku.startrek.server.game.effect.zone;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.libgdx.network.id.ServerEntityIdSystem;
import com.gempukku.startrek.game.*;
import com.gempukku.startrek.game.event.CardChangedZones;
import com.gempukku.startrek.game.event.ShipMoved;
import com.gempukku.startrek.game.filter.CardFilteringSystem;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.mission.MissionOperations;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.*;
import com.gempukku.startrek.server.game.deck.HiddenDilemmaStackComponent;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;
import com.gempukku.startrek.server.game.stack.ObjectStackSystem;

import java.util.function.Consumer;

public class ZoneOperations extends BaseSystem {
    private EventSystem eventSystem;
    private PlayerResolverSystem playerResolverSystem;
    private ObjectStackSystem objectStackSystem;
    private ServerEntityIdSystem serverEntityIdSystem;
    private GameEntityProvider gameEntityProvider;
    private CardFilteringSystem cardFilteringSystem;
    private MissionOperations missionOperations;

    private ComponentMapper<CardInPlayComponent> cardInPlayComponentMapper;
    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<FaceDownCardInMissionComponent> faceDownCardInMissionComponentMapper;
    private ComponentMapper<CardInMissionComponent> cardInMissionComponentMapper;
    private ComponentMapper<CardInHandComponent> cardInHandComponentMapper;
    private ComponentMapper<CardInBrigComponent> cardInBrigComponentMapper;
    private ComponentMapper<CardInCoreComponent> cardInCoreComponentMapper;
    private ComponentMapper<CardInDiscardComponent> cardInDiscardComponentMapper;
    private ComponentMapper<CardInDilemmaPileComponent> cardInDilemmaPileComponentMapper;
    private ComponentMapper<ObjectOnStackComponent> cardOnStackComponentMapper;

    public void moveFromTopOfDeckToHand(Entity playerEntity) {
        Entity cardEntity = removeTopCardOfDeck(playerEntity);
        if (cardEntity != null) {
            CardComponent card = cardEntity.getComponent(CardComponent.class);
            CardZone oldZone = card.getCardZone();
            CardZone newZone = CardZone.Hand;
            card.setCardZone(newZone);
            CardInHandComponent cardInHand = cardInHandComponentMapper.create(cardEntity);
            cardInHand.setOwner(card.getOwner());
            eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

            PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
            stats.setHandCount(stats.getHandCount() + 1);
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

            notifyZoneChange(cardEntity, oldZone, newZone, null, -1);
        }
    }

    private void notifyZoneChange(Entity cardEntity, CardZone fromZone, CardZone toZone,
                                  String missionOwner, int missionIndex) {
        String owner = cardEntity.getComponent(CardComponent.class).getOwner();
        if (fromZone != null && fromZone != toZone) {
            eventSystem.fireEvent(
                    new CardChangedZones(serverEntityIdSystem.getEntityId(cardEntity), owner, fromZone, toZone,
                            missionOwner, missionIndex), gameEntityProvider.getGameEntity());
        }
    }

    public void removeCardFromHand(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        cardInHandComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setHandCount(stats.getHandCount() - 1);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    public void moveEffectToStack(Entity effectEntity, int abilityIndex) {
        ObjectOnStackComponent cardOnStack = cardOnStackComponentMapper.create(effectEntity);
        cardOnStack.setType("effect");
        cardOnStack.setAbilityIndex(abilityIndex);
        objectStackSystem.stackEntity(effectEntity);
        eventSystem.fireEvent(EntityUpdated.instance, effectEntity);
    }

    public void removeEffectFromStack() {
        Entity effectEntity = objectStackSystem.removeTopMostFromStack();
        world.deleteEntity(effectEntity);
    }

    public void moveFromCurrentZoneToStack(Entity cardEntity, int abilityIndex) {
        String missionOwner = null;
        int missionIndex = -1;
        CardInMissionComponent inMission = cardEntity.getComponent(CardInMissionComponent.class);
        if (inMission != null) {
            missionOwner = inMission.getMissionOwner();
            missionIndex = inMission.getMissionIndex();
        }
        removeFromCurrentZone(cardEntity);

        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Stack;
        card.setCardZone(newZone);
        ObjectOnStackComponent cardOnStack = cardOnStackComponentMapper.create(cardEntity);
        cardOnStack.setType("card");
        cardOnStack.setAbilityIndex(abilityIndex);
        objectStackSystem.stackEntity(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        notifyZoneChange(cardEntity, oldZone, newZone, missionOwner, missionIndex);
    }

    public void removeCardFromStack(Entity cardEntity) {
        cardOnStackComponentMapper.remove(cardEntity);
        objectStackSystem.removeFromStack(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveFromCurrentZoneToBrig(Entity cardEntity, Entity brigPlayerEntity) {
        String missionOwner = null;
        int missionIndex = -1;
        CardInMissionComponent inMission = cardEntity.getComponent(CardInMissionComponent.class);
        if (inMission != null) {
            missionOwner = inMission.getMissionOwner();
            missionIndex = inMission.getMissionIndex();
        }
        removeFromCurrentZone(cardEntity);

        cardInPlayComponentMapper.create(cardEntity);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Brig;
        card.setCardZone(newZone);
        GamePlayerComponent gamePlayer = brigPlayerEntity.getComponent(GamePlayerComponent.class);
        CardInBrigComponent cardInBrig = cardInBrigComponentMapper.create(cardEntity);
        cardInBrig.setBrigOwner(gamePlayer.getName());
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        notifyZoneChange(cardEntity, oldZone, newZone, missionOwner, missionIndex);
    }

    public void removeCardFromBrig(Entity cardEntity) {
        cardInBrigComponentMapper.remove(cardEntity);
        cardInPlayComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveFromCurrentZoneToMission(Entity cardEntity, Entity missionEntity, boolean faceUp) {
        removeFromCurrentZone(cardEntity);

        cardInPlayComponentMapper.create(cardEntity);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Mission;
        CardInMissionComponent mission = missionEntity.getComponent(CardInMissionComponent.class);
        card.setCardZone(newZone);
        CardInMissionComponent cardInMission = cardInMissionComponentMapper.create(cardEntity);
        cardInMission.setMissionOwner(mission.getMissionOwner());
        cardInMission.setMissionIndex(mission.getMissionIndex());
        if (faceUp) {
            faceUpCardInMissionComponentMapper.create(cardEntity);
        } else {
            FaceDownCardInMissionComponent faceDownCard = faceDownCardInMissionComponentMapper.create(cardEntity);
            faceDownCard.setOwner(card.getOwner());

            incrementFaceDownCardCount(card.getOwner(), mission.getMissionOwner(), mission.getMissionIndex());
        }
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        notifyZoneChange(cardEntity, oldZone, newZone, mission.getMissionOwner(), mission.getMissionIndex());
    }

    public void removeCardFromMission(Entity cardEntity) {
        cardInPlayComponentMapper.remove(cardEntity);
        CardInMissionComponent cardInMission = cardInMissionComponentMapper.get(cardEntity);
        FaceDownCardInMissionComponent faceDownInMission = faceDownCardInMissionComponentMapper.get(cardEntity);
        if (faceDownInMission != null) {
            String missionOwner = cardInMission.getMissionOwner();
            int missionIndex = cardInMission.getMissionIndex();
            faceDownCardInMissionComponentMapper.remove(cardEntity);

            CardComponent card = cardEntity.getComponent(CardComponent.class);
            decrementFaceDownCardCount(card.getOwner(), missionOwner, missionIndex);
        }
        faceUpCardInMissionComponentMapper.remove(cardEntity);
        cardInMissionComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void attachToShip(Entity shipEntity, Entity cardEntity) {
        CardInMissionComponent cardInMission = cardInMissionComponentMapper.get(cardEntity);
        FaceDownCardInMissionComponent faceDownInMission = faceDownCardInMissionComponentMapper.get(cardEntity);
        if (faceDownInMission != null) {
            String missionOwner = cardInMission.getMissionOwner();
            int missionIndex = cardInMission.getMissionIndex();

            CardComponent card = cardEntity.getComponent(CardComponent.class);
            decrementFaceDownCardCount(card.getOwner(), missionOwner, missionIndex);
            incrementFaceDownCardOnCard(shipEntity);
        }

        String shipId = serverEntityIdSystem.getEntityId(shipEntity);
        CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);
        cardInPlay.setAttachedToId(shipId);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    private void incrementFaceDownCardOnCard(Entity shipEntity) {
        CardInPlayComponent cardInPlay = shipEntity.getComponent(CardInPlayComponent.class);
        cardInPlay.setAttachedFaceDownCount(cardInPlay.getAttachedFaceDownCount() + 1);
        eventSystem.fireEvent(EntityUpdated.instance, shipEntity);
    }

    private void decrementFaceDownCardOnCard(Entity shipEntity) {
        CardInPlayComponent cardInPlay = shipEntity.getComponent(CardInPlayComponent.class);
        cardInPlay.setAttachedFaceDownCount(cardInPlay.getAttachedFaceDownCount() - 1);
        eventSystem.fireEvent(EntityUpdated.instance, shipEntity);
    }

    public void unattachFromShip(Entity shipEntity, Entity cardEntity) {
        CardInMissionComponent cardInMission = cardInMissionComponentMapper.get(cardEntity);
        FaceDownCardInMissionComponent faceDownInMission = faceDownCardInMissionComponentMapper.get(cardEntity);
        if (faceDownInMission != null) {
            String missionOwner = cardInMission.getMissionOwner();
            int missionIndex = cardInMission.getMissionIndex();

            CardComponent card = cardEntity.getComponent(CardComponent.class);
            String cardOwner = card.getOwner();

            incrementFaceDownCardCount(cardOwner, missionOwner, missionIndex);
            decrementFaceDownCardOnCard(shipEntity);
        }

        CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);
        cardInPlay.setAttachedToId(null);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    private void decrementFaceDownCardCount(String cardOwner, String missionOwner, int missionIndex) {
        Entity missionEntity = missionOperations.findMission(missionOwner, missionIndex);
        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
        ObjectMap<String, Integer> playerFaceDownCardsCount = mission.getPlayerFaceDownCardsCount();
        int oldCount = playerFaceDownCardsCount.get(cardOwner, 0);
        playerFaceDownCardsCount.put(cardOwner, oldCount - 1);
        eventSystem.fireEvent(EntityUpdated.instance, missionEntity);
    }

    private void incrementFaceDownCardCount(String cardOwner, String missionOwner, int missionIndex) {
        Entity missionEntity = missionOperations.findMission(missionOwner, missionIndex);
        MissionComponent mission = missionEntity.getComponent(MissionComponent.class);
        ObjectMap<String, Integer> playerFaceDownCardsCount = mission.getPlayerFaceDownCardsCount();
        int oldCount = playerFaceDownCardsCount.get(cardOwner, 0);
        playerFaceDownCardsCount.put(cardOwner, oldCount + 1);
        eventSystem.fireEvent(EntityUpdated.instance, missionEntity);
    }

    public void attachFromShipToShip(Entity fromShipEntity, Entity toShipEntity, Entity cardEntity) {
        String shipId = serverEntityIdSystem.getEntityId(toShipEntity);
        CardInPlayComponent cardInPlay = cardEntity.getComponent(CardInPlayComponent.class);
        cardInPlay.setAttachedToId(shipId);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        decrementFaceDownCardOnCard(fromShipEntity);
        incrementFaceDownCardOnCard(toShipEntity);
    }

    public void moveShip(Entity shipEntity, Entity missionCardEntity) {
        CardInMissionComponent shipInMission = shipEntity.getComponent(CardInMissionComponent.class);
        String missionOwnerFrom = shipInMission.getMissionOwner();
        int missionIndexFrom = shipInMission.getMissionIndex();

        CardInMissionComponent cardInMission = missionCardEntity.getComponent(CardInMissionComponent.class);
        String missionOwner = cardInMission.getMissionOwner();
        int missionIndex = cardInMission.getMissionIndex();

        shipInMission.setMissionOwner(missionOwner);
        shipInMission.setMissionIndex(missionIndex);
        eventSystem.fireEvent(EntityUpdated.instance, shipEntity);

        cardFilteringSystem.forEachCardInPlay(shipEntity, null, new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        CardInMissionComponent cardInMission = entity.getComponent(CardInMissionComponent.class);
                        cardInMission.setMissionOwner(missionOwner);
                        cardInMission.setMissionIndex(missionIndex);
                        eventSystem.fireEvent(EntityUpdated.instance, entity);
                    }
                }, "attachedTo(self)"
        );

        eventSystem.fireEvent(new ShipMoved(
                serverEntityIdSystem.getEntityId(shipEntity),
                missionOwnerFrom, missionIndexFrom,
                missionOwner, missionIndex), gameEntityProvider.getGameEntity());
    }

    public void moveFromCurrentZoneToCore(Entity cardEntity) {
        String missionOwner = null;
        int missionIndex = -1;
        CardInMissionComponent inMission = cardEntity.getComponent(CardInMissionComponent.class);
        if (inMission != null) {
            missionOwner = inMission.getMissionOwner();
            missionIndex = inMission.getMissionIndex();
        }
        removeFromCurrentZone(cardEntity);

        cardInPlayComponentMapper.create(cardEntity);
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Core;
        card.setCardZone(newZone);
        cardInCoreComponentMapper.create(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        notifyZoneChange(cardEntity, oldZone, newZone, missionOwner, missionIndex);
    }

    public void removeCardFromCore(Entity cardEntity) {
        cardInCoreComponentMapper.remove(cardEntity);
        cardInPlayComponentMapper.remove(cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void moveFromCurrentZoneToBottomOfDeck(Entity cardEntity) {
        String missionOwner = null;
        int missionIndex = -1;
        CardInMissionComponent inMission = cardEntity.getComponent(CardInMissionComponent.class);
        if (inMission != null) {
            missionOwner = inMission.getMissionOwner();
            missionIndex = inMission.getMissionIndex();
        }
        removeFromCurrentZone(cardEntity);

        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.Deck;
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        deck.getCards().insert(0, cardEntity.getId());
        card.setCardZone(newZone);

        stats.setDeckCount(stats.getDeckCount() + 1);

        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone, missionOwner, missionIndex);
    }

    public void setupCardToTopOfDeck(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone newZone = CardZone.Deck;
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        deck.getCards().add(cardEntity.getId());
        card.setCardZone(newZone);

        stats.setDeckCount(stats.getDeckCount() + 1);

        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    private Entity removeTopCardOfDeck(Entity playerEntity) {
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        Array<Integer> cards = deck.getCards();
        if (cards.size == 0)
            return null;

        Entity cardEntity = world.getEntity(cards.pop());

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDeckCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        return cardEntity;
    }

    public void removeCardFromDeck(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
        Array<Integer> cards = deck.getCards();
        cards.removeValue(cardEntity.getId(), false);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDeckCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    public void moveFromCurrentZoneToDiscardPile(Entity cardEntity) {
        String missionOwner = null;
        int missionIndex = -1;
        CardInMissionComponent inMission = cardEntity.getComponent(CardInMissionComponent.class);
        if (inMission != null) {
            missionOwner = inMission.getMissionOwner();
            missionIndex = inMission.getMissionIndex();
        }
        removeFromCurrentZone(cardEntity);

        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.DiscardPile;
        card.setCardZone(newZone);
        cardInDiscardComponentMapper.create(cardEntity);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDiscardPileComponent discard = playerEntity.getComponent(PlayerDiscardPileComponent.class);
        Array<String> cards = discard.getCards();
        cards.add(serverEntityIdSystem.getEntityId(cardEntity));
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone, missionOwner, missionIndex);
    }

    public void removeCardFromDiscardPile(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        cardInDiscardComponentMapper.remove(cardEntity);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDiscardPileComponent discard = playerEntity.getComponent(PlayerDiscardPileComponent.class);
        Array<String> cards = discard.getCards();
        cards.removeValue(serverEntityIdSystem.getEntityId(cardEntity), false);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    public void moveFromCurrentZoneToBottomOfDilemmaPile(Entity cardEntity, boolean faceUp) {
        String missionOwner = null;
        int missionIndex = -1;
        CardInMissionComponent inMission = cardEntity.getComponent(CardInMissionComponent.class);
        if (inMission != null) {
            missionOwner = inMission.getMissionOwner();
            missionIndex = inMission.getMissionIndex();
        }
        removeFromCurrentZone(cardEntity);

        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.DilemmaPile;
        card.setCardZone(newZone);
        CardInDilemmaPileComponent cardInDilemmaPile = cardInDilemmaPileComponentMapper.create(cardEntity);
        cardInDilemmaPile.setFaceUp(faceUp);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> cards = dilemmaPile.getCards();
        cards.insert(0, cardEntity.getId());
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDilemmaCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);

        notifyZoneChange(cardEntity, oldZone, newZone, missionOwner, missionIndex);
    }

    public void moveFromCurrentZoneToTopOfDilemmaStack(Entity cardEntity) {
        String missionOwner = null;
        int missionIndex = -1;
        CardInMissionComponent inMission = cardEntity.getComponent(CardInMissionComponent.class);
        if (inMission != null) {
            missionOwner = inMission.getMissionOwner();
            missionIndex = inMission.getMissionIndex();
        }
        removeFromCurrentZone(cardEntity);

        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone oldZone = card.getCardZone();
        CardZone newZone = CardZone.DilemmaStack;
        card.setCardZone(newZone);

        Entity gameEntity = gameEntityProvider.getGameEntity();
        DilemmaStackComponent dilemmaStack = gameEntity.getComponent(DilemmaStackComponent.class);
        dilemmaStack.setCardCount(dilemmaStack.getCardCount() + 1);

        HiddenDilemmaStackComponent hiddenDilemmaStack = gameEntity.getComponent(HiddenDilemmaStackComponent.class);
        Array<Integer> cards = hiddenDilemmaStack.getCards();
        cards.insert(0, cardEntity.getId());

        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, gameEntity);

        notifyZoneChange(cardEntity, oldZone, newZone, missionOwner, missionIndex);
    }

    public void setupCardToTopOfDilemmaPile(Entity cardEntity, boolean faceUp) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone newZone = CardZone.DilemmaPile;
        card.setCardZone(newZone);
        CardInDilemmaPileComponent cardInDilemmaPile = cardInDilemmaPileComponentMapper.create(cardEntity);
        cardInDilemmaPile.setFaceUp(faceUp);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> cards = dilemmaPile.getCards();
        cards.add(cardEntity.getId());
        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDilemmaCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
    }

    public Entity removeTopCardFromDilemmaPile(Entity playerEntity) {
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> cards = dilemmaPile.getCards();
        if (cards.size == 0)
            return null;

        Entity cardEntity = world.getEntity(cards.pop());
        cardInDilemmaPileComponentMapper.remove(cardEntity);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDilemmaCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);

        return cardEntity;
    }

    public void removeCardFromDilemmaPile(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(card.getOwner());
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> cards = dilemmaPile.getCards();
        cards.removeValue(cardEntity.getId(), false);
        cardInDilemmaPileComponentMapper.remove(cardEntity);

        PlayerPublicStatsComponent stats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        stats.setDilemmaCount(cards.size);
        eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        eventSystem.fireEvent(EntityUpdated.instance, cardEntity);
    }

    public void removeFromCurrentZone(Entity cardEntity) {
        CardComponent card = cardEntity.getComponent(CardComponent.class);
        CardZone zone = card.getCardZone();
        if (zone != null) {
            switch (zone) {
                case Hand:
                    removeCardFromHand(cardEntity);
                    break;
                case Core:
                    removeCardFromCore(cardEntity);
                    break;
                case Stack:
                    removeCardFromStack(cardEntity);
                    break;
                case Brig:
                    removeCardFromBrig(cardEntity);
                    break;
                case Mission:
                    removeCardFromMission(cardEntity);
                    break;
                case Deck:
                    removeCardFromDeck(cardEntity);
                    break;
                case DiscardPile:
                    removeCardFromDiscardPile(cardEntity);
                    break;
                case DilemmaPile:
                    removeCardFromDilemmaPile(cardEntity);
                    break;
            }
        }
    }

    @Override
    protected void processSystem() {

    }
}
