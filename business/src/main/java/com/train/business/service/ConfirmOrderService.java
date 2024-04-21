package com.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.train.business.domain.DailyTrainTicket;
import com.train.business.enums.ConfirmOrderStatusEnum;
import com.train.business.enums.SeatTypeEnum;
import com.train.business.req.ConfirmOrderDoReq;
import com.train.business.req.ConfirmOrderTicketReq;
import com.train.common.context.LoginMemberContext;
import com.train.common.exception.BusinessException;
import com.train.common.exception.BusinessExceptionEnum;
import com.train.common.response.PageResp;
import com.train.common.util.SnowUtil;
import com.train.business.domain.ConfirmOrder;
import com.train.business.domain.ConfirmOrderExample;
import com.train.business.mapper.ConfirmOrderMapper;
import com.train.business.req.ConfirmOrderQueryReq;
import com.train.business.req.ConfirmOrderSaveReq;
import com.train.business.resp.ConfirmOrderQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;

    public void save(ConfirmOrderSaveReq req) {
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);

        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    public void doConfirm(ConfirmOrderDoReq dto) {
        LOG.info("===> start do confirm: {}", dto);

        // 保存确认订单表，状态初始
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(dto.getDate());
        confirmOrder.setTrainCode(dto.getTrainCode());
        confirmOrder.setStart(dto.getStart());
        confirmOrder.setEnd(dto.getEnd());
        confirmOrder.setDailyTrainTicketId(dto.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(dto.getTickets()));
        confirmOrderMapper.insert(confirmOrder);

        // 查询余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(dto.getDate(), dto.getTrainCode(), dto.getStart(), dto.getEnd());
        LOG.info("查询余票记录：{}", dailyTrainTicket);
        LOG.info("<=== end do confirm");

        // 预扣减库存
        reduceTicket(dto, dailyTrainTicket);

//        while (true) {
//            // 取确认订单表的记录，同日期车次，状态是I，分页处理，每次取N条
//            ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
//            confirmOrderExample.setOrderByClause("id asc");
//            ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
//            criteria.andDateEqualTo(dto.getDate())
//                    .andTrainCodeEqualTo(dto.getTrainCode())
//                    .andStatusEqualTo(ConfirmOrderStatusEnum.INIT.getCode());
//            PageHelper.startPage(1, 5);
//            List<ConfirmOrder> list = confirmOrderMapper.selectByExampleWithBLOBs(confirmOrderExample);
//
//            if (CollUtil.isEmpty(list)) {
//                LOG.info("没有需要处理的订单，结束循环");
//                break;
//            } else {
//                LOG.info("本次处理{}条订单", list.size());
//            }

//            // 一条一条的卖
//            list.forEach(confirmOrder -> {
//                try {
//                    sell(confirmOrder);
//                } catch (BusinessException e) {
//                    if (e.getE().equals(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR)) {
//                        LOG.info("本订单余票不足，继续售卖下一个订单");
//                        confirmOrder.setStatus(ConfirmOrderStatusEnum.EMPTY.getCode());
//                        updateStatus(confirmOrder);
//                    } else {
//                        throw e;
//                    }
//                }
//            });
//        }
    }

    private void  reduceTicket(ConfirmOrderDoReq dto, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : dto.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }

        }
    }
}
