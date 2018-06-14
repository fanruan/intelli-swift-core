package com.fr.swift.http;

import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestBody;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/6/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Controller()
public class TestController {

    @ResponseBody
    @RequestMapping(value = "swift/test/string", method = RequestMethod.GET)
    public String testPrint(HttpServletResponse response, HttpServletRequest request) {
        System.out.println("swift/test");
        return String.valueOf(System.currentTimeMillis());
    }

    @ResponseBody
    @RequestMapping(value = "swift/test/map", method = RequestMethod.GET)
    public Object testPrint1(HttpServletResponse response, HttpServletRequest request) {
        System.out.println("swift/test");
        Map map = new HashMap();
        map.put("1", System.currentTimeMillis());
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "swift/test/{id}", method = RequestMethod.GET)
    public Object testPrint2(HttpServletResponse response, HttpServletRequest request, @PathVariable("id") String id) {
        System.out.println("swift/test");
        List<String> list = new ArrayList<String>();
        list.add(String.valueOf(System.currentTimeMillis()));
        list.add(String.valueOf(System.currentTimeMillis()));
        list.add(String.valueOf(System.currentTimeMillis()));
        return list;
    }

    @ResponseBody
    @RequestMapping(value = "swift/test/postdata", method = RequestMethod.POST)
    public Object testPrint2(HttpServletResponse response, HttpServletRequest request,
                             @RequestBody(required = false) Map<String, Object> requestMap) {
        System.out.println("swift/test");
        List<String> list = new ArrayList<String>();
        list.add(String.valueOf(System.currentTimeMillis()));
        list.add(String.valueOf(System.currentTimeMillis()));
        list.add(String.valueOf(System.currentTimeMillis()));
        return list;
    }
}
