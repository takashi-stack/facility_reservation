package com.example.facilityreservation.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
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
public class UserInfoUpdateController {
    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(
        UserInfoUpdateController.class);

    @Autowired
    private UserManageService userManageService;

    @RequestMapping(path = "/update/{userId}", method = RequestMethod.GET)
    public String index(@PathVariable(value = "userId") String userId,
                        Model model) {
        UserEntity entity = userManageService.findById(userId);

        UserInfoForm form = new UserInfoForm();
        form.setId(userId);
        form.setUsername(entity.getId());
        form.setPassword(entity.getPassword());
        form.setPermission(entity.getPermissionLevel()
            .ordinal());
        form.setNote(entity.getNote());

        model.addAttribute("userInfoForm", form);
        return "user-update";
    }

    public String index(UserInfoForm form, Model model) {
        model.addAttribute("userInfoForm", form);
        return "user-update";
    }

    @RequestMapping(path = "/update/{userId}", params = "redo", method = RequestMethod.POST)
    public String update_redo(@PathVariable(value = "userId") String userId,
                              @Validated UserInfoForm form,
                              BindingResult result, Model model) {
        return index(form, model);
    }

    @RequestMapping(path = "/update/{userId}", params = "update_confirm", method = RequestMethod.POST)
    public String update_confirm(@PathVariable(value = "userId") String userId,
                                 @Validated UserInfoForm form,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            return index(form, model);
        }

        try {
            UserEntity entity = toEntity(form);
            userManageService.saveConfirm(entity);
            return "user-update-check";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return index(form, model);
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(form, model);
        }
    }

    @RequestMapping(path = "/update/{userId}", params = "complete", method = RequestMethod.POST)
    public String update(@PathVariable(value = "userId") String userId,
                         @Validated UserInfoForm form, BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return index(form, model);
        }

        try {
            UserEntity entity = toEntity(form);
            userManageService.save(entity);
            return "redirect:/admin/user/update/" + userId + "?complete";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return index(form, model);
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(form, model);
        }
    }

    @RequestMapping(path = "/update/{userId}", params = "complete", method = RequestMethod.GET)
    public String update_completion(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        model.addAttribute("message", "ユーザ情報の更新");
        return "common-completion";
    }

    @RequestMapping(path = "/delete/{userId}", params = "redo", method = RequestMethod.POST)
    public String delete_redo(@PathVariable(value = "userId") String userId,
                              @Validated UserInfoForm form,
                              BindingResult result, Model model) {
        return index(form, model);
    }

    @RequestMapping(path = "/update/{userId}", params = "delete_confirm", method = RequestMethod.POST)
    public String delete_confirm(@PathVariable(value = "userId") String userId,
                                 @Validated UserInfoForm form,
                                 BindingResult result, Model model) {

        try {
            userManageService.deleteConfirm(userId);
            UserEntity entity = userManageService.findById(userId);
            form.setId(userId);
            form.setUsername(entity.getId());
            form.setPassword(entity.getPassword());
            form.setPermission(entity.getPermissionLevel()
                .ordinal());
            form.setNote(entity.getNote());

            model.addAttribute("userInfoForm", form);
            return "user-delete-check";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return index(form, model);
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(form, model);
        }
    }

    @RequestMapping(path = "/delete/{userId}", params = "complete", method = RequestMethod.POST)
    public String delete(@PathVariable(value = "userId") String userId,
                         @Validated UserInfoForm form, BindingResult result,
                         Model model) {

        try {
            userManageService.deleteById(userId);
            return "redirect:/admin/user/delete/" + userId + "?complete";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return index(form, model);
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(form, model);
        }
    }

    @RequestMapping(path = "/delete/{userId}", params = "complete", method = RequestMethod.GET)
    public String delete_completion(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        model.addAttribute("message", "ユーザ情報の削除");
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
