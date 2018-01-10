package com.example.ssocommonlogin.web.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class LoginController {

    private Set<String> set = ConcurrentHashMap.newKeySet();

    @PostMapping("/login")
    public boolean login(@RequestParam(value = "userName") String userName,
                         @RequestParam(value = "pwd") String pwd,
                         @RequestParam(value = "callback") String callback,
                         @RequestParam(value = "redirectUrl") String redirectUrl,
                         HttpServletResponse response) throws IOException {

        if (userName.equals("wyj") && pwd.equals("123123")) {
            String sessionId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("sessionId", sessionId);
            response.addCookie(cookie);
            set.add(sessionId);
            response.sendRedirect(callback + "?sessionId=" +
                    sessionId + "&redirectUrl=" + redirectUrl);
            return true;
        }
        return false;
    }

    @GetMapping("/check/login")
    public void checkLogin(@CookieValue(value = "sessionId", required = false) String sessionId,
                              @RequestParam(value = "redirectUrl") String redirectUrl,
                              @RequestParam(value = "callback") String callback,
                              HttpServletRequest request,
                              HttpServletResponse response) throws IOException {

        // 没有登陆
        if (sessionId == null || !set.contains(sessionId)) {
            response.sendRedirect(request.getContextPath() + "/index.html?redirectUrl=" +
                    redirectUrl+"&callback="+callback);
            return;
        }

        response.sendRedirect(callback + "?sessionId=" + sessionId + "&redirectUrl=" + redirectUrl);
    }

    @GetMapping("/check/sessionId")
    public boolean checkSessionId(@RequestParam("sessionId") String sessionId) {
        return set.contains(sessionId);
    }

    @GetMapping("/logout")
    public boolean logout(@RequestParam(value = "sessionId") String sessionId) {
        if (sessionId != null) {
            set.remove(sessionId);
        }
        return true;
    }
}
