package com.gempukku.startrek.server.game.effect.setup;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.card.CardType;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.mission.MissionComponent;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.CardInMissionComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;
import com.gempukku.startrek.server.game.effect.zone.ZoneOperations;

import java.util.Comparator;
import java.util.function.Consumer;

public class SetupMissionCardsEffect extends OneTimeEffectSystem {
    private CardLookupSystem cardLookupSystem;
    private EventSystem eventSystem;
    private PlayerResolverSystem playerResolverSystem;
    private ComponentMapper<MissionComponent> missionComponentMapper;
    private ComponentMapper<CardInMissionComponent> cardInMissionComponentMapper;
    private ComponentMapper<CardComponent> cardComponentMapper;
    private ZoneOperations zoneOperations;

    public SetupMissionCardsEffect() {
        super("setupMissionCards");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String username = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory,
                gameEffect.getDataString("player"));
        Array<Entity> playerMissions = getAllPlayerMissions(username);
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
            missionComponentMapper.create(missionEntity);
            CardInMissionComponent cardInMission = cardInMissionComponentMapper.create(missionEntity);
            cardInMission.setMissionOwner(username);
            cardInMission.setMissionIndex(i);
            zoneOperations.moveFromCurrentZoneToMission(missionEntity, missionEntity, true);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"player"},
                new String[]{});
        playerResolverSystem.validatePlayer(effect.getString("player"));
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
