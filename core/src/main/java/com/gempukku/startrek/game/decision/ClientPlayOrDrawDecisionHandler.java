package com.gempukku.startrek.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.AuthenticationHolderSystem;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.PlayerPositionSystem;
import com.gempukku.startrek.game.PlayerPublicStatsComponent;
import com.gempukku.startrek.game.ability.ClientCardAbilitySystem;
import com.gempukku.startrek.game.ability.HeadquarterRequirements;
import com.gempukku.startrek.game.card.CardFilteringSystem;

import java.util.function.Consumer;

public class ClientPlayOrDrawDecisionHandler extends BaseSystem implements DecisionHandler {
    private AuthenticationHolderSystem authenticationHolderSystem;
    private ClientDecisionSystem clientDecisionSystem;
    private PlayerPositionSystem playerPositionSystem;
    private StageSystem stageSystem;
    private CardFilteringSystem cardFilteringSystem;
    private CardLookupSystem cardLookupSystem;
    private ClientCardAbilitySystem clientCardAbilitySystem;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("playOrDrawDecision", this);
    }

    @Override
    public void processNewDecision(JsonValue decisionData) {
        Stage stage = stageSystem.getStage();

        Table table = new Table();
        table.setFillParent(true);

        VerticalGroup verticalGroup = new VerticalGroup();
        String username = authenticationHolderSystem.getUsername();
        Entity playerEntity = playerPositionSystem.getPlayerEntity(username);
        checkForDraw(table, verticalGroup, playerEntity);
        markPlayableCards(username, playerEntity);

        table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));

        stage.addActor(table);
    }

    private void markPlayableCards(String username, Entity playerEntity) {
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        int availableCounters = publicStats.getCounterCount();
        Entity playerHeadquarter = cardFilteringSystem.findFirstCardInPlay("missionType(Headquarters),owner(username(" + username + "))");
        HeadquarterRequirements headquarterRequirements = clientCardAbilitySystem.getClientCardAbility(playerHeadquarter, HeadquarterRequirements.class);
        cardFilteringSystem.forEachCardInHand(username, new Consumer<Entity>() {
            @Override
            public void accept(Entity cardEntity) {
                CardComponent card = cardEntity.getComponent(CardComponent.class);
                CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(card.getCardId());
                if (cardDefinition.getCost() <= availableCounters
                        && headquarterRequirements.getCardFilter().accepts(null, null, cardEntity)) {
                } else {
                }
            }
        });
    }

    private void checkForDraw(Table table, VerticalGroup verticalGroup, Entity playerEntity) {
        PlayerPublicStatsComponent publicStats = playerEntity.getComponent(PlayerPublicStatsComponent.class);
        if (publicStats.getDeckCount() > 0 && publicStats.getCounterCount() > 0) {
            TextButton drawCardButton = new TextButton("Draw a card", stageSystem.getSkin(), UISettings.mainButtonStyle);
            drawCardButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            table.remove();
                            ObjectMap<String, String> parameters = new ObjectMap<>();
                            parameters.put("action", "draw");
                            clientDecisionSystem.executeDecision(parameters);
                        }
                    });
            verticalGroup.addActor(drawCardButton);
        } else {
            TextButton passButton = new TextButton("Pass", stageSystem.getSkin(), UISettings.mainButtonStyle);
            passButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            table.remove();
                            ObjectMap<String, String> parameters = new ObjectMap<>();
                            parameters.put("action", "pass");
                            clientDecisionSystem.executeDecision(parameters);
                        }
                    });
            verticalGroup.addActor(passButton);
        }
    }

    @Override
    protected void processSystem() {

    }
}
