package com.example.facilityreservation.web;

import java.security.Principal;

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

import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.FacilityTypeService;
import com.example.facilityreservation.web.form.FacilityForm;

@Controller
@RequestMapping(value = "/admin/facility/")
@SessionAttributes(types = FacilityForm.class)
public class FacilityAddController extends BaseController {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityTypeService facilityTypeService;

    @ModelAttribute("facilityForm")
    public FacilityForm setupForm() {
        return new FacilityForm();
    }

    @RequestMapping(path = "/add", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("types", facilityTypeService.list());
        return "facility-add";
    }

    @RequestMapping(path = "/add", params = "redo", method = RequestMethod.POST)
    public String redo(@Validated FacilityForm form, BindingResult result,
                       Model model) {
        return index(model);
    }

    @RequestMapping(path = "/add", params = "confirm", method = RequestMethod.POST)
    public String confirm(@Validated FacilityForm form, BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return index(model);
        }

        try {
            model.addAttribute("types", facilityTypeService.list());
            return "facility-add-check";
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(model);
        }
    }

    @RequestMapping(path = "/add", params = "complete", method = RequestMethod.POST)
    public String add(Principal principal, @Validated FacilityForm form,
                      BindingResult result, Model model) {

        if (result.hasErrors()) return index(model);

        try {
            facilityService.add(form, getLoginUser(principal));
            return "redirect:/admin/facility/add?complete";
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(model);
        }
    }

    @RequestMapping(value = "/add", params = "complete", method = RequestMethod.GET)
    public String user_add_completion(Model model,
                                      SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        model.addAttribute("message", "施設情報登録");
        return "common-completion";
    }
}
