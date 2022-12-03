package com.gempukku.startrek.server.game.effect.deck;

import com.artemis.Entity;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.lib.artemis.event.EventSystem;
import com.gempukku.libgdx.network.EntityUpdated;
import com.gempukku.startrek.game.GameEntityProvider;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.event.DilemmaPileShuffled;
import com.gempukku.startrek.game.player.PlayerResolverSystem;
import com.gempukku.startrek.server.game.deck.PlayerDeckComponent;
import com.gempukku.startrek.server.game.deck.PlayerDilemmaPileComponent;
import com.gempukku.startrek.server.game.effect.GameEffectComponent;
import com.gempukku.startrek.server.game.effect.OneTimeEffectSystem;

public class ShuffleDeckEffect extends OneTimeEffectSystem {
    private GameEntityProvider gameEntityProvider;
    private PlayerResolverSystem playerResolverSystem;
    private EventSystem eventSystem;

    public ShuffleDeckEffect() {
        super("shuffleDeck");
    }

    @Override
    protected void processOneTimeEffect(Entity sourceEntity, Memory memory, GameEffectComponent gameEffect) {
        String player = getOptionalFromMemory(memory, gameEffect, "player", "playerMemory");
        String username = playerResolverSystem.resolvePlayerUsername(sourceEntity, memory, player);
        Entity playerEntity = playerResolverSystem.findPlayerEntity(username);
        String deckType = gameEffect.getDataString("deck");
        if (deckType.equals("dilemmaDeck")) {
            PlayerDilemmaPileComponent dilemmaPile = playerEntity.getComponent(PlayerDilemmaPileComponent.class);
            dilemmaPile.getCards().shuffle();
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
            eventSystem.fireEvent(new DilemmaPileShuffled(username), gameEntityProvider.getGameEntity());
        } else if (deckType.equals("drawDeck")) {
            PlayerDeckComponent deck = playerEntity.getComponent(PlayerDeckComponent.class);
            deck.getCards().shuffle();
            eventSystem.fireEvent(EntityUpdated.instance, playerEntity);
        }
    }

    @Override
    public void validate(JsonValue effect) {
        ValidateUtil.effectExpectedFields(effect,
                new String[]{"deck"},
                new String[]{"player", "playerMemory"});
        ValidateUtil.hasExactlyOneOf(effect, "player", "playerMemory");
        playerResolverSystem.validatePlayer(effect.getString("player", "currentPlayer"));
        String deck = effect.getString("deck");
        if (!deck.equals("dilemmaDeck") && !deck.equals("drawDeck"))
            throw new GdxRuntimeException("Unable to resolve deck type - " + deck);
    }
}
