package com.example.facilityreservation.web;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.ReservationService;
import com.example.facilityreservation.web.bean.ReserveCell;

@Controller
@RequestMapping(value = "/reservation/")
public class ReservationStatusDetailsController {
    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(
        ReservationStatusDetailsController.class);

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ReservationService reservationService;

    @RequestMapping(value = "/{facilityId}", method = RequestMethod.GET)
    public String index(@PathVariable("facilityId") Integer facilityId,
                        @RequestParam(name = "y", required = false) Integer year,
                        @RequestParam(name = "m", required = false) Integer month,
                        Model model) {
        LocalDate thisMonth = (year != null && month != null) ? LocalDate.of(
            year, month, 1) : LocalDate.now();
        LocalDate nextMonth = thisMonth.plusMonths(1);

        FacilityEntity facility = facilityService.findById(facilityId);
        ReserveCell[][] calendar = reservationService.reservationList(
            facility, thisMonth.getYear(), thisMonth.getMonthValue());

        // set Attribute
        model.addAttribute("facility", facility);
        model.addAttribute("y", thisMonth.getYear());
        model.addAttribute("m", thisMonth.getMonthValue());
        model.addAttribute("next_y", nextMonth.getYear());
        model.addAttribute("next_m", nextMonth.getMonthValue());
        model.addAttribute("calender", calendar);
        return "reservation-status-details";
    }
}
