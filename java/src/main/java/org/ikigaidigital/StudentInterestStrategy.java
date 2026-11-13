package org.ikigaidigital;

public class StudentInterestStrategy implements InterestCalculationStrategy {
    @Override
    public String planType() {
        return "student";
    }

    @Override
    public double calculateInterest(TimeDeposit deposit) {
        if (deposit.getDays() < 366) {
            return deposit.getBalance() * 0.03 / 12;
        }
        return 0;
    }
}
