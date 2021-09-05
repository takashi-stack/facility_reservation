package com.example.facilityreservation.web;

import static org.mockito.Mockito.clearInvocations;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.example.facilityreservation.service.InitService;
import com.example.facilityreservation.utility.WithMockCustomUser;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class InitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InitService initService;
    
    private String root = "/admin/init";
    
    @BeforeEach
    void setup() {
        clearInvocations(initService);
    }
    
    /**
     * ADMINユーザー
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void indexTest() throws Exception {
        mockMvc.perform(get(root))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("init"));
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
    }
    
    /**
     * init、正常ケース
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void initTest() throws Exception {
        mockMvc.perform(
                post(root)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
            // .andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/admin/init?complete"));
        verify(initService, times(1)).initData();
    }
    
    /**
     * init L44
     * 初期化ユーザーでは無い
     * @throws Exception
     */
    @Test
    @WithMockCustomUser(username = "junit-user")
    public void initTestNotInitUser() throws Exception {
        mockMvc.perform(
                post(root)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("init"))
            .andExpect(model().attribute("error", "ログインユーザが初期ユーザではありません。"));
        verify(initService, never()).initData();
    }
    
    /**
     * init L48
     * サーバーエラー
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void initTestServerError() throws Exception {
        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(initService).initData();

        mockMvc.perform(
                post(root)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("init"))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"));
        verify(initService, times(1)).initData();
    }
}
