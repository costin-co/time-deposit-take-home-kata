package org.ikigaidigital;

public class PremiumInterestStrategy implements InterestCalculationStrategy {
    @Override
    public String planType() {
        return "premium";
    }

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        if (deposit.getDays() > 45) {
            return deposit.getBalance() * 0.05 / 12;
        }
        return 0;
    }
}
