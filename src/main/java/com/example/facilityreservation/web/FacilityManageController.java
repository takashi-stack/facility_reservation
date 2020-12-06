package com.example.facilityreservation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.facilityreservation.service.FacilityService;

@Controller
@RequestMapping(value = "/admin/facility/")
public class FacilityManageController {

    @Autowired
    private FacilityService facilityService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("facilities", facilityService.list());
        return "facility-management";
    }
}
