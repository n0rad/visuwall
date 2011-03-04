package com.jsmadja.wall.projectwall.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.jsmadja.wall.projectwall.persistence.dao.UserService;
import com.jsmadja.wall.projectwall.persistence.entity.User;

@Controller
@RequestMapping("security")
public class SecurityController implements AuthenticationFailureHandler, AuthenticationSuccessHandler {

    @Autowired
    UserService userService;

    /**
     * This mapping is never called as spring security will intercept this url to process the login.
     */
    public void login() {
        // never called
    }

    /**
     * This mapping is never called as spring security will intercept this url to process the logout.
     */
    public void logout() {
        // never called
    }

    @RequestMapping("getregister")
    public ModelAndView getregister() {
        return new ModelAndView("user/register", "User", new User());
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public void register(User user, ServletRequest request, HttpServletResponse response) throws Exception {
        userService.save(user);
        //response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().append("ok");
    }

    @RequestMapping("getlogin")
    public ModelAndView getlogin(ServletRequest request, HttpServletResponse response) throws Exception {
        int code;
        if (request.getParameter("code") != null) {
            code = Integer.parseInt(request.getParameter("code"));

            // HTTP status code
            response.setStatus(code);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("User", new User());
        data.put("code", request.getParameter("code"));
        return new ModelAndView("user/login", data);
    }

    @RequestMapping("loggedout")
    public void loggedout(ServletResponse response) throws Exception {
        response.getWriter().append("logout success");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException e) throws IOException, ServletException {
        // TODO change code? using exception type to tell why login fail (typically used on disabled accounts)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/security/getlogin?code=403");
        dispatcher.forward(request, response);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // TODO send user informations
        System.out.println("there");
    }
}
