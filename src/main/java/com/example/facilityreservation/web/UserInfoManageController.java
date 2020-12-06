package com.example.facilityreservation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.facilityreservation.service.UserManageService;

@Controller
public class UserInfoManageController {
    @Autowired
    private UserManageService userManageService;

    @RequestMapping(value = "/admin/user", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("users", userManageService.list());
        return "user-management";
    }

}
