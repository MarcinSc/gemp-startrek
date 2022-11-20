package com.gempukku.startrek.game.render;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.Entity;
import com.artemis.utils.IntBag;
import com.gempukku.libgdx.lib.artemis.animation.AnimationDirectorSystem;
import com.gempukku.libgdx.lib.artemis.animation.animator.WaitAnimator;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.libgdx.lib.graph.artemis.text.TextBlock;
import com.gempukku.libgdx.lib.graph.artemis.text.TextComponent;
import com.gempukku.libgdx.lib.graph.artemis.text.TextSystem;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.common.ServerStateChanged;
import com.gempukku.startrek.game.CardComponent;
import com.gempukku.startrek.game.template.CardTemplates;
import com.gempukku.startrek.game.zone.ObjectOnStackComponent;

public class StackTextHighlightingSystem extends BaseEntitySystem {
    private CardLookupSystem cardLookupSystem;
    private CardRenderingSystem cardRenderingSystem;
    private TextSystem textSystem;
    private AnimationDirectorSystem animationDirectorSystem;

    private boolean stateChanged;

    public StackTextHighlightingSystem() {
        super(Aspect.all(ObjectOnStackComponent.class));
    }

    @EventListener
    public void serverStateChanged(ServerStateChanged serverStateChanged, Entity entity) {
        stateChanged = true;
    }

    @Override
    protected void processSystem() {
        if (stateChanged) {
            stateChanged = false;

            IntBag entities = getSubscription().getEntities();
            for (int i = 0; i < entities.size(); i++) {
                Entity entityOnStack = world.getEntity(entities.get(i));
                ObjectOnStackComponent objectOnStack = entityOnStack.getComponent(ObjectOnStackComponent.class);
                int abilityIndex = objectOnStack.getAbilityIndex();
                if (abilityIndex >= 0) {
                    CardComponent card = entityOnStack.getComponent(CardComponent.class);
                    if (card != null) {
                        Entity renderedCard = cardRenderingSystem.getCommonZones().findRenderedCard(entityOnStack);

                        CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(card.getCardId());
                        String cardText = CardTemplates.createStepCardText(cardDefinition, abilityIndex, objectOnStack.getEffectStep());

                        int cardTextBlockIndex = 3;

                        // Text
                        TextComponent text = renderedCard.getComponent(TextComponent.class);
                        TextBlock textBlock = text.getTextBlocks().get(cardTextBlockIndex);
                        if (!cardText.equals(textBlock.getText())) {
                            textBlock.setText(cardText);
                            textSystem.updateText(renderedCard.getId(), cardTextBlockIndex);
                            animationDirectorSystem.enqueueAnimator("Server", new WaitAnimator(3f));
                        }
                    }
                }
            }
        }
    }
}
