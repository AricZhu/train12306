package com.train.member.controller;

import com.train.common.context.LoginMemberContext;
import com.train.common.response.CommonResp;
import com.train.common.response.PageResp;
import com.train.member.request.PassengerQueryReq;
import com.train.member.request.PassengerSaveReq;
import com.train.member.response.PassengerQueryResp;
import com.train.member.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PassengerController {
    @Autowired
    private PassengerService passengerService;

    @PostMapping("/save-passenger")
    public CommonResp<Object> savePassenger(@Valid @RequestBody PassengerSaveReq req) {
        passengerService.save(req);
        return new CommonResp<>();
    }

    @GetMapping("/passenger/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryPassengerList(@Valid PassengerQueryReq req) {
        req.setMemberId(LoginMemberContext.getId());
        PageResp<PassengerQueryResp> list = passengerService.queryList(req);

        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete-passenger/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        passengerService.delete(id);
        return new CommonResp<>();
    }
}
