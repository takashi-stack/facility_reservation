package com.example.facilityreservation.web;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.service.UserManageService;
import com.example.facilityreservation.utility.MockUser;
import com.example.facilityreservation.utility.WithMockCustomUser;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class UserInfoManageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserManageService userManageService;
    
    @BeforeEach
    void setup() {
        clearInvocations(userManageService);
    }

    /**
     * 正常ケース
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void indexTest() throws Exception {
        List<UserEntity> mockValue = new ArrayList<>();
        mockValue.add(MockUser.genMockRootUserEntity());
        when(userManageService.list()).thenReturn(mockValue);

        mockMvc.perform(get("/admin/user"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-management"))
            .andExpect(model().attribute("users", mockValue));
        verify(userManageService, times(1)).list();
    }
    
    /**
     * ADMINユーザー以外
     * @throws Exception
     */
    @Test
    @WithMockCustomUser(roles = {"NONE"})
    public void indexTestNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/user"))
            //.andDo(print())
            .andExpect(status().isForbidden());
        verify(userManageService, never()).list();
    }
}
