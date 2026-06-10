package com.feng.militia_admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.feng.militia_admin.mapper")
public class MilitiaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilitiaAdminApplication.class, args);
    }

}
