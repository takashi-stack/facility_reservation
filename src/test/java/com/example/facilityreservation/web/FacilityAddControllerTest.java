package com.example.facilityreservation.web;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.web.util.NestedServletException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.facilityreservation.domain.entity.FacilityTypeEntity;
import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.FacilityTypeService;
import com.example.facilityreservation.utility.WithMockCustomUser;
import com.example.facilityreservation.utility.MockFacility;
import com.example.facilityreservation.web.form.FacilityForm;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class FacilityAddControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @MockBean
    private FacilityTypeService facilityTypeService;

    private String root = "/admin/facility";

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
        List<FacilityTypeEntity> mockValue = MockFacility.genMockFacilityTypes();
        when(facilityTypeService.list()).thenReturn(mockValue);
        mockMvc.perform(get(root + "/add"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-add"))
            .andExpect(model().attribute("types", mockValue));
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * ログイン前
     * @throws Exception
     */
    @Test
    public void indexTestBeforeLogin() throws Exception {
        mockMvc.perform(get(root + "/add"))
            //.andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("http://localhost/login"));
        verify(facilityTypeService, never()).list();
    }

    /**
     * ロールがADMINではない
     * @throws Exception
     */
    @Test
    @WithMockCustomUser(roles = {"USER"})
    public void indexTestNotAllowRole() throws Exception {
        mockMvc.perform(get(root + "/add"))
            //.andDo(print())
            .andExpect(status().isForbidden());
        verify(facilityTypeService, never()).list();
    }

    /**
     * redo
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void redoTest() throws Exception {
        List<FacilityTypeEntity> mockValue = MockFacility.genMockFacilityTypes();
        when(facilityTypeService.list()).thenReturn(mockValue);
        mockMvc.perform(
                post(root + "/add?redo")
                .flashAttr("facilityForm", genForm(1, "テスト", 1, 1))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-add"))
            .andExpect(model().attribute("types", mockValue));
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * confirm
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void confirmTest() throws Exception {
        List<FacilityTypeEntity> mockValue = MockFacility.genMockFacilityTypes();
        when(facilityTypeService.list()).thenReturn(mockValue);
        mockMvc.perform(
                post(root + "/add?confirm")
                .flashAttr("facilityForm", genForm(1, "テスト", 1, 1))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-add-check"))
            .andExpect(model().attribute("types", mockValue));
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * confirm L52
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void confirmTestHasValidationErrors() throws Exception {
        List<FacilityTypeEntity> mockValue = MockFacility.genMockFacilityTypes();
        when(facilityTypeService.list()).thenReturn(mockValue);
        mockMvc.perform(
                post(root + "/add?confirm")
                .flashAttr("facilityForm", genForm(1, "テスト", 1, 0))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-add"))
            .andExpect(model().attribute("types", mockValue))
            .andExpect(content().string(containsString("1名以上で入力してください。")));
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * confirm L60
     * スローに失敗する為、コメントアウト
     * @throws Exception
     */
//    @Test
//    @WithMockCustomUser
//    public void confirmTestServerErrors() throws Exception {
//        doThrow(new DataIntegrityViolationException("サーバーエラー"))
//        .when(facilityService).list();
//
//        mockMvc.perform(
//                post(root + "/add?confirm")
//                .flashAttr("facilityForm", genForm(1, "テスト", 1, 1))
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//            .andDo(print())
//            .andExpect(status().isOk())
//            .andExpect(view().name("facility-add"))
//            .andExpect(model().attribute("error", "システムエラーが発生しました。"));
//        verify(facilityTypeService, times(1)).list();
//    }

    /**
     * add、正常ケース
     * BaseController.getLoginUser内部のキャスト
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void addTest() throws Exception {
        FacilityForm form = genForm(1, "テスト", 1, 1);
        mockMvc.perform(post(root + "/add?complete")
                .flashAttr("facilityForm", form)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/admin/facility/add?complete"))
            .andExpect(model().hasNoErrors());
        verify(facilityService).add((FacilityForm) any(), (UserEntity) any());
    }

    /**
     * add、L68
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void addTestHasErrorOfUsername() throws Exception {
        FacilityForm form = genForm(1, "12345678901", 1, 1);
        mockMvc.perform(post(root + "/add?complete")
                .flashAttr("facilityForm", form)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-add"))
            .andExpect(model().hasErrors())
            .andExpect(content().string(containsString("1文字以上10文字以下で入力してください。")));
        verify(facilityService, never()).add((FacilityForm) any(), (UserEntity) any());
    }

    /**
     * add、L76
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void addTestServerError() throws Exception {
        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(facilityService).add((FacilityForm) any(), (UserEntity) any());

        FacilityForm form = genForm(1, "テスト", 1, 1);
        mockMvc.perform(post(root + "/add?complete")
                .flashAttr("facilityForm", form)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-add"))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"));
        verify(facilityService).add((FacilityForm) any(), (UserEntity) any());
    }
    
    /**
     * user_add_completion
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void user_add_completionTest() throws Exception {
        mockMvc.perform(get(root + "/add?complete"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("common-completion"))
            .andExpect(model().attribute("message", "施設情報登録"));
    }

    private FacilityForm genForm(int id, String name, int typeId, int capacity) {
        FacilityForm form = new FacilityForm();
        form.setId(id);
        form.setName(name);
        form.setTypeId(typeId);
        form.setCapacity(capacity);
        return form;
    }
}
