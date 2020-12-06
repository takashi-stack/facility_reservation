package com.example.facilityreservation.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.service.InitService;

@Controller
@RequestMapping("/admin/")
public class InitController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(
        InitController.class);

    @Autowired
    private InitService initService;

    @RequestMapping(path = "/init", method = RequestMethod.GET)
    public String index(Model model) {
        return "init";
    }

    @RequestMapping(path = "/init", method = RequestMethod.POST)
    public String init(Principal principal, Model model) {
        try {
            UserEntity user = getLoginUser(principal);
            if (!"root".equals(user.getId()) && !"ginga".equals(user.getId())) {
                throw new BusinessException("ログインユーザが初期ユーザではありません。");
            }

            initService.initData();

            return "redirect:/admin/init?complete";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return index(model);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(model);
        }
    }

    @RequestMapping(path = "/init", params = "complete", method = RequestMethod.GET)
    public String init_competion(Model model) {
        model.addAttribute("message", "初期化");
        return "common-completion";
    }
}
