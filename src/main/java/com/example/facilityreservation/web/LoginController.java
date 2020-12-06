package com.example.facilityreservation.web;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.facilityreservation.web.authentication.InValidAuthenticationException;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(
        LoginController.class);

    @Autowired
    HttpSession session;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String index() {
        return "login";
    }

    @RequestMapping(path = "/login", params = "error", method = RequestMethod.GET)
    public String indexError(Model model) {

        Object obj = session.getAttribute(
            WebAttributes.AUTHENTICATION_EXCEPTION);
        if (obj == null) return index();

        logger.info("Exception is " + obj.toString());
        if (obj instanceof InValidAuthenticationException) {
            InValidAuthenticationException e = (InValidAuthenticationException) obj;
            if (e.hasError("username")) {
                model.addAttribute("errorUsername", e.getErrors("username"));
            }

            if (e.hasError("password")) {
                model.addAttribute("errorPassword", e.getErrors("password"));
            }
        } else if (obj instanceof AuthenticationServiceException) {
            model.addAttribute("error", "システムエラーが発生しました。管理者にお問い合わせください。");
        } else if (obj instanceof AuthenticationException) {
            AuthenticationException e = (AuthenticationException) obj;
            model.addAttribute("error", e.getMessage());
        } else {
            model.addAttribute("error", "システムエラーが発生しました。管理者にお問い合わせください。");
        }

        return index();
    }
}
