package com.BagnSave.backend.collectionmember;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.BagnSave.backend.collectionmember.dto.CollectionMemberDTO;

@Service
public class CollectionMemberService {
    
    private final CollectionMemberRepository collectionMemberRepository;

    public CollectionMemberService(CollectionMemberRepository collectionMemberRepository) {
        this.collectionMemberRepository = collectionMemberRepository;
    }

    public Page<CollectionMemberDTO> getCollectionMembers(Pageable pageable) {
        return collectionMemberRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private CollectionMemberDTO toDTO(CollectionMember collectionMember) {
        return new CollectionMemberDTO(
            collectionMember.getId().getCollectionId(),
            collectionMember.getId().getProductId()
        );
    }
}
