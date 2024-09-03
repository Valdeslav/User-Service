package com.valdeslav.user.integration;

import com.valdeslav.user.UserApplication;
import com.valdeslav.user.model.Role;
import com.valdeslav.user.model.User;
import com.valdeslav.user.model.enums.Roles;
import com.valdeslav.user.repository.RoleRepository;
import com.valdeslav.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.valdeslav.user.integration.Constants.*;
import static com.valdeslav.user.TestUtils.createJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = UserApplication.class)
@AutoConfigureMockMvc
public class AuthTest implements ApplicationContextAware {
    private static ApplicationContext context;

    @Autowired
    private MockMvc mvc;

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AuthTest.context = applicationContext;
    }

    @BeforeAll
    public static void init() {
        RoleRepository roleRepository = getBean(RoleRepository.class);
        UserRepository userRepository = getBean(UserRepository.class);
        Role role = roleRepository.findFirstByName(Roles.USER.name()).orElseThrow(() -> new RuntimeException("Role USER should exist."));
        User user = User.builder()
                .username(USERNAME)
                .password(new BCryptPasswordEncoder().encode(PASSWORD))
                .email(EMAIL)
                .roles(Collections.singleton(role))
                .build();
        userRepository.save(user);
    }

    @Test
    public void loginSuccessTest() throws Exception {
        mvc.perform(post("api/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createJsonString())
        )
    }
}
