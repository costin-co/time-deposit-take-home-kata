package org.ikigaidigital.persistence.repository;

import org.ikigaidigital.persistence.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, Integer> {
}
