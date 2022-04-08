package cn.com.dmg.myspringboot.controller;

import cn.com.dmg.myspringboot.utils.MyXmlUtils;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class MyController {

    @Value("${server.port}")
    private String port;
//    @Resource
//    private RedisUtil redisUtil;

    @GetMapping("/getPort")
    public String getPort(){
        return port;
    }


    @GetMapping("/testJz")
    public String testJz(){
        //System.out.println(redisUtil);
        //MyXmlUtils.test();
        return port;
    }


}
