package cn.largequant.cloudpay.service.impl;

import cn.largequant.cloudcommon.constant.Constants;
import cn.largequant.cloudcommon.entity.seckill.Product;
import cn.largequant.cloudcommon.util.CommonUtil;
import cn.largequant.cloudpay.service.WxPayService;
import cn.largequant.cloudpay.utils.*;
import com.alipay.demo.trade.utils.ZxingUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import weixin.popular.api.SnsAPI;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    private static final Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);

    @Value("${wexinpay.notify.url}")
    private String notify_url;
    @Value("${server.context.url}")
    private String server_url;

    //微信Native支付下单(模式二):用户打开"微信扫一扫“,扫描商户的二维码后完成支付
    @Override
    public String wxNativePay2(Product product) {
        logger.info("订单号：{}生成微信支付码", product.getOutTradeNo());
        String message = Constants.SUCCESS;
        try {
            // 二维码保存路径
            String imgPath = Constants.QRCODE_PATH + Constants.SF_FILE_SEPARATOR + product.getOutTradeNo() + ".png";
            // 用TreeMap保存请求参数，自动对参数排序
            SortedMap<Object, Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams); // 基础参数：APPID,商业号,随机字符串
            packageParams.put("product_id", product.getProductId());    // 商品ID
            packageParams.put("body", product.getBody());   // 商品描述
            packageParams.put("out_trade_no", product.getOutTradeNo()); // 商品订单号
            packageParams.put("total_fee", CommonUtil.subZeroAndDot(product.getTotalFee())); // 商品总金额
            packageParams.put("spbill_create_ip", product.getSpbillCreateIp()); // 发起人IP地址
            packageParams.put("notify_url", notify_url);    // 扫码回调地址
            packageParams.put("trade_type", "NATIVE");    // 交易类型(原生扫码支付)
            String sign = PayCommonUtil.createSign("UTF-8", packageParams, ConfigUtil.API_KEY); // 以上参数 + API密钥 构成签名
            packageParams.put("sign", sign); // 签名
            // 将请求参数转换为xml格式的string
            String requestXML = PayCommonUtil.getRequestXml(packageParams);
            // http带参数请求 统一下单地址
            String resXml = HttpUtil.postData(ConfigUtil.UNIFIED_ORDER_URL, requestXML);
            // 解析响应xml转成map
            Map map = XMLUtil.doXMLParse(resXml);
            // 提取return_code,判断是否请求成功
            String returnCode = (String) map.get("return_code");
            // 请求统一下单url成功
            if ("SUCCESS".equals(returnCode)) {
                // 提取result_code,判断是否成功生成微信支付码
                String resultCode = (String) map.get("result_code");
                if ("SUCCESS".equals(resultCode)) {
                    logger.info("订单号：{}生成微信支付码成功", product.getOutTradeNo());
                    // 提取code_url,并转换为短链接,最后生成二维码
                    String urlCode = (String) map.get("code_url");
                    ConfigUtil.shorturl(urlCode);
                    ZxingUtils.getQRCodeImge(urlCode, 256, imgPath);
                } else {
                    String errCodeDes = (String) map.get("err_code_des");
                    logger.info("订单号：{}生成微信支付码(系统)失败:{}", product.getOutTradeNo(), errCodeDes);
                    message = Constants.FAIL;
                }
            } else {
                String returnMsg = (String) map.get("return_msg");
                logger.info("(订单号：{}生成微信支付码(通信)失败:{}", product.getOutTradeNo(), returnMsg);
                message = Constants.FAIL;
            }
        } catch (Exception e) {
            logger.error("订单号：{}生成微信支付码失败(系统异常))", product.getOutTradeNo(), e);
            message = Constants.FAIL;
        }
        return message;
    }

    //微信Native支付下单(模式一):用户打开"微信扫一扫“,扫描商户的二维码后完成支付
    @Override
    public void wxNativePay1(Product product) {
        //商户支付回调URL设置指引：进入公众平台-->微信支付-->开发配置-->扫码支付-->修改 加入回调URL
        //注意参数初始化 这只是个Demo
        SortedMap<Object, Object> packageParams = new TreeMap<>();
        //封装通用参数
        ConfigUtil.commonParams(packageParams);
        packageParams.put("product_id", product.getProductId());//真实商品ID
        packageParams.put("time_stamp", PayCommonUtil.getCurrTime());
        //生成签名
        String sign = PayCommonUtil.createSign("UTF-8", packageParams, ConfigUtil.API_KEY);
        //组装二维码信息(注意全角和半角：的区别 狗日的腾讯)
        StringBuffer qrCode = new StringBuffer();
        qrCode.append("weixin://wxpay/bizpayurl?");
        qrCode.append("appid=" + ConfigUtil.APP_ID);
        qrCode.append("&mch_id=" + ConfigUtil.MCH_ID);
        qrCode.append("&nonce_str=" + packageParams.get("nonce_str"));
        qrCode.append("&product_id=" + product.getProductId());
        qrCode.append("&time_stamp=" + packageParams.get("time_stamp"));
        qrCode.append("&sign=" + sign);
        String imgPath = Constants.QRCODE_PATH + Constants.SF_FILE_SEPARATOR + product.getProductId() + ".png";
        /**
         * 生成二维码
         * 1、这里如果是一个单独的服务的话，建议直接返回qrCode即可，调用方自己生成二维码
         * 2、 如果真要生成，生成到系统绝对路径
         */
        ZxingUtils.getQRCodeImge(qrCode.toString(), 256, imgPath);
    }

    //微信H5支付:用户在微信以外的手机浏览器请求微信支付的场景唤起微信支付
    @Override
    public String wxH5Pay(Product product) {
        logger.info("订单号：{}发起H5支付", product.getOutTradeNo());
        // 最后返回mweb_url,用户跳转到这个链接,会自动调起微信支付
        String mwebUrl = "";
        try {
            // 用TreeMap保存请求参数，自动对参数排序
            SortedMap<Object, Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams);
            packageParams.put("product_id", product.getProductId());    // 商品ID
            packageParams.put("body", product.getBody());   // 商品描述
            packageParams.put("out_trade_no", product.getOutTradeNo()); // 商品订单号
            packageParams.put("total_fee", CommonUtil.subZeroAndDot(product.getTotalFee())); // 总金额
            packageParams.put("spbill_create_ip", product.getSpbillCreateIp()); // 发起人IP地址
            packageParams.put("notify_url", notify_url);    // 回调地址
            packageParams.put("trade_type", "MWEB");    // 交易类型(H5支付)
            // 下面H5支付专有参数
            JSONObject value = new JSONObject();
            value.put("type", "WAP");
            value.put("wap_url", "https://blog.52itstyle.com"); //WAP网站URL地址
            value.put("wap_name", "科帮网充值"); //WAP 网站名
            JSONObject scene_info = new JSONObject();
            scene_info.put("h5_info", value);
            packageParams.put("scene_info", scene_info.toString()); // H5支付专有参数
            String sign = PayCommonUtil.createSign("UTF-8", packageParams, ConfigUtil.API_KEY); // 以上参数 + API密钥 构成签名
            packageParams.put("sign", sign); // 签名
            // 将请求参数转换为xml格式的string
            String requestXML = PayCommonUtil.getRequestXml(packageParams);
            // http带参数请求 统一下单地址
            String resXml = HttpUtil.postData(ConfigUtil.UNIFIED_ORDER_URL, requestXML);
            // 解析响应xml转成map
            Map map = XMLUtil.doXMLParse(resXml);
            // 提取return_code,判断是否请求成功
            String returnCode = (String) map.get("return_code");
            if ("SUCCESS".equals(returnCode)) {
                // 提取result_code,判断是否成功发起H5支付成功
                String resultCode = (String) map.get("result_code");
                if ("SUCCESS".equals(resultCode)) {
                    logger.info("订单号：{}发起H5支付成功", product.getOutTradeNo());
                    mwebUrl = (String) map.get("mweb_url");
                } else {
                    String errCodeDes = (String) map.get("err_code_des");
                    logger.info("订单号：{}发起H5支付(系统)失败:{}", product.getOutTradeNo(), errCodeDes);
                }
            } else {
                String returnMsg = (String) map.get("return_msg");
                logger.info("(订单号：{}发起H5支付(通信)失败:{}", product.getOutTradeNo(), returnMsg);
            }
        } catch (Exception e) {
            logger.error("订单号：{}发起H5支付失败(系统异常))", product.getOutTradeNo(), e);
        }
        return mwebUrl;
    }

    //微信JSAPI支付(微信公众号支付):用户在微信内进入商家H5页面,并在微信内调用JSSDK完成支付
    @Override
    public String wxJSAPIPay(Product product) {
        // 商品总金额
        String totalFee = product.getTotalFee();
        totalFee = CommonUtil.subZeroAndDot(totalFee);
        // 注意：回调地址redirect_uri需要在微信支付端添加认证网址
        String redirect_uri = server_url + "weixinMobile/dopay?outTradeNo=" + product.getOutTradeNo() + "&totalFee=" + totalFee;
        // 也可以通过state传递参数 redirect_uri 后面加参数未经过验证
        return SnsAPI.connectOauth2Authorize(ConfigUtil.APP_ID, redirect_uri, true, null);
    }

    //微信退款
    @Override
    public String wxRefund(Product product) {
        logger.info("订单号：{}微信退款", product.getOutTradeNo());
        String message = Constants.SUCCESS;
        try {
            // 用TreeMap保存请求参数，自动对参数排序
            SortedMap<Object, Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams);
            packageParams.put("out_trade_no", product.getOutTradeNo()); // 商品订单号
            packageParams.put("out_refund_no", product.getOutTradeNo());    //商品退款单号
            String totalFee = product.getTotalFee();
            totalFee = CommonUtil.subZeroAndDot(totalFee);
            packageParams.put("total_fee", totalFee);   // 总金额
            packageParams.put("refund_fee", totalFee);  // 退款金额(全额)
            packageParams.put("op_user_id", ConfigUtil.MCH_ID); // 操作员帐号, 默认为商户号
            String sign = PayCommonUtil.createSign("UTF-8", packageParams, ConfigUtil.API_KEY); // 以上参数 + API密钥 构成签名
            packageParams.put("sign", sign);// 签名
            // 将请求参数转换为xml格式的string
            String requestXML = PayCommonUtil.getRequestXml(packageParams);
            // https(要证书)带参数请求 微信退款地址
            String resPost = ClientCustomSSL.doRefund(ConfigUtil.REFUND_URL, requestXML);
            // 解析响应xml转成map
            Map map = XMLUtil.doXMLParse(resPost);
            // 提取return_code,判断是否请求成功
            String returnCode = (String) map.get("return_code");
            // 请求退款url成功
            if ("SUCCESS".equals(returnCode)) {
                // 提取result_code,判断是否退款成功
                String resultCode = (String) map.get("result_code");
                if ("SUCCESS".equals(resultCode)) {
                    // 退款成功
                    logger.info("订单号：{}微信退款成功并删除二维码", product.getOutTradeNo());
                } else {
                    String errCodeDes = (String) map.get("err_code_des");
                    logger.info("订单号：{}微信退款失败:{}", product.getOutTradeNo(), errCodeDes);
                    message = Constants.FAIL;
                }
            } else {
                String returnMsg = (String) map.get("return_msg");
                logger.info("订单号：{}微信退款失败:{}", product.getOutTradeNo(), returnMsg);
                message = Constants.FAIL;
            }
        } catch (Exception e) {
            logger.error("订单号：{}微信支付失败(系统异常)", product.getOutTradeNo(), e);
            message = Constants.FAIL;
        }
        return message;
    }

    //微信关闭订单
    @Override
    public String wxCloseOrder(Product product) {
        logger.info("订单号：{}微信关闭订单", product.getOutTradeNo());
        String message = Constants.SUCCESS;
        try {
            // 用TreeMap保存请求参数，自动对参数排序
            SortedMap<Object, Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams); // 基础参数：APPID,商业号,随机字符串
            packageParams.put("out_trade_no", product.getOutTradeNo()); // 商品订单号
            String sign = PayCommonUtil.createSign("UTF-8", packageParams, ConfigUtil.API_KEY); // 以上参数 + API密钥 构成签名
            packageParams.put("sign", sign);    // 签名
            // 将请求参数转换为xml格式的string
            String requestXML = PayCommonUtil.getRequestXml(packageParams);
            // http带参数请求 关闭订单地址
            String resXml = HttpUtil.postData(ConfigUtil.CLOSE_ORDER_URL, requestXML);
            // 解析响应xml转成map
            Map map = XMLUtil.doXMLParse(resXml);
            // 提取return_code,判断是否请求成功
            String returnCode = (String) map.get("return_code");
            if ("SUCCESS".equals(returnCode)) {
                // 提取result_code,判断是否成功生成关闭订单
                String resultCode = (String) map.get("result_code");
                if ("SUCCESS".equals(resultCode)) {
                    // 关闭订单成功
                    logger.info("订单号：{}微信关闭订单成功", product.getOutTradeNo());
                } else {
                    // 如果不成功,看 订单是不存在还是已经关闭
                    String errCode = (String) map.get("err_code");
                    String errCodeDes = (String) map.get("err_code_des");
                    if ("ORDERNOTEXIST".equals(errCode) || "ORDERCLOSED".equals(errCode)) {
                        logger.info("订单号：{}微信关闭订单:{}", product.getOutTradeNo(), errCodeDes);
                    } else {
                        logger.info("订单号：{}微信关闭订单失败:{}", product.getOutTradeNo(), errCodeDes);
                        message = Constants.FAIL;
                    }
                }
            } else {
                String returnMsg = (String) map.get("return_msg");
                logger.info("订单号：{}微信关闭订单失败:{}", product.getOutTradeNo(), returnMsg);
                message = Constants.FAIL;
            }
        } catch (Exception e) {
            logger.error("订单号：{}微信关闭订单失败(系统异常)", product.getOutTradeNo(), e);
            message = Constants.FAIL;
        }
        return message;
    }

    //微信下载账单
    @Override
    public void wxDownloadBill() {
        try {
            //获取两天以前的账单
            //String billDate = DateUtil.getBeforeDayDate("2");
            SortedMap<Object, Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams); //公用部分
            packageParams.put("bill_type", "ALL");  //ALL，返回当日所有订单信息，默认值SUCCESS，返回当日成功支付的订单REFUND，返回当日退款订单
            //packageParams.put("tar_type", "GZIP");    //压缩账单
            packageParams.put("bill_date", "20161206"); //账单日期
            String sign = PayCommonUtil.createSign("UTF-8", packageParams, ConfigUtil.API_KEY); // 以上参数 + API密钥 构成签名
            packageParams.put("sign", sign);    // 签名
            // 将请求参数转换为xml格式的string
            String requestXML = PayCommonUtil.getRequestXml(packageParams);
            // http带参数请求 下载账单地址
            String resXml = HttpUtil.postData(ConfigUtil.DOWNLOAD_BILL_URL, requestXML);
            // 如果响应结果以<xml>开头,说明下载失败
            if (resXml.startsWith("<xml>")) {
                Map map = XMLUtil.doXMLParse(resXml);
                String returnMsg = (String) map.get("return_msg");
                logger.info("微信查询订单失败:{}", returnMsg);
            } else {
                //下载成功,在这里入库或返回
            }
        } catch (Exception e) {
            logger.error("微信查询订单异常", e);
        }
    }

    //微信查询订单
    @Override
    public String wxOrderQuery(Product product) {
        try {
            // 用TreeMap保存请求参数，自动对参数排序
            SortedMap<Object, Object> packageParams = new TreeMap<>();
            ConfigUtil.commonParams(packageParams); // 基础参数：APPID,商业号,商户订单号
            packageParams.put("out_trade_no", product.getOutTradeNo()); // 商品订单号
            String sign = PayCommonUtil.createSign("UTF-8", packageParams, ConfigUtil.API_KEY); // 以上参数 + API密钥 构成签名
            packageParams.put("sign", sign);    // 签名
            // 将请求参数转换为xml格式的string
            String requestXML = PayCommonUtil.getRequestXml(packageParams);
            // http带参数请求 查询订单地址
            String resXml = HttpUtil.postData(ConfigUtil.CHECK_ORDER_URL, requestXML);
            // 解析响应xml转成map
            Map map = XMLUtil.doXMLParse(resXml);
            // 提取return_code,判断是否请求成功
            String returnCode = (String) map.get("return_code");
            if ("SUCCESS".equals(returnCode)) {
                // 提取result_code,判断是否成功获取订单状态
                String resultCode = (String) map.get("result_code");
                if ("SUCCESS".equals(resultCode)) {
                    // 提取trade_state并返回
                    String tradeState = (String) map.get("trade_state");
                    return tradeState;
                } else {
                    String errCodeDes = (String) map.get("err_code_des");
                    return errCodeDes;
                }
            } else {
                String returnMsg = (String) map.get("return_msg");
                return returnMsg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
