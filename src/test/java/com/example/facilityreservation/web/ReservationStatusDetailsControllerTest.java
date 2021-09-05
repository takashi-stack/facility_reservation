package com.example.facilityreservation.web;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.ReservationService;
import com.example.facilityreservation.utility.MockFacility;
import com.example.facilityreservation.utility.MockReserveCell;
import com.example.facilityreservation.utility.WithMockCustomUser;
import com.example.facilityreservation.web.bean.ReserveCell;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class ReservationStatusDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private FacilityService facilityService;

    @MockBean
    private ReservationService reservationService;

    private FacilityEntity mockFacilityEntity = MockFacility.genFacilityEntity();

    @BeforeEach
    void setup() {
        clearInvocations(facilityService);
        clearInvocations(reservationService);
    }

    /**
     * 正常ケース
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void indexTest() throws Exception {
        int year = 2021;
        int month = 1;
        ReserveCell mockReserveCell = MockReserveCell.genReserveCell(year, month, 1);
        ReserveCell[][] rcList =  {{mockReserveCell}, {mockReserveCell}};
        when(facilityService.findById(20)).thenReturn(mockFacilityEntity);
        when(reservationService.reservationList(mockFacilityEntity, year, month))
        .thenReturn(rcList);

        mockMvc.perform(
                get("/reservation/20")
                .param("y", String.valueOf(year))
                .param("m", String.valueOf(month))
                )
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("reservation-status-details"))
            .andExpect(model().attribute("facility", mockFacilityEntity))
            .andExpect(model().attribute("y", year))
            .andExpect(model().attribute("m", month))
            .andExpect(model().attribute("next_y", year))
            .andExpect(model().attribute("next_m", month + 1))
            .andExpect(model().attribute("calender", rcList));
    }
}
