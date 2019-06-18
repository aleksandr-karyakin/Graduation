package com.company.project.model;

import com.company.project.web.validator.View;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "dishes")
public class Dish extends AbstractNamedEntity {

    @Column
    @NotNull
    @Range(min = 1, max = 10000)
    private Integer price;

    @Column
    private boolean enabled;

    @JoinColumn(name = "restaurant_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull(groups = View.Persist.class)
    @JsonBackReference
    private Restaurant restaurant;

    public Dish() {
    }

    public Dish(Integer id, String name, Integer price, boolean enabled) {
        super(id, name);
        this.price = price;
        this.enabled = enabled;
    }

    public Dish(Integer id, String name, Integer price, boolean enabled, Restaurant restaurant) {
        super(id, name);
        this.price = price;
        this.enabled = enabled;
        this.restaurant = restaurant;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "price=" + price +
                ", enabled=" + enabled +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
