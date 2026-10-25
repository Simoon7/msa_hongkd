// member/controller/MemberController.java
package com.example.msa.member.controller;

import com.example.msa.member.dto.*;
import com.example.msa.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody MemberSaveReqDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.register(dto));
    }

    @PostMapping("/doLogin")
    public ResponseEntity<MemberLoginResDTO> login(@RequestBody MemberLoginReqDTO dto) {
        return ResponseEntity.ok(memberService.login(dto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refresh(@RequestBody MemberRefreshReqDTO dto) {
        return ResponseEntity.ok(memberService.reissueAccessToken(dto));
    }
}