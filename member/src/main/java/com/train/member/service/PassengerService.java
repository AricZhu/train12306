package com.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.train.common.util.SnowUtil;
import com.train.member.domain.Passenger;
import com.train.member.mapper.PassengerMapper;
import com.train.member.request.PassengerSaveReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {
    @Autowired
    private PassengerMapper passengerMapper;

    public void savePassenger(PassengerSaveReq req) {
        Passenger passenger = new Passenger();
        BeanUtil.copyProperties(req, passenger);

        DateTime now = DateTime.now();
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);

        passengerMapper.insert(passenger);
    }
}
