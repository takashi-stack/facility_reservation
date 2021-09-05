package com.example.facilityreservation.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.WebAttributes;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.AuthenticationException;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.facilityreservation.web.authentication.InValidAuthenticationException;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 正常ケース
     * @throws Exception
     */
    @Test
    public void indexTest() throws Exception {
        mockMvc.perform(get("/login"))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }
    
    /**
     * L36、正常ケース
     * @throws Exception
     */
    @Test
    public void indexErrorTest() throws Exception {
        mockMvc.perform(
        		get("/login?error"))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().hasNoErrors());
    }

    /**
     * L42
     * @throws Exception
     */
    @Test
    public void indexErrorTestHasErrorOfUsername() throws Exception {
        InValidAuthenticationException e = new InValidAuthenticationException("indexErrorTest 1.");
        e.setError("username", "Invalid username.");
        ArrayList<String> errors = new ArrayList<>();
        errors.add("Invalid username.");
        mockMvc.perform(
        		get("/login?error")
        		.sessionAttr(WebAttributes.AUTHENTICATION_EXCEPTION, e))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("errorUsername", errors));
    }
    
    /**
     * L46
     * @throws Exception
     */
    @Test
    public void indexErrorTestHasErrorOfPassword() throws Exception {
        InValidAuthenticationException e = new InValidAuthenticationException("indexErrorTest 2.");
        e.setError("password", "Invalid password.");
        ArrayList<String> errors = new ArrayList<>();
        errors.add("Invalid password.");
        mockMvc.perform(
        		get("/login?error")
        		.sessionAttr(WebAttributes.AUTHENTICATION_EXCEPTION, e))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("errorPassword", errors));
    }
    
    /**
     * L49
     * @throws Exception
     */
    @Test
    public void indexErrorTestAuthenticationServiceException() throws Exception {
    	AuthenticationServiceException e = new AuthenticationServiceException("indexErrorTest 3.");
        mockMvc.perform(
        		get("/login?error")
        		.sessionAttr(WebAttributes.AUTHENTICATION_EXCEPTION, e))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "システムエラーが発生しました。管理者にお問い合わせください。"));
    }
    
    /**
     * L52
     * @throws Exception
     */
    @Test
    public void indexErrorTestAuthenticationException() throws Exception {
 
    	/*** 分岐確認用Exceptionクラス */
    	class TestAuthenticationException extends AuthenticationException {
			private static final long serialVersionUID = 1L;

			public TestAuthenticationException(String msg) {
				super(msg);
			}}

    	String eMsg = "AuthenticationExceptionのテストです。";
    	AuthenticationException e = new TestAuthenticationException(eMsg);
        mockMvc.perform(
        		get("/login?error")
        		.sessionAttr(WebAttributes.AUTHENTICATION_EXCEPTION, e))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", eMsg));
    }
    
    /**
     * L54
     * @throws Exception
     */
    @Test
    public void indexErrorTestException() throws Exception {
    	Exception e = new Exception("サーバーエラーのテストです。");
        mockMvc.perform(
        		get("/login?error")
        		.sessionAttr(WebAttributes.AUTHENTICATION_EXCEPTION, e))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("login"))
            .andExpect(model().attribute("error", "システムエラーが発生しました。管理者にお問い合わせください。"));
    }
}

