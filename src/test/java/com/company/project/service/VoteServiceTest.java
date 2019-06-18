package com.company.project.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import com.company.project.ActiveDbProfileResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static com.company.project.data.RestaurantTestData.REST1_ID;
import static com.company.project.data.RestaurantTestData.REST2_ID;
import static com.company.project.data.UserTestData.USER_ID;

@SpringJUnitConfig(locations = {"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public class VoteServiceTest {

    @Autowired
    private VoteService service;

    @Test
    @Transactional
    public void vote() {
        assertThat(1).isEqualTo(service.getCurrentRate(REST1_ID));
        assertThat(1).isEqualTo(service.getCurrentRate(REST2_ID));
        service.vote(USER_ID, REST1_ID);
        assertThat(2).isEqualTo(service.getCurrentRate(REST1_ID));
        assertThat(1).isEqualTo(service.getCurrentRate(REST2_ID));
        service.vote(USER_ID, REST2_ID);
        assertThat(1).isEqualTo(service.getCurrentRate(REST1_ID));
        assertThat(2).isEqualTo(service.getCurrentRate(REST2_ID));
    }

    @Test
    void getCurrentRate() {
        assertThat(1).isEqualTo(service.getCurrentRate(REST1_ID));
        assertThat(0).isEqualTo(service.getCurrentRate(1));
    }
}