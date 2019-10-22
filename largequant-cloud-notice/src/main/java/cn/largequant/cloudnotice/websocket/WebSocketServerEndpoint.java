package cn.largequant.cloudnotice.websocket;


import cn.largequant.cloudnotice.entity.MessageVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/yxd/{userId}") //WebSocket客户端建立连接的地址
@Component
@Slf4j
public class WebSocketServerEndpoint {

    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    //存活的session集合（使用线程安全的map保存）
    private static Map<String, Session> livingSessions = new ConcurrentHashMap<>();

    /**
     * 建立连接的回调方法
     *
     * @param session 与客户端的WebSocket连接会话
     * @param userId  用户名，WebSocket支持路径参数
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        livingSessions.put(session.getId(), session);
        log.info(userId + "进入连接");
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
        log.info(userId + " : " + message);
        sendMessageToAll(userId + " : " + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("发生错误");
        log.error(error.getStackTrace() + "");
    }


    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        livingSessions.remove(session.getId());
        log.info(userId + " 关闭连接");
    }

    /**
     * 单独发送消息
     *
     * @param session
     * @param message
     */
    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发消息
     *
     * @param message
     */
    public void sendMessageToAll(String message) {
        MessageVo messageVo = JSONObject.parseObject(message, MessageVo.class);
        livingSessions.forEach((sessionId, session) -> {
            //发给指定的接收用户
//            if (userId.equals(messageVo.getReceiveUserId())) {
            sendMessage(session, message);
//            }
        });
    }
}
