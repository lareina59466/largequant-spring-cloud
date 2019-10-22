package cn.largequant.cloudpay.service.impl;

import cn.largequant.cloudcommon.constant.Constants;
import cn.largequant.cloudcommon.entity.seckill.Product;
import cn.largequant.cloudcommon.util.CommonUtil;
import cn.largequant.cloudpay.config.AliPayConfig;
import cn.largequant.cloudpay.service.AliPayService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.utils.ZxingUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {

    @Value("${alipay.notify.url}")
    private String notify_url;

    //支付宝预下单
    @Override
    public String aliPay(Product product) {
        log.info("订单号：{}生成支付宝支付码", product.getOutTradeNo());
        String message = Constants.SUCCESS;
        //二维码存放路径
        String imgPath = Constants.QRCODE_PATH + Constants.SF_FILE_SEPARATOR + product.getOutTradeNo() + ".png";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(product.getSubject())////订单名称
                .setTotalAmount(CommonUtil.divide(product.getTotalFee(), "100").toString()) // 商品总金额
                .setOutTradeNo(product.getOutTradeNo())  // 商品订单号
                .setSellerId("")  // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
                .setBody(product.getBody())//128长度 --附加信息，订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
                .setStoreId("test_store_id")    // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
                .setTimeoutExpress("120m") // 支付超时，定义为120分钟
                .setNotifyUrl(notify_url)//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setExtendParams(extendParams);
        //  http带参数请求，获取响应结果
        AlipayF2FPrecreateResult result = AliPayConfig.getAlipayTradeService().tradePrecreate(builder);
        // 判断订单状态
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                // 获取请求响应结果
                AlipayTradePrecreateResponse response = result.getResponse();
                // 生成二维码
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, imgPath);
                break;
            case FAILED:
                log.info("支付宝预下单失败!!!");
                message = Constants.FAIL;
                break;
            case UNKNOWN:
                log.info("系统异常，预下单状态未知!!!");
                message = Constants.FAIL;
                break;
            default:
                log.info("不支持的交易状态，交易返回异常!!!");
                message = Constants.FAIL;
                break;
        }
        return message;
    }

    //支付宝退款
    @Override
    public String aliRefund(Product product,String refundReason) {
        log.info("订单号：" + product.getOutTradeNo() + "支付宝退款");
        String message = Constants.SUCCESS;
        // 创建退款请求builder，设置请求参数
        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
                .setOutTradeNo(product.getOutTradeNo())// (必填) 外部订单号，需要退款交易的商户外部订单号
                .setRefundAmount(CommonUtil.divide(product.getTotalFee(), "100").toString())// (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
                .setRefundReason(refundReason)// (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
                //.setOutRequestNo(outRequestNo)
                .setStoreId("test_store_id");// (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持
        // 获取请求响应结果
        AlipayF2FRefundResult result = AliPayConfig.getAlipayTradeService().tradeRefund(builder);
        // 判断订单状态
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝退款成功: )");
                break;
            case FAILED:
                log.info("支付宝退款失败!!!");
                message = Constants.FAIL;
                break;
            case UNKNOWN:
                log.info("系统异常，订单退款状态未知!!!");
                message = Constants.FAIL;
                break;
            default:
                log.info("不支持的交易状态，交易返回异常!!!");
                message = Constants.FAIL;
                break;
        }
        return message;
    }

    //支付宝关闭订单
    @Override
    public String aliCloseOrder(Product product) {
        log.info("订单号：" + product.getOutTradeNo() + "支付宝关闭订单");
        String message = Constants.SUCCESS;
        try {
            String imgPath = Constants.QRCODE_PATH + Constants.SF_FILE_SEPARATOR + "alipay_" + product.getOutTradeNo() + ".png";
            File file = new File(imgPath);
            if (file.exists()) {
                AlipayClient alipayClient = AliPayConfig.getAlipayClient();
                AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
                request.setBizContent("{" +
                        "    \"out_trade_no\":\"" + product.getOutTradeNo() + "\"" +
                        "  }");
                AlipayTradeCloseResponse response = alipayClient.execute(request);
                if (response.isSuccess()) {//扫码未支付的情况
                    log.info("订单号：" + product.getOutTradeNo() + "支付宝关闭订单成功并删除支付二维码");
                    file.delete();
                } else {
                    if ("ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())) {
                        log.info("订单号：" + product.getOutTradeNo() + response.getSubMsg() + "(预下单 未扫码的情况)");
                    } else if ("ACQ.TRADE_STATUS_ERROR".equals(response.getSubCode())) {
                        log.info("订单号：" + product.getOutTradeNo() + response.getSubMsg());
                    } else {
                        log.info("订单号：" + product.getOutTradeNo() + "支付宝关闭订单失败" + response.getSubCode() + response.getSubMsg());
                        message = Constants.FAIL;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = Constants.FAIL;
            log.info("订单号：" + product.getOutTradeNo() + "支付宝关闭订单异常");
        }
        return message;
    }

    //支付宝下载账单
    @Override
    public String aliDownloadBill(String billDate, String billType) {
        log.info("获取支付宝订单地址:" + billDate);
        String downloadBillUrl = "";
        try {
            AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
            request.setBizContent("{" + "    \"bill_type\":\"trade\","
                    + "    \"bill_date\":\"2016-12-26\"" + "  }");

            AlipayDataDataserviceBillDownloadurlQueryResponse response = AliPayConfig.getAlipayClient().execute(request);
            if (response.isSuccess()) {
                log.info("获取支付宝订单地址成功:" + billDate);
                downloadBillUrl = response.getBillDownloadUrl();//获取下载地
            } else {
                log.info("获取支付宝订单地址失败" + response.getSubMsg() + ":" + billDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("获取支付宝订单地址异常:" + billDate);
        }
        return downloadBillUrl;
    }

    //支付宝H5支付
    @Override
    public String aliH5Pay(Product product) {
        log.info("支付宝手机支付下单");
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        String returnUrl = "回调地址 http 自定义";
        alipayRequest.setReturnUrl(returnUrl);//前台通知
        alipayRequest.setNotifyUrl(notify_url);//后台回调
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", product.getOutTradeNo());
        bizContent.put("total_amount", product.getTotalFee());//订单金额:元
        bizContent.put("subject", product.getSubject());//订单标题
        bizContent.put("seller_id", Configs.getPid());//实际收款账号，一般填写商户PID即可
        bizContent.put("product_code", "QUICK_WAP_PAY");//手机网页支付
        bizContent.put("body", "两个苹果五毛钱");
        String biz = bizContent.toString().replaceAll("\"", "'");
        alipayRequest.setBizContent(biz);
        log.info("业务参数:" + alipayRequest.getBizContent());
        String form = Constants.FAIL;
        try {
            form = AliPayConfig.getAlipayClient().pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            log.error("支付宝构造表单失败", e);
        }
        return form;
    }

    //支付宝网站支付
    @Override
    public String aliPcPay(Product product) {
        log.info("支付宝PC支付下单");
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        String returnUrl = "前台回调地址 http 自定义";
        alipayRequest.setReturnUrl(returnUrl);//前台通知
        alipayRequest.setNotifyUrl(notify_url);//后台回调
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", product.getOutTradeNo());
        bizContent.put("total_amount", product.getTotalFee());//订单金额:元
        bizContent.put("subject", product.getSubject());//订单标题
        bizContent.put("seller_id", Configs.getPid());//实际收款账号，一般填写商户PID即可
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");//电脑网站支付
        bizContent.put("body", "两个苹果五毛钱");
        /**
         * 这里有三种模式可供选择
         * 如果在系统内支付，并且是弹出层支付，建议选择模式二、其他模式会跳出当前iframe(亲测有效)
         */
        bizContent.put("qr_pay_mode", "2");
        String biz = bizContent.toString().replaceAll("\"", "'");
        alipayRequest.setBizContent(biz);
        log.info("业务参数:" + alipayRequest.getBizContent());
        String form = Constants.FAIL;
        try {
            form = AliPayConfig.getAlipayClient().pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            log.error("支付宝构造表单失败", e);
        }
        return form;
    }

    //支付宝APP支付
    @Override
    public String aliAppPay(Product product) {
        String orderString = Constants.FAIL;
        // 实例化客户端
        AlipayClient alipayClient = AliPayConfig.getAlipayClient();
        // 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        // SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(product.getBody());
        model.setSubject(product.getSubject());
        model.setOutTradeNo(product.getOutTradeNo());
        model.setTimeoutExpress("30m");
        model.setTotalAmount(product.getTotalFee());
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl("商户外网可以访问的异步地址");
        try {
            // 这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient
                    .sdkExecute(request);
            orderString = response.getBody();//就是orderString 可以直接给客户端请求，无需再做处理。
            //System.out.println(response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return orderString;
    }

    @Override
    public boolean rsaCheckV1(Map<String, String> params) {
        //验证签名 校验签名
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, Configs.getAlipayPublicKey(), "UTF-8");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return signVerified;
    }

    @Override
    public boolean rsaCheckV2(Map<String, String> params) {
        //验证签名 校验签名
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "UTF-8");
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return signVerified;
    }
}
