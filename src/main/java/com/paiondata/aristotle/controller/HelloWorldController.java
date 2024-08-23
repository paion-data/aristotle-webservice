package com.paiondata.aristotle.controller;

import com.paiondata.aristotle.model.entity.Relation;
import com.paiondata.aristotle.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HelloWorldController {

    @Autowired
    private NodeService nodeService;

    @RequestMapping("/hello")
    @ResponseBody
    public Map<String, Object> showHelloWorld(){
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "HelloWorld");
        return map;
    }

    @GetMapping("parse")
    @ResponseBody
    public List<Relation> parse(String sentence) {
        return nodeService.parseAndBind(sentence);
    }
}