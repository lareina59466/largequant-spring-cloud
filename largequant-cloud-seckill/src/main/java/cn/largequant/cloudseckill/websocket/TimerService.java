package cn.largequant.cloudseckill.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器
 */
@Slf4j
@Component
public class TimerService {

    @Autowired
    private WebSocketService webSocketService;

    public void executeTimeCountdown(Long startTimestamp, Long endTimestamp) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // 向websocket客户端推送消息
                log.info("【executeTimeCountdown】 run，endTimestamp={}" + endTimestamp);
                try {
                    webSocketService.sendMessage("秒杀已结束");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        // 到秒杀结束时间时执行
        log.info("startTimestamp={}", startTimestamp);
        log.info("endTimestamp={}", endTimestamp);
        log.info("currentTimestamp={}", System.currentTimeMillis());
        // schedule()只执行一次，第二个参数是要等待这么长的时间才可以第一次执行run() 方法。如果要重复执行，要加第三个参数：第一次调用之后，从第二次开始每隔多长的时间调用一次 run() 方法。
        timer.schedule(timerTask, endTimestamp - System.currentTimeMillis());
    }

}