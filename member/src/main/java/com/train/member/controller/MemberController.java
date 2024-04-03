package com.train.member.controller;

import com.train.common.response.CommonResp;
import com.train.member.request.MemberLoginReq;
import com.train.member.request.MemberRegisterReq;
import com.train.member.request.MemberSendCodeReq;
import com.train.member.response.MemberLoginResp;
import com.train.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("register")
    public CommonResp<Long> register(@Valid @RequestBody MemberRegisterReq req) {
        long id = memberService.register(req.getMobile());

        return new CommonResp<>(id);
    }

    @PostMapping("/send-code")
    public CommonResp<String> sendCode(@Valid @RequestBody MemberSendCodeReq req) {
        String code = memberService.sendCode(req);
        return new CommonResp<String>(code);
    }

    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq req) {
        MemberLoginResp resp = memberService.login(req);
        return new CommonResp<>(resp);
    }
}
