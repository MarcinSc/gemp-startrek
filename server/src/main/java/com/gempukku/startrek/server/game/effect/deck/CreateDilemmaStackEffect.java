package com.gempukku.startrek.server.game.effect.deck;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.startrek.common.IdProviderSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.GameEntityProvider;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.event.DilemmaPileShuffled;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.game.zone.CardInDilemmaPileComponent;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

import java.util.function.Function;

public class CreateDilemmaStackEffect extends OneTimeEffectSystem {
    private PlayerResolverSystem playerResolverSystem;
    private IdProviderSystem idProviderSystem;
    private EventSystem eventSystem;
    private GameEntityProvider gameEntityProvider;

    public CreateDilemmaStackEffect() {
        super("createDilemmaStack");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        int cardCount = Integer.parseInt(memory.getValue(gameEffect.getDataString("amountMemory")));
        // TODO: modify the card count here, from modifiers
        String dilemmaUsername = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, gameEffect.getDataString("player"));
        Entity playerEntity = playerResolverSystem.findPlayerEntity(dilemmaUsername);
        PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
        Array<Integer> dilemmaPileCards = dilemmaPile.getCards();

        int cardsToPresent = Math.min(cardCount, dilemmaPileCards.size);
        Array<Entity> cards = new Array<>();
        for (int i = 0; i < cardsToPresent; i++) {
            Entity cardEntity = world.getEntity(getIthCardFromTop(dilemmaPileCards, i));
            CardInDilemmaPileComponent cardInDilemmaPile = cardEntity.getComponent(CardInDilemmaPileComponent.class);
            if (cardInDilemmaPile.isFaceUp()) {
                shuffleDilemmaPile(dilemmaUsername, dilemmaPileCards);
            }
            cards.add(world.getEntity(getIthCardFromTop(dilemmaPileCards, i)));
        }

        String dilemmaCardIds = StringUtils.merge(cards, new Function<Entity, String>() {
            @Override
            public String apply(Entity entity) {
                return entity.getComponent(CardComponent.class).getCardId();
            }
        });

        memory.setValue(gameEffect.getDataString("dilemmaMemory"), dilemmaCardIds);
        memory.setValue(gameEffect.getDataString("costResultMemory"), String.valueOf(cardCount));
    }

    private Integer getIthCardFromTop(Array<Integer> dilemmaPileCards, int i) {
        return dilemmaPileCards.get(dilemmaPileCards.size - 1 - i);
    }

    private void shuffleDilemmaPile(String pileOwner, Array<Integer> dilemmaPileCards) {
        dilemmaPileCards.shuffle();
        for (int j = 0; j < dilemmaPileCards.size; j++) {
            world.getEntity(dilemmaPileCards.get(j)).getComponent(CardInDilemmaPileComponent.class).setFaceUp(false);
        }
        eventSystem.fireEvent(new DilemmaPileShuffled(pileOwner), gameEntityProvider.getGameEntity());
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"player", "amountMemory", "costResultMemory", "dilemmaMemory"},
                new String[]{});
        playerResolverSystem.validatePlayer(effect.getString("player"));
    }
}
