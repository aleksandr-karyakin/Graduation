package com.company.project.service;

import com.company.project.AuthorizedUser;
import com.company.project.model.Role;
import com.company.project.model.User;
import com.company.project.repository.UserRepository;
import com.company.project.to.UserTo;
import com.company.project.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

import static com.company.project.util.UserUtil.prepareToSave;
import static com.company.project.util.ValidationUtil.checkNotFound;
import static com.company.project.util.ValidationUtil.checkNotFoundWithId;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Sort SORT_NAME_EMAIL = new Sort(Sort.Direction.ASC, "name", "email");

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        return repository.save(prepareToSave(user, passwordEncoder));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void update(User user) {
        Assert.notNull(user, "user must not be null");
        get(user.getId());
        repository.save(prepareToSave(user, passwordEncoder));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void update(UserTo userTo) {
        Assert.notNull(userTo, "user must not be null");
        User user = get(userTo.getId());
        repository.save(prepareToSave(UserUtil.updateFromTo(user, userTo), passwordEncoder));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id) != 0, id);
    }

    @Override
    public User get(int id) {
        return checkNotFoundWithId(repository.findUserById(id), id);
    }

    @Override
    public User getByEmail(String email) {
        Assert.notNull(email, "email must not be null");
        return checkNotFound(repository.findByEmail(email), "email=" + email);
    }

    @Override
    @Cacheable("users")
    public List<User> getAll() {
        return repository.findAll(SORT_NAME_EMAIL);
    }

    @Override
    public List<User> getAllManagers() {
        return repository.findAllByRoles(Role.ROLE_MANAGER, SORT_NAME_EMAIL);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void enable(int id, boolean flag) {
        User user = get(id);
        user.setEnabled(flag);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public void changeRoles(int id) {
        Set<Role> roles = get(id).getRoles();
        if (roles.contains(Role.ROLE_MANAGER)) {
            roles.remove(Role.ROLE_MANAGER);
        } else {
            roles.add(Role.ROLE_MANAGER);
        }
    }

    @Override
    public AuthorizedUser loadUserByUsername(String email) throws UsernameNotFoundException {
        Assert.notNull(email, "email must not be null");
        User user = repository.findByEmail(email.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " is not found");
        }
        return new AuthorizedUser(user);
    }
}
