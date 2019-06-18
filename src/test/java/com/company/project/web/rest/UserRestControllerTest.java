package com.company.project.web.rest;

import com.company.project.service.VoteService;
import com.company.project.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static com.company.project.TestUtil.userHttpBasic;
import static com.company.project.data.DishTestData.*;
import static com.company.project.data.RestaurantTestData.*;
import static com.company.project.data.UserTestData.USER;
import static com.company.project.data.UserTestData.USER_ID;
import static com.company.project.util.exception.ErrorType.VOTE_FORBIDDEN;
import static com.company.project.util.exception.TooLateVoteException.VOTE_TOO_LATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = UserRestController.REST_URL + '/';

    @Autowired
    private VoteService service;

    @Test
    void testGetUnauthorized() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllRestaurants() throws Exception {
        mockMvc.perform(get(REST_URL)
                .with(userHttpBasic(USER)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(REST2, REST1));
    }

    @Test
    @EnabledIf(value = "new Date().getHours()<18")
    void testVote() throws Exception {
        assertThat(1).isEqualTo(service.getCurrentRate(REST1_ID));

        mockMvc.perform(post(REST_URL + "/" + REST1_ID + "/votes")
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        assertThat(2).isEqualTo(service.getCurrentRate(REST1_ID));
    }

    @Test
    @EnabledIf(value = "new Date().getHours()>=18")
    void testVoteTooLate() throws Exception {
        mockMvc.perform(post(REST_URL + "/" + REST1_ID + "/votes")
                .with(userHttpBasic(USER))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VOTE_FORBIDDEN))
                .andExpect(detailMessage(VOTE_TOO_LATE));
    }

    @Test
    void testGetRating() throws Exception {
        ResultActions actions = mockMvc.perform(get(REST_URL + "/" + REST1_ID + "/rating")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print());
        assertThat("1").isEqualTo(actions.andReturn().getResponse().getContentAsString());

        service.vote(USER_ID, REST1_ID);

        actions = mockMvc.perform(get(REST_URL + "/" + REST1_ID + "/rating")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print());
        assertThat("2").isEqualTo(actions.andReturn().getResponse().getContentAsString());
    }

    @Test
    void getMenuByRestaurant() throws Exception {
        mockMvc.perform(get(UserRestController.REST_URL + "/" + REST2_ID + "/dishes/")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(contentJson(DISH3, DISH4));
    }
}