package org.ikigaidigital.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
public class WithdrawalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "timeDepositId",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_withdrawals_time_deposit")
    )
    private TimeDepositEntity timeDeposit;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    protected WithdrawalEntity() {
    }

    public WithdrawalEntity(TimeDepositEntity timeDeposit, BigDecimal amount, LocalDate date) {
        this.timeDeposit = timeDeposit;
        this.amount = amount;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public TimeDepositEntity getTimeDeposit() {
        return timeDeposit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}
