package com.company.project.web.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.company.project.model.Dish;
import com.company.project.service.DishService;
import com.company.project.web.AbstractControllerTest;

import java.util.List;

import static com.company.project.data.RestaurantTestData.REST1_ID;
import static com.company.project.data.UserTestData.MANAGER;
import static com.company.project.data.UserTestData.USER;
import static com.company.project.util.json.JsonUtil.writeValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.company.project.TestUtil.readFromJson;
import static com.company.project.TestUtil.userHttpBasic;
import static com.company.project.data.DishTestData.*;
import static com.company.project.util.exception.ErrorType.VALIDATION_ERROR;

class DishRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = DishRestController.REST_URL + '/' + REST1_ID + "/dishes/";

    @Autowired
    private DishService service;

    @Test
    void testCreate() throws Exception {
        Dish expected = new Dish(null, "new dish", 555, true);

        ResultActions action = mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(expected)))
                .andExpect(status().isCreated());

        Dish returned = readFromJson(action, Dish.class);
        expected.setId(returned.getId());

        assertMatch(returned, expected);
        assertMatch(service.getAllForManager(REST1_ID), DISH1, DISH2, expected);
        assertMatch(service.getAllForUser(REST1_ID), DISH1, expected);
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
        Dish updated = new Dish(DISH1_ID, "new name", 333, true);

        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DISH1_ID)
                .with(userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isNoContent());

        assertMatch(service.get(DISH1_ID, REST1_ID), updated);
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + DISH1_ID)
                .with(userHttpBasic(MANAGER)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertMatch(service.getAllForManager(REST1_ID), DISH2);
        assertMatch(service.getAllForUser(REST1_ID), List.of());
    }

    @Test
    void testGet() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + DISH1_ID)
                .with(userHttpBasic(MANAGER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(DISH1));
    }

    @Test
    void testGetNotFound() throws Exception {
        mockMvc.perform(get(REST_URL + 123)
                .with(userHttpBasic(MANAGER)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    void testGetAll() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(MANAGER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(DISH1, DISH2));
    }

    @Test
    void testCreateInvalid() throws Exception {
        Dish expected = new Dish(null, "n", 0, true);
        mockMvc.perform(post(REST_URL)
                .with(userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(expected)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }

    @Test
    void testUpdateInvalid() throws Exception {
        Dish updated = new Dish(DISH1_ID, "n", 0, true);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + DISH1_ID)
                .with(userHttpBasic(MANAGER))
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andDo(print());
    }
}
