package com.company.project.web.rest;

import com.company.project.TestUtil;
import com.company.project.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.company.project.model.Restaurant;
import com.company.project.service.RestaurantService;

import static com.company.project.data.UserTestData.*;
import static com.company.project.util.json.JsonUtil.writeValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.company.project.data.RestaurantTestData.*;
import static com.company.project.util.exception.ErrorType.VALIDATION_ERROR;
import static com.company.project.web.handler.ExceptionInfoHandler.EXCEPTION_DUPLICATE_NAME;

class RestaurantRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = RestaurantRestController.REST_URL + '/';

    @Autowired
    private RestaurantService service;

    @Test
    void testCreate() throws Exception {
        Restaurant expected = new Restaurant(null, "New restaurant");

        ResultActions action = mockMvc.perform(post(REST_URL)
                .with(TestUtil.userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(expected)))
                .andExpect(status().isCreated());

        Restaurant returned = TestUtil.readFromJson(action, Restaurant.class);
        expected.setId(returned.getId());

        assertMatch(returned, expected);
        assertMatch(service.getAllForUser(), REST2, REST1, expected);
    }

    @Test
    void testGetUnauthorized() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetForbidden() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(TestUtil.userHttpBasic(USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdate() throws Exception {
        Restaurant updated = new Restaurant(REST1_ID, "new Restaurant");

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + REST1_ID)
                .with(TestUtil.userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isNoContent());

        assertMatch(service.get(REST1_ID, MANAGER_ID), updated);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + REST1_ID)
                .with(TestUtil.userHttpBasic(MANAGER)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(service.getAllForUser(), REST2);
    }

    @Test
    void testGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + REST1_ID)
                .with(TestUtil.userHttpBasic(MANAGER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(REST1));
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + 123)
                .with(TestUtil.userHttpBasic(MANAGER)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(TestUtil.userHttpBasic(MANAGER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(REST2, REST1));
    }

    @Test
    void testCreateInvalid() throws Exception {
        Restaurant expected = new Restaurant(null, "");
        mockMvc.perform(post(REST_URL)
                .with(TestUtil.userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(expected)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        Restaurant updated = new Restaurant(REST1_ID, "");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + REST1_ID)
                .with(TestUtil.userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    @Transactional
    void testCreateDuplicate() throws Exception {
        Restaurant expected = new Restaurant(null, "McDonalds");
        mockMvc.perform(post(REST_URL)
                .with(TestUtil.userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(expected)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_NAME));
    }

    @Test
    @Transactional
    void testUpdateDuplicate() throws Exception {
        Restaurant updated = new Restaurant(REST1_ID, "KFC");
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + REST1_ID)
                .with(TestUtil.userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_NAME));
    }
}
