package com.example.ssoinstance1.web.controller;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SSOController {

    private Set<String> set = ConcurrentHashMap.newKeySet();
    RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/test")
    public Object test(@CookieValue(value = "sessionId", required = false) String sessionId,
                       HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        // 未登录
        if (sessionId == null || !set.contains(sessionId)) {

            String referer = request.getHeader("referer");
            URIBuilder uriBuilder = new URIBuilder("http://localhost:8080/check/login");

            StringBuilder sb = new StringBuilder();

            URIBuilder builder = new URIBuilder(request.getRequestURL().toString());
            String scheme = builder.getScheme();
            sb.append(scheme + "://");
            String host = builder.getHost();
            sb.append(host + ":");
            int port = builder.getPort();
            sb.append(port);
            sb.append("/callback");
            String callback = sb.toString();

            String path = builder.getPath();
            System.out.println(path);

            uriBuilder.addParameter("callback", callback);
            uriBuilder.addParameter("redirectUrl", referer);

            response.sendRedirect(uriBuilder.build().toString());

            return "false";
        }
//        throw new RuntimeException();
//        登陆
        return "ok";
    }

    @GetMapping("/callback")
    public void loginCallback(@RequestParam("sessionId") String sessionId,
                              @RequestParam("redirectUrl") String redirectUrl,
                              HttpServletResponse response) throws IOException {
        Cookie cookie = new Cookie("sessionId", sessionId);
        response.addCookie(cookie);
        set.add(sessionId);
        response.sendRedirect(redirectUrl);
    }

}
