package cn.deercare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
@MapperScan("com.deercare.mapper")
public class SpringBootMvnWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootMvnWebApplication.class, args);
    }
}
