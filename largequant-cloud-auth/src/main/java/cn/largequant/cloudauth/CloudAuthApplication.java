package cn.largequant.cloudauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "cn.largequant.cloudauth.dao.*")
@SpringBootApplication(scanBasePackages = "cn.largequant")
public class CloudAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudAuthApplication.class, args);
    }

}
