package com.kieran.mallchat.common;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication(scanBasePackages = {"com.kieran.mallchat"})
@MapperScan({"com.kieran.mallchat.common.**.mapper"})
@ServletComponentScan
@EnableEncryptableProperties // JASYPT解密用
@EnableAspectJAutoProxy(exposeProxy = true) // 使用AopContext.currentProxy 必须开启
public class MallchatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallchatCustomApplication.class,args);
    }

}
