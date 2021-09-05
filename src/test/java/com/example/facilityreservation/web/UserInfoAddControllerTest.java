package com.example.facilityreservation.web;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.facilityreservation.service.UserManageService;
import com.example.facilityreservation.utility.WithMockCustomUser;
import com.example.facilityreservation.web.form.UserInfoForm;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class UserInfoAddControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserManageService userManageService;
    
    private String root = "/admin/user";
    
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
        mockMvc.perform(get(root + "/add"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-add"))
            .andExpect(model().attributeExists("userInfoForm"));
    }
    
    /**
     * ADMINユーザー以外
     * @throws Exception
     */
    @Test
    @WithMockCustomUser(roles = {"USER"})
    public void indexTestNotAdmin() throws Exception {
        mockMvc.perform(get(root + "/add"))
            //.andDo(print())
            .andExpect(status().isForbidden());
    }
    
    /**
     * index redo
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void indexRedoTest() throws Exception {
        mockMvc.perform(
                post(root + "/add?redo")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-add"))
            .andExpect(model().attributeExists("userInfoForm"));
    }
}
