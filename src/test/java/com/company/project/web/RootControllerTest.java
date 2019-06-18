package com.company.project.web;

import com.company.project.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.company.project.data.RestaurantTestData.REST1_ID;
import static com.company.project.data.UserTestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RootControllerTest extends AbstractControllerTest {

    @Test
    void testUsers() throws Exception {
        mockMvc.perform(get("/users")
                .with(TestUtil.userAuth(ADMIN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/users.jsp"));
    }

    @Test
    void testForbidden() throws Exception {
        mockMvc.perform(get("/users")
                .with(TestUtil.userAuth(USER)))
                .andDo(print())
                .andExpect(view().name("exception/exception"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/exception/exception.jsp"));
    }

    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void testRestaurants() throws Exception {
        mockMvc.perform(get("/restaurants")
                .with(TestUtil.userAuth(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("restaurants"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/restaurants.jsp"));
    }

    @Test
    void testManagerRestaurants() throws Exception {
        mockMvc.perform(get("/manager/restaurants")
                .with(TestUtil.userAuth(MANAGER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("manager/restaurants"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/manager/restaurants.jsp"));
    }

    @Test
    void testManagerDishes() throws Exception {
        mockMvc.perform(get("/manager/restaurants/" + REST1_ID + "/dishes")
                .with(TestUtil.userAuth(MANAGER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("manager/dishes"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/manager/dishes.jsp"));
    }

    @Test
    void testResources() throws Exception {
        mockMvc.perform(get("/resources/css/style.css"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.valueOf("text/css")))
                .andExpect(status().isOk());
    }
}