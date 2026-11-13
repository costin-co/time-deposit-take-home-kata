package org.ikigaidigital;

public class BasicInterestStrategy implements InterestCalculationStrategy {
    @Override
    public String planType() {
        return "basic";
    }

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        return deposit.getBalance() * 0.01 / 12;
    }
}
