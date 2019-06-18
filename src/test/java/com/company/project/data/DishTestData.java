package com.company.project.data;

import org.springframework.test.web.servlet.ResultMatcher;
import com.company.project.model.Dish;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.company.project.TestUtil.readFromJsonMvcResult;
import static com.company.project.TestUtil.readListFromJsonMvcResult;
import static com.company.project.model.AbstractBaseEntity.START_SEQ;

public class DishTestData {

    public static final int DISH1_ID = START_SEQ + 5;
    public static final int DISH2_ID = START_SEQ + 6;
    public static final int DISH3_ID = START_SEQ + 7;
    public static final int DISH4_ID = START_SEQ + 8;

    public static final Dish DISH1 = new Dish(DISH1_ID, "burger", 100, true);
    public static final Dish DISH2 = new Dish(DISH2_ID, "coffee", 50, false);
    public static final Dish DISH3 = new Dish(DISH3_ID, "chicken", 120, true);
    public static final Dish DISH4 = new Dish(DISH4_ID, "tea", 60, true);

    public static void assertMatch(Dish actual, Dish expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "restaurant");
    }

    public static void assertMatch(Iterable<Dish> actual, Dish... expected) {
        assertMatch(actual, List.of(expected));
    }

    public static void assertMatch(Iterable<Dish> actual, Iterable<Dish> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields("restaurant").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Dish... expected) {
        return result -> assertMatch(readListFromJsonMvcResult(result, Dish.class), List.of(expected));
    }

    public static ResultMatcher contentJson(Dish expected) {
        return result -> assertMatch(readFromJsonMvcResult(result, Dish.class), expected);
    }
}