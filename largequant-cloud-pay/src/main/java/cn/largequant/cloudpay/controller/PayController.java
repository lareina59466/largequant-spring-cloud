package cn.largequant.cloudpay.controller;

import cn.largequant.cloudpay.service.AliPayService;
import cn.largequant.cloudpay.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class PayController {

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private AliPayService aliPayService;

    @Value("${server.context.url}")
    private String server_url;


}
