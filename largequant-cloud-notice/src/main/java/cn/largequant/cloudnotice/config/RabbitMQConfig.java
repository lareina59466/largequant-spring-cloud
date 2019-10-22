package cn.largequant.cloudnotice.config;

import cn.largequant.cloudnotice.config.annotation.ConfirmRabbitTemplate;
import cn.largequant.cloudnotice.constant.RabbitConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class RabbitMQConfig {

    /**
     * 默认采用了java的序列化，性能比较低，而且阅读不友好
     */
    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 注意SimpleAsyncTaskExecutor每次执行一个任务都会新建一个线程，对于生命周期很短的任务不要使用这个线程池，
     */
    @Bean
    public SimpleAsyncTaskExecutor getTaskExecutor() {
        return new SimpleAsyncTaskExecutor("amqp-consumer-");
    }

    /**
     * 配置连接工厂
     */
    @Bean
    SimpleRabbitListenerContainerFactory containerWithConfirm(ConnectionFactory connectionFactory) {
        //SpringBoot自动注解
        SimpleRabbitListenerContainerFactory container = new SimpleRabbitListenerContainerFactory();
        //设置连接工厂
        container.setConnectionFactory(connectionFactory);
        //设置为手动确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //设置消息序列化方式
        container.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置线程池
        container.setTaskExecutor(getTaskExecutor());
        //container.setMaxConcurrentConsumers(1);
        //container.setConcurrentConsumers(1);
        return container;
    }

    /**
     * 配置RabbitTemplate
     */
    @Bean
    @ConfirmRabbitTemplate
    RabbitTemplate rabbitTemplateWithConfirm(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 每次发送消息都会回调这个方法
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause)
                -> log.info("confirm callback id:{},ack:{},cause:{}", correlationData, ack, cause));
        //注意这里需要手动指定，指定之后，如果消息发送到一个交换器，但是匹配不到一个队列，对调用Return回调
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey)
                -> log.info("return callback message：{},code:{},text:{}", message, replyCode, replyText));
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    //声明评论消息队列
    @Bean
    public Queue commentQueue() {
        // true表示持久化该队列
        return new Queue(RabbitConstant.COMMENT_QUEUE, true);
    }

    //声明点赞消息队列
    @Bean
    public Queue likeQueue() {
        // true表示持久化该队列
        return new Queue(RabbitConstant.LIKE_QUEUE, true);
    }

    //声明秒杀消息队列
    @Bean
    public Queue seckillQueue() {
        // true表示持久化该队列
        return new Queue(RabbitConstant.SECKILL_QUEUE, true);
    }

    /**
     * 声明Topic类型的交互器
     */
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(RabbitConstant.CONFIRM_EXCHANGE, false, true);
    }

    /**
     * 绑定评论队列和交互器
     */
    @Bean
    public Binding bindingTransaction() {
        return BindingBuilder.bind(commentQueue()).to(topicExchange()).with(RabbitConstant.RK_COMMENT);
    }

    /**
     * 绑定点赞队列和交互器
     */
    @Bean
    public Binding bindingContract() {
        return BindingBuilder.bind(likeQueue()).to(topicExchange()).with(RabbitConstant.RK_LIKE);
    }

    /**
     * 绑定秒杀队列和交互器
     */
    @Bean
    public Binding bindingQualification() {
        return BindingBuilder.bind(seckillQueue()).to(topicExchange()).with(RabbitConstant.RK_SECKILL);
    }

}
