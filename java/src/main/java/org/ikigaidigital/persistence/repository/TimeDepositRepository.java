package org.ikigaidigital.persistence.repository;

import org.ikigaidigital.persistence.entity.TimeDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeDepositRepository extends JpaRepository<TimeDepositEntity, Integer> {
}
