package org.scoula.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.dto.AccountRegisterDTO;
import org.scoula.account.dto.AccountUpdateDTO;
import org.scoula.account.dto.AccountViewDTO;
import org.scoula.account.dto.BankDTO;
import org.scoula.account.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Log4j2
public class AccountController {

    private final AccountService accountService;

    // 은행 목록 조회
    @GetMapping("/banks")
    public ResponseEntity<List<BankDTO>> getBankList() {
        return ResponseEntity.ok(accountService.getBankList());
    }

    // 계좌 정보, 잔액 조회
    @GetMapping("/{userId}")
    public ResponseEntity<AccountViewDTO> getAccountByUserId(@PathVariable int userId) {
        try {
            AccountViewDTO accountViewDTO = accountService.getAccountByUserId(userId);
            if (accountViewDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(accountViewDTO);
        } catch (Exception e) {
            log.error("계좌 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 계좌 등록
    @PostMapping
    public ResponseEntity<String> registerAccount(@RequestBody AccountRegisterDTO accountRegisterDTO) {
        return handleRequest(() -> {
            accountService.registerAccount(accountRegisterDTO);
            return "계좌 등록 성공";
        }, HttpStatus.CREATED);
    }

    // 계좌 수정
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateAccount(@PathVariable int userId, @RequestBody AccountUpdateDTO accountUpdateDTO) {
        return handleRequest(() -> {
            accountUpdateDTO.setUserId(userId);
            accountService.updateAccount(accountUpdateDTO);
            return "계좌 수정 성공";
        }, HttpStatus.OK);
    }

    // 예외 처리용
    private <T> ResponseEntity<T> handleRequest(SupplierWithException<T> supplier, HttpStatus successStatus) {
        try {
            T result = supplier.get();
            return new ResponseEntity<>(result, successStatus);
        } catch (Exception e) {
            log.error("요청 처리 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
