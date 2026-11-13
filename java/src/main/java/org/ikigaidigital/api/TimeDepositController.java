package org.ikigaidigital.api;

import org.ikigaidigital.api.dto.TimeDepositResponse;
import org.ikigaidigital.service.TimeDepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/time-deposits")
public class TimeDepositController {
    private final TimeDepositService timeDepositService;

    public TimeDepositController(TimeDepositService timeDepositService) {
        this.timeDepositService = timeDepositService;
    }

    @GetMapping
    public List<TimeDepositResponse> findAll() {
        return timeDepositService.findAll();
    }

    @PostMapping("/update-balances")
    public ResponseEntity<Void> updateBalances() {
        timeDepositService.updateAllBalances();
        return ResponseEntity.noContent().build();
    }
}
