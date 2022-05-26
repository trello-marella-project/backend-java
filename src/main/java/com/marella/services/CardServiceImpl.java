package com.marella.services;

import com.marella.models.Card;
import com.marella.repositories.CardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CardServiceImpl implements CardService {
    private CardRepository cardRepository;

    @Override
    public Card findCardById(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(
                () -> new IllegalArgumentException(String.format("card with id: %d does not exist", cardId))
        );
    }
}
