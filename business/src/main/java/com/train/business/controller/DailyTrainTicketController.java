package com.train.business.controller;

import com.train.business.req.DailyTrainTicketQueryReq;
import com.train.business.req.DailyTrainTicketSaveReq;
import com.train.business.resp.DailyTrainTicketQueryResp;
import com.train.business.service.DailyTrainTicketService;
import com.train.common.response.CommonResp;
import com.train.common.response.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
        PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
        return new CommonResp<>(list);
    }

}
