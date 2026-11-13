package org.ikigaidigital;

public interface InterestCalculationStrategy {
    String planType();

    double calculateInterest(TimeDeposit deposit);
}
