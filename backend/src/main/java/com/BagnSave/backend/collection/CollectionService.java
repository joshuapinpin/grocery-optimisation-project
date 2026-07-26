package com.BagnSave.backend.collection;

import com.BagnSave.backend.collection.dto.CollectionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CollectionService {
    
    private final CollectionRepository collectionRepository;

    public CollectionService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public Page<CollectionDTO> getCollections(Pageable pageable) {
        return collectionRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private CollectionDTO toDTO(Collection collection) {
        return new CollectionDTO(
            collection.getId(),
            collection.getName(),
            collection.getIsComparable()
        );
    }

}
