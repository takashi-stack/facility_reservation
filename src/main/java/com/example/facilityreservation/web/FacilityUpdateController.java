package com.example.facilityreservation.web;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.FacilityTypeEntity;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.FacilityTypeService;
import com.example.facilityreservation.web.form.FacilityForm;

@Controller
@RequestMapping(value = "/admin/facility/")
@SessionAttributes(types = FacilityForm.class)
public class FacilityUpdateController extends BaseController {
    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityTypeService facilityTypeService;

    @ModelAttribute("types")
    public List<FacilityTypeEntity> getTypes() {
        return facilityTypeService.list();
    }

    @RequestMapping(value = "/update/{facilityId}", method = RequestMethod.GET)
    public String index(@PathVariable(value = "facilityId") Integer facilityId,
                        Model model) {
        FacilityEntity entity = facilityService.findById(facilityId);

        FacilityForm form = new FacilityForm();
        form.setId(entity.getId());
        form.setName(entity.getName());
        form.setTypeId(entity.getType().getId());
        form.setCapacity(entity.getCapacity());

        model.addAttribute("facilityForm", form);
        return "facility-update";
    }

    public String index(FacilityForm form, Model model) {
        model.addAttribute("facilityForm", form);
        return "facility-update";
    }

    @RequestMapping(value = "/update/{facilityId}", params = "redo", method = RequestMethod.POST)
    public String update_redo(@PathVariable(value = "facilityId") Integer facilityId,
                              @Validated FacilityForm form,
                              BindingResult result, Model model) {
        return index(form, model);
    }

    @RequestMapping(value = "/update/{facilityId}", params = "update_confirm", method = RequestMethod.POST)
    public String update_confirm(@PathVariable(value = "facilityId") Integer facilityId,
                                 @Validated FacilityForm form,
                                 BindingResult result, Principal principal,
                                 Model model) {
        if (result.hasErrors()) {
            return index(form, model);
        }

        try {
            facilityService.updateConfirm(facilityId);
            return "facility-update-check";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return index(form, model);
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(form, model);
        }
    }

    @RequestMapping(value = "/update/{facilityId}", params = "complete", method = RequestMethod.POST)
    public String update(@PathVariable(value = "facilityId") Integer facilityId,
                         Principal principal, @Validated FacilityForm form,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            return index(form, model);
        }

        try {
            facilityService.update(form, getLoginUser(principal), facilityId);
            return "redirect:/admin/facility/update/?complete";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return index(form, model);
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(form, model);
        }
    }

    @RequestMapping(value = "/update", params = "complete", method = RequestMethod.GET)
    public String update_completion(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        model.addAttribute("message", "施設情報の更新");
        return "common-completion";
    }

    @RequestMapping(value = "/delete/{facilityId}", params = "redo", method = RequestMethod.POST)
    public String delete_redo(@PathVariable(value = "facilityId") Integer facilityId,
                              @Validated FacilityForm form,
                              BindingResult result, Model model) {
        return index(form, model);
    }

    @RequestMapping(value = "/update/{facilityId}", params = "delete_confirm", method = RequestMethod.POST)
    public String delete_confirm(@PathVariable(value = "facilityId") Integer facilityId,
                                 @Validated FacilityForm form,
                                 BindingResult result, Principal principal,
                                 Model model) {
        if (result.hasErrors()) {
            return index(form, model);
        }

        try {
            facilityService.deleteConfirm(facilityId);
            FacilityEntity entity = facilityService.findById(facilityId);
            form.setId(entity.getId());
            form.setName(entity.getName());
            form.setTypeId(entity.getType().getId());
            form.setCapacity(entity.getCapacity());

            return "facility-delete-check";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return index(form, model);
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(form, model);
        }
    }

    @RequestMapping(value = "/delete/{facilityId}", params = "complete", method = RequestMethod.POST)
    public String delete(@PathVariable(value = "facilityId") Integer facilityId,
                         @Validated FacilityForm form, BindingResult result,
                         Principal principal, Model model) {
        if (result.hasErrors()) {
            return index(form, model);
        }

        try {
            facilityService.deleteById(facilityId);
            return "redirect:/admin/facility/delete/?complete";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return index(form, model);
        } catch (Exception e) {
            model.addAttribute("error", "システムエラーが発生しました。");
            return index(form, model);
        }
    }

    @RequestMapping(value = "/delete", params = "complete", method = RequestMethod.GET)
    public String delete_completion(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        model.addAttribute("message", "施設情報の削除");
        return "common-completion";
    }
}
