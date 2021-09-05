package com.example.facilityreservation.web;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.utility.WithMockCustomUser;
import com.example.facilityreservation.utility.MockFacility;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class FacilityManageControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;
    
    private String root = "/admin/facility/";
    
    @BeforeEach
    void setup() {
        clearInvocations(facilityService);
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

        mockMvc.perform(get(root))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-management"))
            .andExpect(model().attribute("facilities", ft));
        verify(facilityService, times(1)).list();
    }

    /**
     * ロールがADMINでは無い
     * @throws Exception
     */
    @Test
    @WithMockCustomUser(roles = {"USER"})
    public void indexTestNotAdminRole() throws Exception {
        mockMvc.perform(get(root))
            //.andDo(print())
            .andExpect(status().isForbidden());
        verify(facilityService, never()).list();
    }
}
