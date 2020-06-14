package com.healthy.security.core.social.weixinmp.api.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.healthy.security.core.social.weixinmp.api.WeixinMp;
import com.healthy.security.core.social.weixinmp.entity.WeixinMpUserInfo;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Weixin公众号 API调用模板， scope为Request的Spring bean, 根据当前用户的accessToken创建。
 */
public class WeixinMpImpl extends AbstractOAuth2ApiBinding implements WeixinMp {

    /**
     * 获取用户信息的url
     */
    private static final String URL_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?openid=";

    /**
     * accessToken构造器
     * @param accessToken
     */
    public WeixinMpImpl(String accessToken) {
        // TokenStrategy.ACCESS_TOKEN_PARAMETER策略会携带access_token参数作为查询条件
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
    }

    /**
     * 默认注册的StringHttpMessageConverter字符集为ISO-8859-1，而微信返回的是UTF-8的，所以覆盖了原来的方法。
     */
    @Override
    protected List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
        messageConverters.remove(0);
        messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return messageConverters;
    }

    /**
     * 获取微信用户信息。
     */
    @Override
    public WeixinMpUserInfo getUserInfo(String openId) {
        String url = URL_GET_USER_INFO + openId;
        String response = getRestTemplate().getForObject(url, String.class);
        if (StrUtil.containsAny(response, "errcode")) {
            return null;
        }
        WeixinMpUserInfo userInfo = JSONUtil.toBean(response, WeixinMpUserInfo.class);
        return userInfo;
    }

}
