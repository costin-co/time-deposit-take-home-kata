package org.ikigaidigital.persistence;

import org.ikigaidigital.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.persistence.repository.TimeDepositRepository;
import org.ikigaidigital.persistence.repository.WithdrawalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PersistenceSchemaTest {
    @Autowired
    private TimeDepositRepository timeDepositRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void persists_entities_using_required_table_and_column_names() {
        TimeDepositEntity deposit = timeDepositRepository.saveAndFlush(
            new TimeDepositEntity("basic", 31, new BigDecimal("1000.00"))
        );
        LocalDate withdrawalDate = LocalDate.of(2026, 11, 13);
        WithdrawalEntity withdrawal = withdrawalRepository.saveAndFlush(
            new WithdrawalEntity(deposit, new BigDecimal("125.50"), withdrawalDate)
        );

        Map<String, Object> depositRow = jdbcTemplate.queryForMap(
                """
                SELECT "planType", "days", "balance"
                FROM "timeDeposits"
                WHERE "id" = ?
                """,
                deposit.getId()
        );

        Map<String, Object> withdrawalRow = jdbcTemplate.queryForMap(
                """
                SELECT "timeDepositId", "amount", "date"
                FROM "withdrawals"
                WHERE "id" = ?
                """,
                withdrawal.getId()
        );

        assertThat(depositRow)
            .containsEntry("planType", "basic")
            .containsEntry("days", 31)
            .containsEntry("balance", new BigDecimal("1000.00"));
        assertThat(withdrawalRow)
            .containsEntry("timeDepositId", deposit.getId())
            .containsEntry("amount", new BigDecimal("125.50"));
        assertThat(withdrawalRow.get("date")).isInstanceOf(java.sql.Date.class);
        WithdrawalEntity persistedWithdrawal = withdrawalRepository.findById(withdrawal.getId()).orElseThrow();
        assertThat(persistedWithdrawal.getDate()).isEqualTo(withdrawalDate);
        assertThat(persistedWithdrawal.getTimeDeposit().getId()).isEqualTo(deposit.getId());
    }
}
