package com.train.business.controller;

import com.train.business.req.TrainQueryReq;
import com.train.business.resp.TrainQueryResp;
import com.train.business.service.TrainSeatService;
import com.train.business.service.TrainService;
import com.train.common.response.CommonResp;
import com.train.common.response.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {

    @Resource
    private TrainService trainService;

    @Autowired
    private TrainSeatService trainSeatService;

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryAll() {
        List<TrainQueryResp> list = trainService.queryAll();
        return new CommonResp<>(list);
    }
}
