package org.scoula.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.account.dto.AccountRegisterDTO;
import org.scoula.account.dto.AccountUpdateDTO;
import org.scoula.account.dto.AccountViewDTO;
import org.scoula.account.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Log4j2
public class AccountController {

    private final AccountService accountService;

    // 계좌 등록
    @PostMapping
    public ResponseEntity<String> registerAccount(@RequestBody AccountRegisterDTO accountRegisterDTO) {
        log.info("계좌 등록 요청: {}", accountRegisterDTO);
        try {
            accountService.registerAccount(accountRegisterDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("계좌 등록 성공");
        } catch (Exception e) {
            log.error("계좌 등록 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("계좌 등록 실패: " + e.getMessage());
        }
    }

    // 계좌 정보, 잔액 조회
    @GetMapping("/{userId}")
    public ResponseEntity<AccountViewDTO> getAccountByUserId(@PathVariable int userId) {
        log.info("계좌 조회 요청 - userId: {}", userId);
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

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateAccount(@PathVariable int userId, @RequestBody AccountUpdateDTO accountUpdateDTO) {
        log.info("계좌 수정 요청 - userId: {}, DTO: {}", userId, accountUpdateDTO);
        try {
            accountUpdateDTO.setUserId(userId);
            accountService.updateAccount(accountUpdateDTO);
            return ResponseEntity.ok("계좌 수정 성공");
        } catch (Exception e) {
            log.error("계좌 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("계좌 수정 실패: " + e.getMessage());
        }
    }
}
