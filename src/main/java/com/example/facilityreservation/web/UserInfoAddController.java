package com.example.facilityreservation.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.service.UserManageService;
import com.example.facilityreservation.web.form.UserInfoForm;

@Controller
@RequestMapping(value = "/admin/user/")
@SessionAttributes(types = UserInfoForm.class)
public class UserInfoAddController {
    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(
        UserInfoAddController.class);

    @Autowired
    private UserManageService userManageService;

    @ModelAttribute("userInfoForm")
    public UserInfoForm setupForm() {
        return new UserInfoForm();
    }

    @RequestMapping(path = "/add", method = RequestMethod.GET)
    public String index(Model model) {
        return "user-add";
    }

    @RequestMapping(path = "/add", params = "redo", method = RequestMethod.POST)
    public String index(@Validated UserInfoForm form, BindingResult result, Model model) {
        return index(model);
    }

    @RequestMapping(path = "/add", params = "confirm", method = RequestMethod.POST)
    public String confirm(@Validated UserInfoForm form, BindingResult result,
                          Model model) {

        if (result.hasErrors()) {
            return index(model);
        }

        try {
            UserEntity entity = toEntity(form);
            userManageService.saveConfirm(entity);
            return "user-add-check";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return index(model);
        }
    }

    @RequestMapping(path = "/add", params = "complete", method = RequestMethod.POST)
    public String add(@Validated UserInfoForm form, BindingResult result,
                           Model model) {

        if (result.hasErrors()) {
            return index(model);
        }

        try {
            UserEntity entity = toEntity(form);
            userManageService.save(entity);
            return "redirect:/admin/user/add?complete";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return index(model);
        }
    }

    @RequestMapping(path = "/add", params = "complete", method = RequestMethod.GET)
    public String complete(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        model.addAttribute("message", "ユーザ登録");
        return "common-completion";
    }

    private UserEntity toEntity(UserInfoForm form) {
        UserEntity entity = new UserEntity();
        entity.setId(form.getUsername());
        entity.setPassword(form.getPassword());
        entity.setPermissionLevel(form.getPermission());
        entity.setNote(form.getNote());
        return entity;
    }
}
