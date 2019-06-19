package com.company.project.web.rest;

import com.company.project.model.Role;
import com.company.project.model.User;
import com.company.project.service.UserService;
import com.company.project.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static com.company.project.TestUtil.readFromJson;
import static com.company.project.TestUtil.userHttpBasic;
import static com.company.project.data.UserTestData.*;
import static com.company.project.util.exception.ErrorType.VALIDATION_ERROR;
import static com.company.project.util.json.JsonUtil.writeValue;
import static com.company.project.web.handler.ExceptionInfoHandler.EXCEPTION_DUPLICATE_EMAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestController.REST_URL + '/';

    @Autowired
    private UserService service;

    @Test
    void testCreate() throws Exception {
        User expected = new User(null, "New", "new@gmail.com", "newPass", Role.ROLE_USER, Role.ROLE_ADMIN);

        ResultActions action = mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(expected, "newPass")))
                .andDo(print())
                .andExpect(status().isCreated());

        User returned = readFromJson(action, User.class);
        expected.setId(returned.getId());

        assertMatch(returned, expected);
        assertMatch(service.getAll(), ADMIN, MANAGER, expected, USER);
    }

    @Test
    void testCreateWithId() throws Exception {
        User userWithId = new User(USER);
        userWithId.setId(12345);
        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(userWithId)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testGetUnauthorized() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetForbidden() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdate() throws Exception {
        User updated = new User(USER);
        updated.setName("UpdatedName");

        mockMvc.perform(put(REST_URL + USER_ID)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertMatch(service.get(USER_ID), updated);
    }

    @Test
    void testUpdateWithIncorrectId() throws Exception {
        User updated = new User(USER);
        updated.setName("UpdatedName");
        mockMvc.perform(put(REST_URL + ADMIN_ID)
                .with(userHttpBasic(ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete(REST_URL + USER_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(service.getAll(), ADMIN, MANAGER);
    }

    @Test
    void testGet() throws Exception {
        mockMvc.perform(get(REST_URL + ADMIN_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(ADMIN));
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + 123)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void testGetByEmail() throws Exception {
        mockMvc.perform(get(REST_URL + "by?email=" + USER.getEmail())
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(USER));
    }

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(ADMIN, MANAGER, USER));
    }

    @Test
    void testGetAllManagers() throws Exception {
        mockMvc.perform(get(REST_URL + "managers")
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(ADMIN, MANAGER));
    }

    @Test
    void testCreateInvalid() throws Exception {
        User expected = new User(null, null, "", "newPass", Role.ROLE_USER);
        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(writeValue(expected)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        User updated = new User(USER);
        updated.setName("");
        mockMvc.perform(put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    @Transactional
    void testCreateDuplicate() throws Exception {
        User expected = new User(null, "New", "user@yandex.ru", "newPass", Role.ROLE_USER);
        mockMvc.perform(post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(expected, "newPass")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }

    @Test
    @Transactional
    void testUpdateDuplicate() throws Exception {
        User updated = new User(USER);
        updated.setEmail("admin@gmail.com");
        mockMvc.perform(put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(jsonWithPassword(updated, "password")))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }
}