package com.gempukku.startrek.game.decision;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.libgdx.lib.artemis.camera.CameraSystem;
import com.gempukku.libgdx.lib.artemis.hierarchy.HierarchySystem;
import com.gempukku.libgdx.lib.artemis.input.UserInputStateComponent;
import com.gempukku.libgdx.lib.artemis.picking.ShapePickingSystem;
import com.gempukku.libgdx.lib.artemis.spawn.SpawnSystem;
import com.gempukku.libgdx.lib.artemis.transform.TransformSystem;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteComponent;
import com.gempukku.libgdx.lib.graph.artemis.sprite.SpriteDefinition;
import com.gempukku.libgdx.lib.graph.artemis.text.TextBlock;
import com.gempukku.libgdx.lib.graph.artemis.text.TextComponent;
import com.gempukku.libgdx.lib.graph.artemis.ui.StageSystem;
import com.gempukku.startrek.LazyEntityUtil;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.StringUtils;
import com.gempukku.startrek.common.UISettings;
import com.gempukku.startrek.game.decision.ui.CardContainerSettings;
import com.gempukku.startrek.game.decision.ui.CardManipulation;
import com.gempukku.startrek.game.template.CardTemplates;
import com.gempukku.startrek.game.zone.CardZone;

public class ClientSetupDilemmasDecisionHandler extends BaseSystem implements DecisionHandler {
    private ClientDecisionSystem clientDecisionSystem;
    private StageSystem stageSystem;
    private CameraSystem cameraSystem;
    private SpawnSystem spawnSystem;
    private ShapePickingSystem shapePickingSystem;
    private TransformSystem transformSystem;
    private HierarchySystem hierarchySystem;
    private CardLookupSystem cardLookupSystem;

    private Table table;
    private TextButton finishedButton;

    private CardManipulation cardManipulation;
    private Entity userInputStateEntity;

    private static final float cardScale = 0.3f;

    @Override
    protected void initialize() {
        clientDecisionSystem.registerDecisionHandler("setupDilemmas", this);
    }

    private void initializeForDecisions() {
        userInputStateEntity = LazyEntityUtil.findEntityWithComponent(world, UserInputStateComponent.class);

        table = new Table();
        table.setFillParent(true);

        VerticalGroup verticalGroup = new VerticalGroup();

        finishedButton = new TextButton("Finished", stageSystem.getSkin(), UISettings.mainButtonStyle) {
            @Override
            public float getPrefWidth() {
                return 200;
            }
        };
        finishedButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        finishedSetup();
                    }
                });
        verticalGroup.addActor(finishedButton);

        table.add(verticalGroup).expand().bottom().right().pad(Value.percentHeight(0.02f, table));
    }

    @Override
    public void processNewDecision(ObjectMap<String, String> decisionData) {
        if (table == null) {
            initializeForDecisions();
        }
        Stage stage = stageSystem.getStage();

        String[] cardIds = StringUtils.split(decisionData.get("dilemmaCardIds"));
        int costCount = Integer.parseInt(decisionData.get("costCount"));

        cardManipulation = new CardManipulation(world, cameraSystem, spawnSystem, shapePickingSystem,
                transformSystem, hierarchySystem,
                userInputStateEntity, cardScale,
                "ui", "pickTarget", "dropTarget");

        Array<Entity> cards = new Array<>();
        for (String cardId : cardIds) {
            CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(cardId);
            Entity renderedCard = CardTemplates.createRenderedCard(cardDefinition, CardZone.Hand, spawnSystem);
            TextComponent text = renderedCard.getComponent(TextComponent.class);
            for (TextBlock textBlock : text.getTextBlocks()) {
                textBlock.setSpriteBatchName("uiDitherText");
            }
            SpriteComponent sprite = renderedCard.getComponent(SpriteComponent.class);
            for (SpriteDefinition spriteSprite : sprite.getSprites()) {
                spriteSprite.setSpriteBatchName("uiDitherTexture");
            }

            cards.add(renderedCard);
        }

        spawnSystem.spawnEntity("game/ui/setupDilemmasUI.template");

        cardManipulation.addCardContainer(
                new CardContainerSettings(0.715257f, 1f, cardScale, 0.3f, 0.65f,
                        0.10f,
                        new Vector2(-0.4f, 0.05f), true), new Array<>());
        cardManipulation.addCardContainer(
                new CardContainerSettings(0.715257f, 1f, cardScale, 1.1f, 0.65f,
                        0.15f,
                        new Vector2(0.175f, 0.05f), false), cards);

        stage.addActor(table);
    }

    private void finishedSetup() {
        ObjectMap<String, String> parameters = new ObjectMap<>();
        parameters.put("action", "pass");
        executeCleanup();
        clientDecisionSystem.executeDecision(parameters);
        cardManipulation.dispose();
        cardManipulation = null;
    }

    private void executeCleanup() {
        table.remove();
    }

    @Override
    protected void processSystem() {
        if (cardManipulation != null) {
            cardManipulation.update();
        }
    }
}
