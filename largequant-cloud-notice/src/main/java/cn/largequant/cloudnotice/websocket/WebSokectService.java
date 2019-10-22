package cn.largequant.cloudnotice.websocket;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WebSokectService implements ChannelAwareMessageListener {

    @Autowired
    private WebSocketServerEndpoint webSocketServerEndpoint;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        byte[] body = message.getBody();
        log.info("receive msg : " + new String(body));
        try {
            webSocketServerEndpoint.sendMessageToAll(new String(body));
            //确认消息成功消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error("消息推送前台出错：" + e.getMessage() + "/r/n重新发送");
            //重新发送
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
