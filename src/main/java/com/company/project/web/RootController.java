package com.company.project.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import com.company.project.AuthorizedUser;
import com.company.project.to.UserTo;
import com.company.project.util.UserUtil;

import javax.validation.Valid;

import static com.company.project.util.ValidationUtil.assureIdConsistent;
import static com.company.project.util.ValidationUtil.checkNew;

@Controller
public class RootController extends AbstractUsersController {

    @GetMapping("/")
    public String root() {
        return "redirect:restaurants";
    }

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(ModelMap model, @AuthenticationPrincipal AuthorizedUser authUser) {
        model.addAttribute("userTo", authUser.getUserTo());
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid UserTo userTo, BindingResult result, SessionStatus status, @AuthenticationPrincipal AuthorizedUser authUser) {
        if (result.hasErrors()) {
            return "profile";
        }
        checkModificationAllowed(authUser.getId());
        assureIdConsistent(userTo, authUser.getId());
        service.update(userTo);
        authUser.update(userTo);
        status.setComplete();
        return "redirect:restaurants";
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("userTo", new UserTo());
        model.addAttribute("register", true);
        return "profile";
    }

    @PostMapping("/register")
    public String saveRegister(@Valid UserTo userTo, BindingResult result, SessionStatus status, ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("register", true);
            return "profile";
        }
        checkNew(userTo);
        service.create(UserUtil.createNewFromTo(userTo));
        status.setComplete();
        return "redirect:login?message=app.registered&username=" + userTo.getEmail();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public String users() {
        return "users";
    }

    @GetMapping("/restaurants")
    public String restaurants() {
        return "restaurants";
    }

    @GetMapping("/manager/restaurants")
    public String managerRestaurants() {
        return "manager/restaurants";
    }

    @GetMapping("/manager/restaurants/{id}/dishes")
    public String managerDishes() {
        return "manager/dishes";
    }
}
