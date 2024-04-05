package com.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.train.common.context.LoginMemberContext;
import com.train.common.response.MemberLoginResp;
import com.train.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MemberInterceptor implements HandlerInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("===> MemberInterceptor开始");
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            JSONObject loginMember = JwtUtil.getJSONObject(token);
            MemberLoginResp member = JSONUtil.toBean(loginMember, MemberLoginResp.class);
            LOG.info("get member: {}", member);
            LoginMemberContext.setMember(member);
        }

        LOG.info("===> MemberInterceptor结束");
        return true;
    }
}
