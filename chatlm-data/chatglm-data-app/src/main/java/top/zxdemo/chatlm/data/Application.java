package top.zxdemo.chatlm.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


/**
 * @author ZhangXing zxdemo.top
 * @description 启动类
 */
@Slf4j
@SpringBootApplication
@Configurable
@EnableScheduling
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


}
