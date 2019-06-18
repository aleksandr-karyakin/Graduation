package com.company.project;

import org.springframework.test.context.ActiveProfilesResolver;

public class ActiveDbProfileResolver implements ActiveProfilesResolver {

    @Override
    public String[] resolve(Class<?> testClass) {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            return new String[]{"hsqldb"};
        } catch (ClassNotFoundException ex) {
            try {
                Class.forName("org.postgresql.Driver");
                return new String[]{"postgres"};
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Could not find DB driver");
            }
        }
    }
}