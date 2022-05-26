package com.marella.services;

import com.marella.models.Block;
import org.springframework.stereotype.Service;

@Service
public interface BlockService {
    Block findBlockById(Long blockId);
}
