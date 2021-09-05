package com.example.facilityreservation.web;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.ReservationEntity;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.ReservationService;
import com.example.facilityreservation.utility.MockFacility;
import com.example.facilityreservation.utility.WithMockCustomUser;
import com.example.facilityreservation.web.form.ReservationForm;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class FacilityReservationAddControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @MockBean
    private ReservationService reservationService;

    private String root = "/reservation";
    
    private FacilityEntity mockValue = MockFacility.genFacilityEntity();

    @BeforeEach
    void setup() {
        clearInvocations(facilityService);
        clearInvocations(reservationService);
    }

    /**
     * index
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void indexTest() throws Exception {
        when(facilityService.findById(7)).thenReturn(mockValue);
        mockMvc.perform(
                get(root + "/7/add")
                .param("y", "2021")   
                .param("m", "1")   
                .param("d", "1")   
                )
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue));
        verify(facilityService, times(1)).findById(7);
    }

    /**
     * redo
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void redoTest() throws Exception {
        when(facilityService.findById(8)).thenReturn(mockValue);
        ReservationForm form = genForm();

        mockMvc.perform(
                post(root + "/8/add?redo")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue));
        verify(facilityService, times(1)).findById(8);
    }
    
    /**
     * confirm
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void confirmTest() throws Exception {
        when(facilityService.findById(9)).thenReturn(mockValue);
        ReservationForm form = genForm();

        mockMvc.perform(
                post(root + "/9/add?confirm")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation-check"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue));
        verify(facilityService, times(1)).findById(9);
        verify(reservationService, times(1)).checkReserve(mockValue, form.getStartTime(), form.getEndTime());
    }
    
    /**
     * confirm L79
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void confirmTestHasErrorMonth() throws Exception {
        when(facilityService.findById(9)).thenReturn(mockValue);
        ReservationForm form = genForm();
        form.setM(null);

        mockMvc.perform(
                post(root + "/9/add?confirm")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue))
            .andExpect(model().hasErrors());
        verify(facilityService, times(1)).findById(9);
    }
    
    /**
     * confirm L90
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void confirmTestThrowBusinessException() throws Exception {
        when(facilityService.findById(10)).thenReturn(mockValue);
        
        ReservationForm form = genForm();

        doThrow(new BusinessException("業務エラー"))
        .when(reservationService).checkReserve(mockValue, form.getStartTime(), form.getEndTime());

        mockMvc.perform(
                post(root + "/10/add?confirm")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue))
            .andExpect(model().attribute("message", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(facilityService, times(1)).findById(10);
        verify(reservationService, times(1)).checkReserve(mockValue, form.getStartTime(), form.getEndTime());
    }

    /**
     * confirm L93
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void confirmTestThrowException() throws Exception {
        when(facilityService.findById(11)).thenReturn(mockValue);
        
        ReservationForm form = genForm();

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(reservationService).checkReserve(mockValue, form.getStartTime(), form.getEndTime());

        mockMvc.perform(
                post(root + "/11/add?confirm")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue))
            .andExpect(model().attribute("message", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(facilityService, times(1)).findById(11);
        verify(reservationService, times(1)).checkReserve(mockValue, form.getStartTime(), form.getEndTime());
    }

    /**
     * add
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void addTest() throws Exception {
        when(facilityService.findById(9)).thenReturn(mockValue);
        ReservationForm form = genForm();

        mockMvc.perform(
                post(root + "/9/add?complete")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/reservation/9/add?complete"));
        verify(facilityService, times(1)).findById(9);
        verify(reservationService, times(1)).reserve((ReservationEntity) any());
    }
    
    /**
     * add L104
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void addTestHasErrorDate() throws Exception {
        when(facilityService.findById(9)).thenReturn(mockValue);
        ReservationForm form = genForm();
        form.setD(null);

        mockMvc.perform(
                post(root + "/9/add?complete")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue))
            .andExpect(model().hasErrors());
        verify(facilityService, times(1)).findById(9);
        verify(reservationService, never()).reserve((ReservationEntity) any());
    }
    
    /**
     * add L119
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void addTestThrowBusinessException() throws Exception {
        when(facilityService.findById(10)).thenReturn(mockValue);
        
        ReservationForm form = genForm();

        doThrow(new BusinessException("業務エラー"))
        .when(reservationService).reserve((ReservationEntity) any());

        mockMvc.perform(
                post(root + "/10/add?complete")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue))
            .andExpect(model().attribute("message", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(facilityService, times(1)).findById(10);
        verify(reservationService, times(1)).reserve((ReservationEntity) any());
    }

    /**
     * add L122
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void addTestThrowException() throws Exception {
        FacilityEntity mockValue = MockFacility.genFacilityEntity();
        when(facilityService.findById(11)).thenReturn(mockValue);
        
        ReservationForm form = genForm();

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(reservationService).reserve((ReservationEntity) any());

        mockMvc.perform(
                post(root + "/11/add?complete")
                .flashAttr("reservationForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-reservation"))
            .andExpect(model().attributeExists("reservationForm"))
            .andExpect(model().attribute("facility", mockValue))
            .andExpect(model().attribute("message", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(facilityService, times(1)).findById(11);
        verify(reservationService, times(1)).reserve((ReservationEntity) any());
    }
    
    /**
     * completion
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void completionTest() throws Exception {
        mockMvc.perform(get(root + "/999/add?complete"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("common-completion"))
            .andExpect(model().attribute("message", "予約"));
    }
    
    private ReservationForm genForm() {
        ReservationForm form = new ReservationForm();
        form.setReservationId(1);
        form.setY(2021);
        form.setM(1);
        form.setD(1);
        // TODO: 相関チェックが必要？
        form.setStartHour("10");
        form.setStartMin("00");
        form.setEndHour("9");
        form.setEndMin("00");
        return form;
    }
}
