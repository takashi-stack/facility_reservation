package com.example.facilityreservation.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.FacilityTypeService;

@Controller
@RequestMapping(value = "/facility/")
public class FacilityListController {
    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityTypeService facilityTypeService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model,
                        @RequestParam(name = "type", required = false) Integer typeId) {
        model.addAttribute("facilities", facilityService.list());
        model.addAttribute("facilityTypes", facilityTypeService.list());
        model.addAttribute("typeId", typeId);
        return "facility-list";
    }
}
