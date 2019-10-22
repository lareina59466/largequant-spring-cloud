package cn.largequant.cloudseckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "cn.largequant")
@MapperScan(basePackages = "cn.largequant.cloudseckill.dao.*")
@EnableEurekaClient
@EnableFeignClients
public class CloudSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudSeckillApplication.class, args);
    }

}
