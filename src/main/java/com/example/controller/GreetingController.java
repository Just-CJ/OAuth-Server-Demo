package com.example.controller;

/**
 * Created by Just_CJ on 2015/12/9.
 */
import java.util.concurrent.atomic.AtomicLong;

import com.example.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping("/greeting2")
    public Greeting greeting2(HttpServletRequest request, @RequestParam(value="name", defaultValue="World2") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, request.getRemoteUser()));
    }
}
