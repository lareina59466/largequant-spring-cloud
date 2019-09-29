package cn.largequant.cloudeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication(scanBasePackages = "cn.largequant")
public class CloudEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudEurekaApplication.class, args);
    }

}
