package com.marella.services;

import com.marella.models.Block;
import com.marella.repositories.BlockRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BlockServiceImpl implements BlockService {
    private BlockRepository blockRepository;

    @Override
    public Block findBlockById(Long blockId) {
        return blockRepository.findById(blockId).orElseThrow(
                () -> new IllegalArgumentException(String.format("block with id: %d does not exist", blockId))
        );
    }
}
