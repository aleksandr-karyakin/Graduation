package com.company.project.web.ajax;

import com.company.project.web.AbstractUsersController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.company.project.model.User;
import com.company.project.to.UserTo;
import com.company.project.util.UserUtil;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/ajax/admin/users")
public class AdminUIController extends AbstractUsersController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(@PathVariable int id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        checkModificationAllowed(id);
        service.delete(id);
    }

    @PostMapping
    public void createOrUpdate(@Valid UserTo userTo) {
        if (userTo.isNew()) {
            service.create(UserUtil.createNewFromTo(userTo));
        } else {
            checkModificationAllowed(userTo.getId());
            service.update(userTo);
        }
    }

    @PostMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        checkModificationAllowed(id);
        service.enable(id, enabled);
    }

    @PostMapping("/{id}/changeRoles")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void appointManager(@PathVariable int id) {
        checkModificationAllowed(id);
        service.changeRoles(id);
    }
}
