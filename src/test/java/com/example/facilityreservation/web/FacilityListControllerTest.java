package com.example.facilityreservation.web;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.FacilityTypeEntity;
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.FacilityTypeService;
import com.example.facilityreservation.utility.WithMockCustomUser;
import com.example.facilityreservation.utility.MockFacility;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class FacilityListControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @MockBean
    private FacilityTypeService facilityTypeService;
    
    @BeforeEach
    void setup() {
        clearInvocations(facilityService);
        clearInvocations(facilityTypeService);
    }
    
    /**
     * ADMINユーザー
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void indexTest() throws Exception {
        List<FacilityEntity> ft = MockFacility.genFacilityEntitys();
        when(facilityService.list()).thenReturn(ft);
        List<FacilityTypeEntity> fte = MockFacility.genMockFacilityTypes();
        when(facilityTypeService.list()).thenReturn(fte);

        mockMvc.perform(get("/facility/").param("type", "777") )
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-list"))
            .andExpect(model().attribute("facilities", ft))
            .andExpect(model().attribute("facilityTypes", fte))
            .andExpect(model().attribute("typeId", 777));
        verify(facilityService, times(1)).list();
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * ログイン前
     * @throws Exception
     */
    @Test
    public void indexTestBeforeLogin() throws Exception {
        mockMvc.perform(get("/facility/").param("type", "777") )
            //.andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("http://localhost/login"));
        verify(facilityService, never()).list();
        verify(facilityTypeService, never()).list();
    }
}
