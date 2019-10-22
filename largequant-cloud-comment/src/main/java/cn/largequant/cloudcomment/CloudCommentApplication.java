package cn.largequant.cloudcomment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackages = "cn.largequant")
@MapperScan(basePackages = "cn.largequant.cloudcomment.dao.*")
@EnableEurekaClient
public class CloudCommentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudCommentApplication.class, args);
    }

}
