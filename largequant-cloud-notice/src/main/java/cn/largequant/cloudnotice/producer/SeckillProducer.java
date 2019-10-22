package cn.largequant.cloudnotice.producer;

import cn.largequant.cloudnotice.constant.RabbitConstant;
import cn.largequant.cloudnotice.entity.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;


@Component
@Slf4j
public class SeckillProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    //消息发送确认回调方法
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("消息发送成功:" + correlationData);
    }

    //消息发送失败回调方法
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息发送失败:" + new String(message.getBody()));
    }

    /**
     * 发送消息，不需要实现任何接口，供外部调用
     */
    @Scheduled(fixedDelay = 5 * 1000)   //spring定时器注解,延迟5秒执行,也就是每5秒调用一次方法
    public void send(MessageVo messageVo) {
        //消息唯一键，用来保证消息幂等性，如果用这种方式，它为convertAndSend第四个参数
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //发送消息到队列(交换器，路由键，json序列化消息，唯一键)
        rabbitTemplate.convertAndSend(RabbitConstant.CONFIRM_EXCHANGE, RabbitConstant.RK_SECKILL, messageVo, correlationId);
    }
}
