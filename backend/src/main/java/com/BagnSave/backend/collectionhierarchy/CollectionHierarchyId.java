package com.BagnSave.backend.collectionhierarchy;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CollectionHierarchyId implements Serializable {
    
    private Integer parentId;
    private Integer childId;
}
