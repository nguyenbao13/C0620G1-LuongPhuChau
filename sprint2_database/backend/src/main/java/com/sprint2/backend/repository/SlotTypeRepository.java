package com.sprint2.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sprint2.backend.entity.SlotType;

@Repository
public interface SlotTypeRepository extends JpaRepository<SlotType, Long> {
}
