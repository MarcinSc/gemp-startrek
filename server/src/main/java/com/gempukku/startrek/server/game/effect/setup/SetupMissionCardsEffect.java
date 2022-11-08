package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.CardZone;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.mission.FaceUpCardInMissionComponent;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.Comparator;
import java.util.function.Consumer;

public class SetupMissionCardsEffect extends OneTimeEffectSystem {
    private CardLookupSystem cardLookupSystem;
    private EventSystem eventSystem;
    private PlayerResolverSystem playerResolverSystem;
    private ComponentMapper<MissionComponent> missionStatusComponentMapper;
    private ComponentMapper<FaceUpCardInMissionComponent> faceUpCardInMissionComponentMapper;
    private ComponentMapper<CardComponent> cardComponentMapper;

    public SetupMissionCardsEffect() {
        super("setupMissionCards");
    }

    @Override
    protected void processOneTimeEffect(Entity gameEffectEntity, GameEffectComponent gameEffect, Memory memory) {
        String player = playerResolverSystem.resolvePlayerUsername(gameEffectEntity, memory,
                gameEffect.getDataString("player"));
        Array<Entity> playerMissions = getAllPlayerMissions(player);
        playerMissions.sort(
                new Comparator<Entity>() {
                    @Override
                    public int compare(Entity o1, Entity o2) {
                        CardDefinition card1 = cardLookupSystem.getCardDefinition(o1.getComponent(CardComponent.class).getCardId());
                        CardDefinition card2 = cardLookupSystem.getCardDefinition(o1.getComponent(CardComponent.class).getCardId());
                        int missionTypeComparison = card1.getMissionType().compareTo(card2.getMissionType());
                        if (missionTypeComparison != 0)
                            return missionTypeComparison;
                        return card1.getTitle().compareTo(card2.getTitle());
                    }
                });
        for (int i = 0; i < playerMissions.size; i++) {
            Entity missionEntity = playerMissions.get(i);
            CardComponent card = cardComponentMapper.get(missionEntity);
            card.setCardZone(CardZone.MISSIONS);
            FaceUpCardInMissionComponent missionZone = faceUpCardInMissionComponentMapper.create(missionEntity);
            missionZone.setMissionOwner(player);
            missionZone.setMissionIndex(i);
            eventSystem.fireEvent(EntityUpdated.instance, missionEntity);
        }
    }

    private Array<Entity> getAllPlayerMissions(String player) {
        Array<Entity> result = new Array<>();
        LazyEntityUtil.forEachEntityWithComponent(world, CardComponent.class,
                new Consumer<Entity>() {
                    @Override
                    public void accept(Entity entity) {
                        CardComponent cardComponent = entity.getComponent(CardComponent.class);
                        if (cardComponent.getOwner().equals(player)) {
                            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardComponent.getCardId());
                            if (cardDefinition.getType() == CardType.Mission) {
                                result.add(entity);
                            }
                        }
                    }
                });
        return result;
    }
}
