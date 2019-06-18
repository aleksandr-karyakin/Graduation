package com.company.project.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.company.project.model.AbstractBaseEntity;
import com.company.project.service.UserService;
import com.company.project.util.exception.ModificationRestrictionException;
import com.company.project.web.validator.UniqueMailValidator;

public abstract class AbstractUsersController {

    private boolean modificationRestriction;

    @Autowired
    protected UserService service;

    @Autowired
    private UniqueMailValidator emailValidator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.addValidators(emailValidator);
    }

    @Autowired
    private void setEnvironment(Environment environment) {
        modificationRestriction = environment.acceptsProfiles(Profiles.of("heroku"));
    }

    protected void checkModificationAllowed(int id) {
        if (modificationRestriction && id < AbstractBaseEntity.START_SEQ + 2) {
            throw new ModificationRestrictionException();
        }
    }
}