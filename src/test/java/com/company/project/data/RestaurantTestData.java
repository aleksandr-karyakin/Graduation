package com.company.project.data;

import org.springframework.test.web.servlet.ResultMatcher;
import com.company.project.model.Restaurant;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.company.project.TestUtil.readFromJsonMvcResult;
import static com.company.project.TestUtil.readListFromJsonMvcResult;
import static com.company.project.model.AbstractBaseEntity.START_SEQ;

public class RestaurantTestData {

    public static final int REST1_ID = START_SEQ + 3;
    public static final int REST2_ID = START_SEQ + 4;

    public static final Restaurant REST1 = new Restaurant(REST1_ID, "McDonalds");
    public static final Restaurant REST2 = new Restaurant(REST2_ID, "KFC");

    public static void assertMatch(Restaurant actual, Restaurant expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "user", "dishes");
    }

    public static void assertMatch(Iterable<Restaurant> actual, Restaurant... expected) {
        assertMatch(actual, List.of(expected));
    }

    public static void assertMatch(Iterable<Restaurant> actual, Iterable<Restaurant> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields("user", "dishes").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Restaurant... expected) {
        return result -> assertMatch(readListFromJsonMvcResult(result, Restaurant.class), List.of(expected));
    }

    public static ResultMatcher contentJson(Restaurant expected) {
        return result -> assertMatch(readFromJsonMvcResult(result, Restaurant.class), expected);
    }
}
