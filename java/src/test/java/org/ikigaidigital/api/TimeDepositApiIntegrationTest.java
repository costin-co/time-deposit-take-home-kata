package org.ikigaidigital.api;

import org.ikigaidigital.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.persistence.repository.TimeDepositRepository;
import org.ikigaidigital.persistence.repository.WithdrawalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:time-deposit-api;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class TimeDepositApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TimeDepositRepository timeDepositRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @BeforeEach
    void clear_data() {
        withdrawalRepository.deleteAllInBatch();
        timeDepositRepository.deleteAllInBatch();
    }

    @Test
    void get_returns_deposits_with_their_withdrawals() throws Exception {
        TimeDepositEntity deposit = timeDepositRepository.saveAndFlush(
            new TimeDepositEntity("basic", 31, new BigDecimal("1000.00"))
        );
        withdrawalRepository.saveAndFlush(
            new WithdrawalEntity(deposit, new BigDecimal("125.50"), LocalDate.of(2026, 11, 13))
        );

        mockMvc.perform(get("/api/time-deposits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(deposit.getId()))
            .andExpect(jsonPath("$[0].planType").value("basic"))
            .andExpect(jsonPath("$[0].balance").value(1000.00))
            .andExpect(jsonPath("$[0].days").value(31))
            .andExpect(jsonPath("$[0].withdrawals", hasSize(1)))
            .andExpect(jsonPath("$[0].withdrawals[0].amount").value(125.50))
            .andExpect(jsonPath("$[0].withdrawals[0].date").value("2026-11-13"));
    }

    @Test
    void endpoints_handle_an_empty_database() throws Exception {
        mockMvc.perform(get("/api/time-deposits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(post("/api/time-deposits/update-balances"))
            .andExpect(status().isNoContent());
    }

    @Test
    void post_updates_and_persists_every_deposit_using_calculator_boundaries() throws Exception {
        var deposits = timeDepositRepository.saveAllAndFlush(List.of(
            new TimeDepositEntity("basic", 30, new BigDecimal("1000.00")),
            new TimeDepositEntity("basic", 31, new BigDecimal("1000.00")),
            new TimeDepositEntity("premium", 45, new BigDecimal("1000.00")),
            new TimeDepositEntity("premium", 46, new BigDecimal("1000.00")),
            new TimeDepositEntity("student", 365, new BigDecimal("1000.00")),
            new TimeDepositEntity("student", 366, new BigDecimal("1000.00"))
        ));
        WithdrawalEntity withdrawal = withdrawalRepository.saveAndFlush(
            new WithdrawalEntity(deposits.get(1), new BigDecimal("125.50"), LocalDate.of(2026, 11, 13))
        );

        mockMvc.perform(post("/api/time-deposits/update-balances"))
            .andExpect(status().isNoContent());

        Map<Integer, BigDecimal> expectedBalancesById = Map.of(
            deposits.get(0).getId(), new BigDecimal("1000.00"),
            deposits.get(1).getId(), new BigDecimal("1000.83"),
            deposits.get(2).getId(), new BigDecimal("1000.00"),
            deposits.get(3).getId(), new BigDecimal("1004.17"),
            deposits.get(4).getId(), new BigDecimal("1002.50"),
            deposits.get(5).getId(), new BigDecimal("1000.00")
        );
        List<TimeDepositEntity> persistedDeposits = timeDepositRepository.findAllById(
            expectedBalancesById.keySet()
        );
        Map<Integer, BigDecimal> persistedBalancesById = persistedDeposits.stream()
            .collect(Collectors.toMap(
                TimeDepositEntity::getId,
                TimeDepositEntity::getBalance
            ));

        assertThat(persistedBalancesById).isEqualTo(expectedBalancesById);
        assertThat(persistedDeposits)
            .allSatisfy(entity -> assertThat(entity.getBalance().scale()).isEqualTo(2));
        WithdrawalEntity persistedWithdrawal = withdrawalRepository.findById(withdrawal.getId()).orElseThrow();
        assertThat(persistedWithdrawal.getAmount()).isEqualByComparingTo("125.50");
        assertThat(persistedWithdrawal.getDate()).isEqualTo(LocalDate.of(2026, 11, 13));
        assertThat(persistedWithdrawal.getTimeDeposit().getId()).isEqualTo(deposits.get(1).getId());
    }

    @Test
    void repeated_post_calls_are_cumulative() throws Exception {
        TimeDepositEntity deposit = timeDepositRepository.saveAndFlush(
            new TimeDepositEntity("basic", 31, new BigDecimal("1000.00"))
        );

        mockMvc.perform(post("/api/time-deposits/update-balances"))
            .andExpect(status().isNoContent());
        assertThat(timeDepositRepository.findById(deposit.getId()).orElseThrow().getBalance())
            .isEqualByComparingTo("1000.83");

        mockMvc.perform(post("/api/time-deposits/update-balances"))
            .andExpect(status().isNoContent());
        assertThat(timeDepositRepository.findById(deposit.getId()).orElseThrow().getBalance())
            .isEqualByComparingTo("1001.66");
    }
}
