package com.gempukku.startrek.server.service;

import com.gempukku.startrek.card.CardData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CardDataService {
    @Autowired
    private DummyGdx dummyGdx;

    private CardData cardData;

    @PostConstruct
    public void initializeCards() {
        cardData = new CardData();
        cardData.initializeCards();
    }

    public CardData getCardData() {
        return cardData;
    }
}
