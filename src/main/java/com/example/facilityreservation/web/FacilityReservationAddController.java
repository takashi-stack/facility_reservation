package com.example.facilityreservation.web;

import java.security.Principal;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.ReservationEntity;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.ReservationService;
import com.example.facilityreservation.web.form.ReservationForm;

@Controller
@RequestMapping(value = "/reservation/")
@SessionAttributes(types = ReservationForm.class)
public class FacilityReservationAddController extends BaseController {
    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(
        FacilityReservationAddController.class);

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ReservationService reservationService;

    @RequestMapping(value = "/{facilityId}/add", method = RequestMethod.GET)
    public String index(@PathVariable("facilityId") Integer facilityId,
                        @RequestParam(name = "y") Integer year,
                        @RequestParam(name = "m") Integer month,
                        @RequestParam(name = "d") Integer day, Model model) {

        FacilityEntity facility = facilityService.findById(facilityId);

        ReservationForm form = new ReservationForm();
        form.setY(year);
        form.setM(month);
        form.setD(day);

        model.addAttribute("reservationForm", form);
        model.addAttribute("facility", facility);
        return "facility-reservation";
    }

    public String index(ReservationForm form, FacilityEntity facility,
                        Model model) {
        model.addAttribute("reservationForm", form);
        model.addAttribute("facility", facility);
        return "facility-reservation";
    }

    @RequestMapping(path = "/{facilityId}/add", params = "redo", method = RequestMethod.POST)
    public String redo(@PathVariable("facilityId") Integer facilityId,
                       @Validated ReservationForm form, BindingResult result,
                       Model model) {
        FacilityEntity facility = facilityService.findById(facilityId);
        return index(form, facility, model);
    }

    @RequestMapping(path = "/{facilityId}/add", params = "confirm", method = RequestMethod.POST)
    public String confirm(@PathVariable("facilityId") Integer facilityId,
                          @Validated ReservationForm form, BindingResult result,
                          Principal principal, Model model) {
        FacilityEntity facility = facilityService.findById(facilityId);
        if (result.hasErrors()) {
            return index(form, facility, model);
        }

        try {
            reservationService.checkReserve(facility, form.getStartTime(), form
                .getEndTime());
            model.addAttribute("reservationForm", form);
            model.addAttribute("facility", facility);
            return "facility-reservation-check";
        } catch (BusinessException e) {
            model.addAttribute("message", e.getMessage());
            return index(form, facility, model);
        } catch (Exception e) {
            model.addAttribute("message", "システムエラーが発生しました。");
            return index(form, facility, model);
        }
    }

    @RequestMapping(path = "/{facilityId}/add", params = "complete", method = RequestMethod.POST)
    public String add(@PathVariable("facilityId") Integer facilityId,
                      @Validated ReservationForm form, BindingResult result,
                      Principal principal, Model model) {
        FacilityEntity facility = facilityService.findById(facilityId);

        if (result.hasErrors()) {
            return index(form, facility, model);
        }

        try {
            ReservationEntity reservation = new ReservationEntity();

            reservation.setFacility(facility);
            reservation.setStartTime(form.getStartTime());
            reservation.setEndTime(form.getEndTime());
            reservation.setUser(getLoginUser(principal));

            reservationService.reserve(reservation);
            return "redirect:/reservation/" + facilityId + "/add?complete";
        } catch (BusinessException e) {
            model.addAttribute("message", e.getMessage());
            return index(form, facility, model);
        } catch (Exception e) {
            model.addAttribute("message", "システムエラーが発生しました。");
            return index(form, facility, model);
        }
    }

    @RequestMapping(path = "/{facilityId}/add", params = "complete", method = RequestMethod.GET)
    public String completion(Model model, SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        model.addAttribute("message", "予約");
        return "common-completion";
    }
}
