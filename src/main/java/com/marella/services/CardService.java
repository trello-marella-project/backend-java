package com.marella.services;

import com.marella.models.Card;
import org.springframework.stereotype.Service;

@Service
public interface CardService {
    Card findCardById(Long cardId);
}
