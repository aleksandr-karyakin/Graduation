package com.company.project.data;

import org.springframework.test.web.servlet.ResultMatcher;
import com.company.project.model.Role;
import com.company.project.model.User;
import com.company.project.util.json.JsonUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.company.project.TestUtil.readFromJsonMvcResult;
import static com.company.project.TestUtil.readListFromJsonMvcResult;
import static com.company.project.model.AbstractBaseEntity.START_SEQ;

public class UserTestData {

    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int MANAGER_ID = START_SEQ + 2;

    public static final User USER = new User(USER_ID, "User", "user@yandex.ru", "password", Role.ROLE_USER);
    public static final User ADMIN = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", Role.ROLE_USER, Role.ROLE_MANAGER, Role.ROLE_ADMIN);
    public static final User MANAGER = new User(MANAGER_ID, "Manager", "manager@gmail.com", "manager", Role.ROLE_USER, Role.ROLE_MANAGER);

    public static void assertMatch(User actual, User expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "registered", "roles", "password");
    }

    public static void assertMatch(Iterable<User> actual, User... expected) {
        assertMatch(actual, List.of(expected));
    }

    public static void assertMatch(Iterable<User> actual, Iterable<User> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields("registered", "roles", "password").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(User... expected) {
        return result -> assertMatch(readListFromJsonMvcResult(result, User.class), List.of(expected));
    }

    public static ResultMatcher contentJson(User expected) {
        return result -> assertMatch(readFromJsonMvcResult(result, User.class), expected);
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }
}
