package org.ikigaidigital.persistence.repository;

import org.ikigaidigital.persistence.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, Integer> {
    @Query("""
        select withdrawal
        from WithdrawalEntity withdrawal
        join fetch withdrawal.timeDeposit
        where withdrawal.timeDeposit.id in :depositIds
        """)
    List<WithdrawalEntity> findAllWithTimeDepositByTimeDepositIds(
        @Param("depositIds") List<Integer> depositIds
    );
}
