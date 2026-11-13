package org.ikigaidigital;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TimeDepositCalculatorTest {

    @Test
    public void basic_plan_applies_interest_after_30_days() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(1, "basic", 1000.0, 31)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1000.83);
    }

    @Test
    public void basic_plan_has_no_interest_at_day_0() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(1, "basic", 1000.0, 0)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1000.0);
    }

    @Test
    public void basic_plan_has_no_interest_at_30_days() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(1, "basic", 1000.0, 30)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1000.0);
    }

    @Test
    public void student_plan_has_no_interest_at_30_days() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(2, "student", 1000.0, 30)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1000.0);
    }

    @Test
    public void student_plan_applies_interest_at_31_days() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(2, "student", 1000.0, 31)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1002.50);
    }

    @Test
    public void student_plan_applies_interest_before_1_year() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(2, "student", 1000.0, 365)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1002.50);
    }

    @Test
    public void student_plan_has_no_interest_at_or_after_1_year() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(2, "student", 1000.0, 366)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1000.0);
    }

    @Test
    public void premium_plan_applies_interest_after_45_days() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(3, "premium", 1000.0, 46)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1004.17);
    }

    @Test
    public void premium_plan_has_no_interest_at_45_days() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(3, "premium", 1000.0, 45)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1000.0);
    }

    @Test
    public void unknown_plan_type_has_no_interest() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(4, "legacy", 1000.0, 90)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1000.0);
    }

    @Test
    public void null_plan_type_throws_when_days_are_greater_than_30() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(4, null, 1000.0, 31)
        ));

        assertThatThrownBy(() -> calc.updateBalance(plans))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void null_plan_type_does_not_throw_when_days_are_at_most_30() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(4, null, 1000.0, 30)
        ));

        assertThatCode(() -> calc.updateBalance(plans)).doesNotThrowAnyException();
        assertThat(plans.get(0).getBalance()).isEqualTo(1000.0);
    }

    @Test
    public void plan_type_is_case_sensitive() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(4, "Basic", 1000.0, 90)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(1000.0);
    }

    @Test
    public void rounding_uses_half_up_to_two_decimal_places() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(5, "basic", 6.0, 31)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(6.01);
    }

    @Test
    public void basic_plan_rounding_uses_exact_binary_double_value() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(5, "basic", 174.0, 31)
        ));

        calc.updateBalance(plans);

        assertThat(plans.get(0).getBalance()).isEqualTo(174.14);
    }

    @Test
    public void empty_list_is_a_noop() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = Collections.emptyList();

        assertThatCode(() -> calc.updateBalance(plans)).doesNotThrowAnyException();
        assertThat(plans).isEmpty();
    }

    @Test
    public void multiple_deposits_are_updated_independently() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(10, "basic", 1000.0, 31),
            new TimeDeposit(11, "student", 2000.0, 365),
            new TimeDeposit(12, "premium", 3000.0, 46),
            new TimeDeposit(13, "legacy", 4000.0, 90)
        ));

        calc.updateBalance(plans);

        assertThat(plans).extracting(TimeDeposit::getBalance)
            .containsExactly(1000.83, 2005.00, 3012.50, 4000.00);
    }

    @Test
    public void repeated_calls_accumulate_interest_on_the_updated_balance() {
        TimeDepositCalculator calc = new TimeDepositCalculator();
        List<TimeDeposit> plans = new ArrayList<>(List.of(
            new TimeDeposit(20, "basic", 1000.0, 31)
        ));

        calc.updateBalance(plans);
        assertThat(plans.get(0).getBalance()).isEqualTo(1000.83);

        calc.updateBalance(plans);
        assertThat(plans.get(0).getBalance()).isEqualTo(1001.6600000000001);
    }
}
