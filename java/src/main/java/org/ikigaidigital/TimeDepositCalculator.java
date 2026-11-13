package org.ikigaidigital;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TimeDepositCalculator {
    private final List<InterestCalculationStrategy> strategies = List.of(
        new StudentInterestStrategy(),
        new PremiumInterestStrategy(),
        new BasicInterestStrategy()
    );

    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit deposit : xs) {
            double interest = 0;

            if (deposit.getDays() > 30) {
                for (InterestCalculationStrategy strategy : strategies) {
                    if (deposit.getPlanType().equals(strategy.planType())) {
                        interest += strategy.calculateInterest(deposit);
                        break;
                    }
                }
            }

            double a2d = deposit.getBalance()
                    + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
            deposit.setBalance(a2d);
        }
    }
}
