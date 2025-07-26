package org.scoula.paymentlist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentListController {

    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }


    public void init(){
        System.out.println("paymentlistcontroller 등록됨");
    }
}
