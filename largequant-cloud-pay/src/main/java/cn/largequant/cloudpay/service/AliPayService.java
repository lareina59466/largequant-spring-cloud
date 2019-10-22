package cn.largequant.cloudpay.service;

import cn.largequant.cloudcommon.entity.seckill.Product;

import java.util.Map;

public interface AliPayService {

    //支付宝预下单
    String aliPay(Product product);

    //支付宝退款
    String aliRefund(Product product,String refundReason);

    //支付宝关闭订单
    String aliCloseOrder(Product product);

    //支付宝下载账单
    String aliDownloadBill(String billDate,String billType);

    //支付宝H5支付
    String aliH5Pay(Product product);

    //支付宝网站支付
    String aliPcPay(Product product);

    //支付宝APP支付
    String aliAppPay(Product product);

    //验证签名1
    boolean rsaCheckV1(Map<String,String> params);

    //验证签名2
    boolean rsaCheckV2(Map<String,String> params);
}
