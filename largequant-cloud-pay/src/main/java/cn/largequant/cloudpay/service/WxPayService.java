package cn.largequant.cloudpay.service;

import cn.largequant.cloudcommon.entity.seckill.Product;

public interface WxPayService {

    //微信Native支付下单(模式二)
    String wxNativePay2(Product product);

    //微信Native支付下单(模式一)
    void wxNativePay1(Product product);

    //微信JSAPI支付(微信公众号支付)
    String wxJSAPIPay(Product product);

    //微信H5支付
    String wxH5Pay(Product product);

    //微信支付退款
    String wxRefund(Product product);

    //微信关闭订单
    String wxCloseOrder(Product product);

    //微信下载账单
    void wxDownloadBill();

    //微信查询订单
    String wxOrderQuery(Product product);
}
