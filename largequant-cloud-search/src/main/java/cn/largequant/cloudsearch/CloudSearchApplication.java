package cn.largequant.cloudsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "cn.largequant")
public class CloudSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudSearchApplication.class, args);
    }

}
