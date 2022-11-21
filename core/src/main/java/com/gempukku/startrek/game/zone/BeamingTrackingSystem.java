package com.gempukku.startrek.game.zone;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.lib.artemis.audio.AudioSystem;
import com.gempukku.libgdx.lib.artemis.event.EventListener;
import com.gempukku.startrek.common.IncomingUpdatesProcessor;

public class BeamingTrackingSystem extends BaseSystem {
    private IncomingUpdatesProcessor incomingUpdatesProcessor;
    private AudioSystem audioSystem;

    private Array<CardsBeamed> eventsToProcess = new Array<>();

    @EventListener
    public void cardsBeamed(CardsBeamed cardsBeamed, Entity entity) {
        eventsToProcess.add(cardsBeamed);
        audioSystem.playSound("fx", "transporter");
    }

    @Override
    protected void processSystem() {
        for (CardsBeamed cardsBeamed : eventsToProcess) {
            String fromShipId = cardsBeamed.getFromShipId();
            String toShipId = cardsBeamed.getToShipId();
            Array<String> entityIds = cardsBeamed.getEntityIds();
        }
        eventsToProcess.clear();
    }
}
