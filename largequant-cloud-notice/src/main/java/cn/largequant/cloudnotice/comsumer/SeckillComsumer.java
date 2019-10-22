package cn.largequant.cloudnotice.comsumer;

import cn.largequant.cloudnotice.constant.RabbitConstant;
import cn.largequant.cloudnotice.entity.MessageVo;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class SeckillComsumer {

    @RabbitListener(queues = RabbitConstant.COMMENT_QUEUE)
    public void process(MessageVo messageVo, Channel channel,
                        @Header(name = "amqp_deliveryTag") long deliveryTag,
                        @Header("amqp_redelivered") boolean redelivered) throws IOException {
        try {
           /* //  获取触发用户
            User user = userService.getUser(eventMsg.getActorId());
            //  生成站内信
            Message message = new Message();
            message.setFromId(UtilBox.SYSTEM_USERID);
            message.setToId(eventMsg.getEntityOwnerId());
            message.setCreatedDate(new Date());
            message.setConversationId(String.format("%d_%d", message.getFromId(), message.getToId()));
            message.setHasRead(0);
            message.setContent("用户:" + user.getName() + " 评论了你的问题:http://127.0.0.1:8080/question/" + eventMsg.getExt("questionId"));
            //  保存消息到数据库
            messageService.addMessage(message);*/
            channel.basicAck(deliveryTag, false);
        }catch (Exception e) {
            log.error("consume confirm error!", e);
            //这一步千万不要忘记，不会会导致消息未确认，消息到达连接的qos之后便不能再接收新消息
            //一般重试肯定的有次数，这里简单的根据是否已经重发过来来决定重发。第二个参数表示是否重新分发
            channel.basicReject(deliveryTag, !redelivered);
            //这个方法我知道的是比上面多一个批量确认的参数
            // channel.basicNack(deliveryTag, false,!redelivered);
        }
    }

}
