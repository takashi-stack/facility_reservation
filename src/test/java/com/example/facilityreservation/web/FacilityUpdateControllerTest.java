package com.example.facilityreservation.web;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.FacilityTypeEntity;
import com.example.facilityreservation.domain.entity.UserEntity;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.service.FacilityService;
import com.example.facilityreservation.service.FacilityTypeService;
import com.example.facilityreservation.utility.MockFacility;
import com.example.facilityreservation.utility.WithMockCustomUser;
import com.example.facilityreservation.web.form.FacilityForm;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class FacilityUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @MockBean
    private FacilityTypeService facilityTypeService;

    private String root = "/admin/facility/";
    
    private FacilityEntity mockFacilityEntity = MockFacility.genFacilityEntity();

    private List<FacilityTypeEntity> mockFacilityTypes = MockFacility.genMockFacilityTypes();

    @BeforeEach
    void setup() {
        clearInvocations(facilityService);
        clearInvocations(facilityTypeService);
        when(facilityTypeService.list()).thenReturn(mockFacilityTypes);
    }

    /**
     * index
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void indexTest() throws Exception {
        when(facilityService.findById(7)).thenReturn(mockFacilityEntity);
        
        mockMvc.perform(get(root + "/update/7"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attributeExists("facilityForm"))
            .andExpect(model().attribute("types", mockFacilityTypes));
        verify(facilityService, times(1)).findById(7);
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * index、ADMINユーザー以外
     * @throws Exception
     */
    @Test
    @WithMockCustomUser(roles = {"USER"})
    public void indexTestNotAdmin() throws Exception {
        when(facilityService.findById(7)).thenReturn(mockFacilityEntity);
        
        mockMvc.perform(get(root + "/update/7"))
            //.andDo(print())
            .andExpect(status().isForbidden());
        verify(facilityService, never()).findById(7);
        verify(facilityTypeService, never()).list();
    }

    /**
     * update_redo
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_redoTest() throws Exception {
        FacilityForm form = genForm(8, "テスト", 1, 1);

        mockMvc.perform(
                post(root + "/update/8?redo")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes));
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * update_confirm
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_confirmTest() throws Exception {
        FacilityForm form = genForm(9, "テスト", 1, 1);

        mockMvc.perform(
                post(root + "/update/9?update_confirm")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update-check"))
            .andExpect(model().attribute("types", mockFacilityTypes));
        verify(facilityService, times(1)).updateConfirm(9);
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * update_confirm L73
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_confirmTestHasErrorCap() throws Exception {
        FacilityForm form = genForm(9, "テスト", 1, 0);

        mockMvc.perform(
                post(root + "/update/9?update_confirm")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().hasErrors());
        verify(facilityService, never()).updateConfirm(9);
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * update_confirm L81
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_confirmThrowBusinessException() throws Exception {
        FacilityForm form = genForm(10, "テスト", 1, 1);

        doThrow(new BusinessException("業務エラー"))
        .when(facilityService).updateConfirm(10);

        mockMvc.perform(
                post(root + "/update/10?update_confirm")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().attribute("error", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(facilityService, times(1)).updateConfirm(10);
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * update_confirm L84
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_confirmTestThrowException() throws Exception {
        FacilityForm form = genForm(11, "テスト", 1, 1);

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(facilityService).updateConfirm(11);

        mockMvc.perform(
                post(root + "/update/11?update_confirm")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(facilityService, times(1)).updateConfirm(11);
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * update
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void updateTest() throws Exception {
        FacilityForm form = genForm(9, "テスト", 1, 1);

        mockMvc.perform(
                post(root + "/update/9?complete")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/admin/facility/update/?complete"));
        verify(facilityService, times(1)).update(eq(form), (UserEntity) any(), eq(9));
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * update L93
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void updateTestHasErrorName() throws Exception {
        FacilityForm form = genForm(10, "テストテストテストテスト", 1, 1);

        mockMvc.perform(
                post(root + "/update/10?complete")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().hasErrors());
        verify(facilityService, never()).update(eq(form), (UserEntity) any(), eq(10));
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * update L101
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void updateTestThrowBusinessException() throws Exception {
        FacilityForm form = genForm(11, "テスト", 1, 1);
        
        doThrow(new BusinessException("業務エラー"))
        .when(facilityService).update(eq(form), (UserEntity) any(), eq(11));

        mockMvc.perform(
                post(root + "/update/11?complete")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().attribute("error", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(facilityService, times(1)).update(eq(form), (UserEntity) any(), eq(11));
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * update L104
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void updateTestThrowException() throws Exception {
        FacilityForm form = genForm(12, "テスト", 1, 1);

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(facilityService).update(eq(form), (UserEntity) any(), eq(12));

        mockMvc.perform(
                post(root + "/update/12?complete")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(facilityService, times(1)).update(eq(form), (UserEntity) any(), eq(12));
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * update_completion
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void update_completionTest() throws Exception {
        mockMvc.perform(get(root + "/update?complete"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("common-completion"))
            .andExpect(model().attribute("message", "施設情報の更新"));
    }
    
    /**
     * delete
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void deleteTest() throws Exception {
        FacilityForm form = genForm(9, "テスト", 1, 1);

        mockMvc.perform(
                post(root + "/delete/9?complete")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/admin/facility/delete/?complete"));
        verify(facilityService, times(1)).deleteById(9);
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * delete L154
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void deleteTestHasErrorTypeId() throws Exception {
        FacilityForm form = genForm(10, "テスト", 0, 1);

        mockMvc.perform(
                post(root + "/delete/10?complete")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().hasErrors());
        verify(facilityService, never()).deleteById(10);
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * delete L162
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void deleteTestThrowBusinessException() throws Exception {
        FacilityForm form = genForm(11, "テスト", 1, 1);
        
        doThrow(new BusinessException("業務エラー"))
        .when(facilityService).deleteById(11);

        mockMvc.perform(
                post(root + "/delete/11?complete")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().attribute("error", "業務エラー"))
            .andExpect(model().hasNoErrors())
            .andExpect(content().string(containsString("業務エラー")));
        verify(facilityService, times(1)).deleteById(11);
        verify(facilityTypeService, times(1)).list();
    }

    /**
     * delete L165
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void deleteTestThrowException() throws Exception {
        FacilityForm form = genForm(12, "テスト", 1, 1);

        doThrow(new DataIntegrityViolationException("サーバーエラー"))
        .when(facilityService).deleteById(12);

        mockMvc.perform(
                post(root + "/delete/12?complete")
                .flashAttr("facilityForm", form)  
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("facility-update"))
            .andExpect(model().attribute("facilityForm", form))
            .andExpect(model().attribute("types", mockFacilityTypes))
            .andExpect(model().attribute("error", "システムエラーが発生しました。"))
            .andExpect(model().hasNoErrors());
        verify(facilityService, times(1)).deleteById(12);
        verify(facilityTypeService, times(1)).list();
    }
    
    /**
     * delete_completion
     * @throws Exception
     */
    @Test
    @WithMockCustomUser
    public void delete_completionTest() throws Exception {
        mockMvc.perform(get(root + "/delete?complete"))
            //.andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("common-completion"))
            .andExpect(model().attribute("message", "施設情報の削除"));
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
