package cn.largequant.cloudsso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "cn.largequant")
@MapperScan(basePackages = "cn.largequant.cloudsso.dao.*")
@EnableEurekaClient
@EnableFeignClients
public class CloudSsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudSsoApplication.class, args);
    }

}
