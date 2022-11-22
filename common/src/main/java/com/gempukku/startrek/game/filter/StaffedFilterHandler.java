package com.gempukku.startrek.game.filter;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.startrek.card.Affiliation;
import com.gempukku.startrek.card.CardDefinition;
import com.gempukku.startrek.card.CardIcon;
import com.gempukku.startrek.card.CardLookupSystem;
import com.gempukku.startrek.game.Memory;
import com.gempukku.startrek.game.ValidateUtil;
import com.gempukku.startrek.game.card.CardFilteringSystem;

public class StaffedFilterHandler extends CardFilterSystem {
    private CardFilteringSystem cardFilteringSystem;
    private CardLookupSystem cardLookupSystem;

    public StaffedFilterHandler() {
        super("staffed");
    }

    @Override
    public CardFilter resolveFilter(Array<String> parameters) {
        return new CardFilter() {
            @Override
            public boolean accepts(Entity sourceEntity, Memory memory, Entity cardEntity) {
                Array<Entity> unstoppedPersonnelOnBoard = cardFilteringSystem.findAllInPlay(cardEntity, memory,
                        "type(Personnel),unstopped,attachedTo(self)");
                int cmdCount = 0;
                int stfCount = 0;
                for (Entity personnel : unstoppedPersonnelOnBoard) {
                    CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(personnel);
                    if (hasIcon(cardDefinition, CardIcon.Cmd))
                        cmdCount++;
                    else if (hasIcon(cardDefinition, CardIcon.Stf))
                        stfCount++;
                }

                int cmdRequired = 0;
                int stfRequired = 0;
                CardDefinition shipDefinition = cardLookupSystem.getCardDefinition(cardEntity);
                for (CardIcon staffingRequirement : shipDefinition.getStaffingRequirements()) {
                    if (staffingRequirement == CardIcon.Cmd)
                        cmdRequired++;
                    if (staffingRequirement == CardIcon.Stf)
                        stfRequired++;
                }

                // Can't staff if number of commanders is less than required, or if total staff
                // (including commanders) is less than total staff required (including commanders)
                if (cmdCount < cmdRequired || cmdCount + stfCount < cmdRequired + stfRequired)
                    return false;

                Affiliation shipAffiliation = shipDefinition.getAffiliation();
                for (Entity personnel : unstoppedPersonnelOnBoard) {
                    CardDefinition cardDefinition = cardLookupSystem.getCardDefinition(personnel);
                    if (cardDefinition.getAffiliation() == shipAffiliation)
                        return true;
                }

                return false;
            }
        };
    }

    private boolean hasIcon(CardDefinition cardDefinition, CardIcon icon) {
        for (CardIcon cardDefinitionIcon : cardDefinition.getIcons()) {
            if (cardDefinitionIcon == icon)
                return true;
        }
        return false;
    }

    @Override
    public void validate(Array<String> parameters) {
        ValidateUtil.exactly(parameters, 0);
    }
}
