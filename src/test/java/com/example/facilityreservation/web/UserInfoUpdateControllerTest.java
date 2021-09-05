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

import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.domain.enums.PermissionLevel;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.service.UserManageService;
import com.example.facilityreservation.utility.MockUser;
import com.example.facilityreservation.utility.WithMockCustomUser;
import com.example.facilityreservation.web.form.UserInfoForm;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class UserInfoUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserManageService userManageService;
    
    private String root = "/admin/user";
    
    private UserEntity mockUserEntity = MockUser.genMockRootUserEntity();
    
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
        when(userManageService.findById("1")).thenReturn(mockUserEntity);

        mockMvc.perform(get(root + "/update/1"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attributeExists("userInfoForm"));
        verify(userManageService, times(1)).findById("1");
    }
    
    /**
     * ADMINユーザー以外
     * @throws Exception
     */
    @Test
    @WithMockCustomUser(roles = {"USER"})
    public void indexTestNotAdmin() throws Exception {
        mockMvc.perform(get(root + "/update/1"))
            //.andDo(print())
            .andExpect(status().isForbidden());
        verify(userManageService, never()).list();
    }
    
    /**
     * update_redo
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_redoTest() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        mockMvc.perform(
                post(root + "/update/1?redo")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form));
    }
    
    /**
     * update_confirm
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_confirmTest() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        mockMvc.perform(
                post(root + "/update/1?update_confirm")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update-check"));
        verify(userManageService, times(1)).saveConfirm((UserEntity) any());
    }
    
    /**
     * update_confirm L66
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_confirmTestHasErrorUsername() throws Exception {
        UserInfoForm form = genForm("1", "roo", "admin00", PermissionLevel.ADMIN, "");

        mockMvc.perform(
                post(root + "/update/1?update_confirm")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().hasErrors());
        verify(userManageService, never()).saveConfirm((UserEntity) any());
    }
    
    /**
     * update_confirm L75
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_confirmThrowBusinessException() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        doThrow(new BusinessException("業務エラー"))
        .when(userManageService).saveConfirm((UserEntity) any());

        mockMvc.perform(
                post(root + "/update/1?update_confirm")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().attribute("error", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(userManageService, times(1)).saveConfirm((UserEntity) any());
    }

    /**
     * update_confirm L78
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_confirmTestThrowException() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(userManageService).saveConfirm((UserEntity) any());

        mockMvc.perform(
                post(root + "/update/1?update_confirm")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(userManageService, times(1)).saveConfirm((UserEntity) any());
    }
    
    /**
     * update
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void updateTest() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        mockMvc.perform(
                post(root + "/update/1?complete")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/admin/user/update/1?complete"));
        verify(userManageService, times(1)).save((UserEntity) any());
    }
    
    /**
     * update L87
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void updateTestHasErrorName() throws Exception {
        UserInfoForm form = genForm("1", "roo", "admin00", PermissionLevel.ADMIN, "");

        mockMvc.perform(
                post(root + "/update/1?complete")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().hasErrors());
        verify(userManageService, never()).save((UserEntity) any());
    }
    
    /**
     * update L96
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void updateTestThrowBusinessException() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");
        
        doThrow(new BusinessException("業務エラー"))
        .when(userManageService).save((UserEntity) any());

        mockMvc.perform(
                post(root + "/update/1?complete")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().attribute("error", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(userManageService, times(1)).save((UserEntity) any());
    }

    /**
     * update L99
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void updateTestThrowException() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(userManageService).save((UserEntity) any());

        mockMvc.perform(
                post(root + "/update/1?complete")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(userManageService, times(1)).save((UserEntity) any());
    }
    
    /**
     * update_completion
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_completionTest() throws Exception {
        mockMvc.perform(get(root + "/update/1?complete"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("common-completion"))
            .andExpect(model().attribute("message", "ユーザ情報の更新"));
    }
    
    /**
     * delete_redo
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void delete_redoTest() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        mockMvc.perform(
                post(root + "/delete/1?redo")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
        //.andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("user-update"));
    }
    
    /**
     * delete_confirm
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void delete_confirmTest() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");
        when(userManageService.findById("1")).thenReturn(mockUserEntity);

        mockMvc.perform(
                post(root + "/update/1?delete_confirm")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(view().name("user-delete-check"));
        verify(userManageService, times(1)).deleteConfirm("1");
        verify(userManageService, times(1)).findById("1");
    }

    /**
     * delete_confirm L136
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void delete_confirmThrowBusinessException() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        doThrow(new BusinessException("業務エラー"))
        .when(userManageService).deleteConfirm("1");

        mockMvc.perform(
                post(root + "/update/1?delete_confirm")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().attribute("error", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(userManageService, never()).findById("1");
        verify(userManageService, times(1)).deleteConfirm("1");
    }

    /**
     * delete_confirm L138
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void delete_confirmTestThrowException() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(userManageService).findById("1");

        mockMvc.perform(
                post(root + "/update/1?delete_confirm")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(userManageService, times(1)).findById("1");
        verify(userManageService, times(1)).deleteConfirm("1");
    }
    
    /**
     * delete
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void deleteTest() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        mockMvc.perform(
                post(root + "/delete/1?complete")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/admin/user/delete/1?complete"));
        verify(userManageService, times(1)).deleteById("1");
    }
    
    /**
     * delete L153
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void deleteTestThrowBusinessException() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");
        
        doThrow(new BusinessException("業務エラー"))
        .when(userManageService).deleteById("1");

        mockMvc.perform(
                post(root + "/delete/1?complete")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().attribute("error", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(userManageService, times(1)).deleteById("1");
    }

    /**
     * delete L156
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void deleteTestThrowException() throws Exception {
        UserInfoForm form = genForm("1", "root", "admin00", PermissionLevel.ADMIN, "");

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(userManageService).deleteById("1");

        mockMvc.perform(
                post(root + "/delete/1?complete")
                .flashAttr("userInfoForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("user-update"))
            .andExpect(model().attribute("userInfoForm", form))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(userManageService, times(1)).deleteById("1");
    }
    
    /**
     * delete_completion
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void delete_completionTest() throws Exception {
        mockMvc.perform(get(root + "/delete/1?complete"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("common-completion"))
            .andExpect(model().attribute("message", "ユーザ情報の削除"));
    }

    private static UserInfoForm genForm(String userId, String usename, String passwd, PermissionLevel level, String note) {
        UserInfoForm form = new UserInfoForm();
        form.setId(userId);
        form.setUsername(usename);
        form.setPassword(passwd);
        form.setPermission(level.ordinal());
        form.setNote(note);
        return form;
    }
}
