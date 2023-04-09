package linkedhu.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import linkedhu.model.Role;
import linkedhu.model.User;
import linkedhu.repository.RoleRepository;
import linkedhu.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final int PAGE_SIZE = 10;
    private final String SORT_FIELD = "name";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null)
            throw new UsernameNotFoundException("User is not found");

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(authority -> {
            authorities.add(new SimpleGrantedAuthority(authority.getName()));
        });

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                authorities);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void addRoleToUser(String username, String roleName) {
        User user = getUser(username);
        Role role = getRole(roleName);
        user.getRoles().add(role);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public Role getRole(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public boolean updatePassword(String username, String newPassword) {
        User user = getUser(username);

        if (user == null)
            return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public boolean updateProfilePic(String username, MultipartFile profilePic) throws IOException {
        User user = getUser(username);

        if (user == null)
            return false;

        user.setProfilePic(profilePic.getBytes());
        userRepository.save(user);
        return true;
    }

    public boolean updateCoverPic(String username, MultipartFile coverPic) throws IOException {
        User user = getUser(username);

        if (user == null)
            return false;

        user.setCoverPic(coverPic.getBytes());
        userRepository.save(user);
        return true;
    }

    public boolean updateUserDetails(String username, String name, String title, String company) {
        User user = getUser(username);

        if (user == null)
            return false;

        user.setName(name);
        user.setTitle(title);
        user.setCompany(company);
        userRepository.save(user);
        return true;
    }

    public boolean updateAboutMe(String username, String text) {
        User user = getUser(username);

        if (user == null)
            return false;

        user.setAboutMe(text);
        userRepository.save(user);
        return true;
    }

    public boolean updateCompany(String username, String companyName) {
        User user = getUser(username);

        if (user == null)
            return false;

        user.setCompany(companyName);
        userRepository.save(user);
        return true;
    }

    public boolean disableAccount(String username) {
        User user = getUser(username);

        if (user == null)
            return false;

        user.setActive(false);
        userRepository.save(user);
        return true;
    }

    public boolean enableAccount(String username) {
        User user = getUser(username);

        if (user == null)
            return false;

        user.setActive(true);
        userRepository.save(user);
        return true;
    }

    public Page<User> searchUser(String name) {
        Page<User> users = userRepository.findByNameContains(name,
                PageRequest.of(0, PAGE_SIZE).withSort(Sort.by(SORT_FIELD).descending()));
        return users;
    }
}
