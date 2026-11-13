package org.ikigaidigital.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.api.TimeDepositMapper;
import org.ikigaidigital.api.dto.TimeDepositResponse;
import org.ikigaidigital.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.persistence.repository.TimeDepositRepository;
import org.ikigaidigital.persistence.repository.WithdrawalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimeDepositService {
    private final TimeDepositRepository timeDepositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TimeDepositCalculator calculator;
    private final TimeDepositMapper mapper;

    public TimeDepositService(
        TimeDepositRepository timeDepositRepository,
        WithdrawalRepository withdrawalRepository,
        TimeDepositCalculator calculator,
        TimeDepositMapper mapper
    ) {
        this.timeDepositRepository = timeDepositRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.calculator = calculator;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<TimeDepositResponse> findAll() {
        List<TimeDepositEntity> deposits = timeDepositRepository.findAll();
        if (deposits.isEmpty()) {
            return List.of();
        }

        List<Integer> depositIds = deposits.stream()
            .map(TimeDepositEntity::getId)
            .toList();
        Map<Integer, List<WithdrawalEntity>> withdrawalsByDepositId =
            withdrawalRepository.findAllWithTimeDepositByTimeDepositIds(depositIds).stream()
                .collect(Collectors.groupingBy(withdrawal -> withdrawal.getTimeDeposit().getId()));

        return deposits.stream()
            .map(deposit -> mapper.toResponse(
                deposit,
                withdrawalsByDepositId.getOrDefault(deposit.getId(), Collections.emptyList())
            ))
            .toList();
    }

    @Transactional
    public void updateAllBalances() {
        List<TimeDepositEntity> deposits = timeDepositRepository.findAll();
        List<TimeDeposit> domainDeposits = deposits.stream()
            .map(mapper::toDomain)
            .toList();

        calculator.updateBalance(domainDeposits);

        for (int i = 0; i < deposits.size(); i++) {
            mapper.updateBalance(deposits.get(i), domainDeposits.get(i));
        }
        timeDepositRepository.saveAll(deposits);
    }
}
