package com.BagnSave.backend.collectionhierarchy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.BagnSave.backend.collectionhierarchy.dto.CollectionHierarchyDTO;

@Service
public class CollectionHierarchyService {
    
    private final CollectionHierarchyRepository collectionHierarchyRepository;

    public CollectionHierarchyService(CollectionHierarchyRepository collectionHierarchyRepository) {
        this.collectionHierarchyRepository = collectionHierarchyRepository;
    }

    public Page<CollectionHierarchyDTO> getCollectionHierarchy(Pageable pageable) {
        return collectionHierarchyRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private CollectionHierarchyDTO toDTO(CollectionHierarchy collectionHierarchy) {
        return new CollectionHierarchyDTO(
            collectionHierarchy.getId().getParentId(),
            collectionHierarchy.getId().getChildId()
        );
    }
}
