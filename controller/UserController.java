package linkedhu.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import linkedhu.constants.RoleConstants;
import linkedhu.model.Role;
import linkedhu.model.User;
import linkedhu.request.ChangePasswordRequest;
import linkedhu.request.SearchUserRequest;
import linkedhu.request.SignupRequest;
import linkedhu.request.UpdateUserAboutMe;
import linkedhu.request.UpdateUserCompany;
import linkedhu.request.UpdateUserDetails;
import linkedhu.request.UserRoleRequest;
import linkedhu.response.SimpleResponse;
import linkedhu.service.UserService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Object> saveUser(@RequestBody SignupRequest request) {
        String username = request.getUsername(),
                password = request.getPassword(),
                name = request.getName(),
                department = request.getDepartment(),
                title = request.getTitle(),
                role = request.getRole();

        if (username == null || password == null || name == null || department == null ||
                role == null || title == null || username.length() == 0 || password.length() == 0 ||
                name.length() == 0 || department.length() == 0 || title.length() == 0 || role.length() == 0)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Please fill all the fields", null));

        if (!verifyEmail(username))
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "The email format is incorrect", null));

        if (password.length() < 6)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "The password must be at least 6 characters", null));

        if (userService.getUser(username) != null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "User already exists", null));

        if (role.equals(RoleConstants.ROLE_ADMIN) || role.equals(RoleConstants.ROLE_STUDENT_REP)
                || (!role.equals(RoleConstants.ROLE_ACADEMICIAN) && !role.equals(RoleConstants.ROLE_GRADUATE)
                        && !role.equals(RoleConstants.ROLE_STUDENT)))
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Bad user role", null));

        User user = userService
                .saveUser(new User(null, username, password, name, title, department, "Please tell us about yourself",
                        null,
                        true, null, null, new ArrayList<>()));
        userService.addRoleToUser(username, role);
        return ResponseEntity.ok(new SimpleResponse("success", "The user is created", user));
    }

    // Update user methods
    @PutMapping("/user/update/company")
    public ResponseEntity<Object> updateCompany(@RequestBody UpdateUserCompany request, Principal principal) {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        if (request.getCompany() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Please fill company name", null));

        boolean success = userService.updateCompany(principal.getName(), request.getCompany());

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not update company", null));

        return ResponseEntity
                .ok(new SimpleResponse("success", "The company is updated", userService.getUser(principal.getName())));
    }

    @PutMapping("/user/update/aboutme")
    public ResponseEntity<Object> updateAboutMe(@RequestBody UpdateUserAboutMe request, Principal principal) {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        if (request.getText() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Please fill description", null));

        boolean success = userService.updateAboutMe(principal.getName(), request.getText());

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not update description", null));

        return ResponseEntity.ok(
                new SimpleResponse("success", "The description is updated", userService.getUser(principal.getName())));
    }

    @PutMapping("/user/update/details")
    public ResponseEntity<Object> updateUserDetails(@RequestBody UpdateUserDetails request, Principal principal) {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        if (request.getName() == null || request.getTitle() == null || request.getName().length() == 0
                || request.getTitle().length() == 0)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Please fill all the places", null));

        boolean success = userService.updateUserDetails(principal.getName(), request.getName(), request.getTitle(),
                request.getCompany());

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not update user details", null));

        return ResponseEntity.ok(new SimpleResponse("success", "The user details are updated",
                userService.getUser(principal.getName())));
    }

    @PutMapping("/user/update/profilepic")
    public ResponseEntity<Object> updateProfilePic(@RequestParam MultipartFile file, Principal principal)
            throws IOException {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        if (file == null || file.isEmpty() || file.getSize() == 0)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Please upload a proper image", null));

        boolean success = userService.updateProfilePic(principal.getName(), file);

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not update profile picture", null));

        return ResponseEntity.ok(new SimpleResponse("success", "The profile picture is updated",
                userService.getUser(principal.getName())));
    }

    @PutMapping("/user/update/coverpic")
    public ResponseEntity<Object> updateCoverPic(@RequestParam MultipartFile file, Principal principal)
            throws IOException {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        if (file == null || file.isEmpty() || file.getSize() == 0)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "Please upload a proper image", null));

        boolean success = userService.updateCoverPic(principal.getName(), file);

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not update profile picture", null));

        return ResponseEntity.ok(new SimpleResponse("success", "The profile picture is updated",
                userService.getUser(principal.getName())));
    }

    // Disable account
    @PutMapping("/user/disable")
    public ResponseEntity<Object> disableAccount(Principal principal) {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        boolean success = userService.disableAccount(principal.getName());

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not disable user", null));

        return ResponseEntity.ok(new SimpleResponse("success", "The user is disabled", null));
    }

    // Disable account - admin
    @PutMapping("/user/disable/{id}")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<Object> disableAccount(@PathVariable Long id, Principal principal) {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        User user = userService.getUserById(id);

        if (user == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "User is not found", null));

        boolean success = userService.disableAccount(user.getUsername());

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not disable user", null));

        return ResponseEntity.ok(new SimpleResponse("success", "The user is disabled", null));
    }

    // Enable account - admin
    @PutMapping("/user/enable/{id}")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<Object> enableAccount(@PathVariable Long id, Principal principal) {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        User user = userService.getUserById(id);

        if (user == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "User is not found", null));

        boolean success = userService.enableAccount(user.getUsername());

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not enable user", null));

        return ResponseEntity.ok(new SimpleResponse("success", "The user is enabled", null));
    }

    // Change password
    @PutMapping("/user/changepassword")
    public ResponseEntity<Object> updatePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        if (request.getPassword() == null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "The password is empty", null));

        if (request.getPassword().length() < 6)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "The password must be at least 6 characters", null));

        boolean success = userService.updatePassword(principal.getName(), request.getPassword());

        if (!success)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "Could not change password", null));

        return ResponseEntity.ok(new SimpleResponse("success", "The password is changed", null));
    }

    // Search users
    @PostMapping("/user/search")
    public ResponseEntity<Object> updatePassword(@RequestBody SearchUserRequest request) {
        if (request.getName() == null)
            return ResponseEntity.badRequest()
                    .body(new SimpleResponse("error", "The search name is empty", null));

        Page<User> users = userService.searchUser(request.getName());
        return ResponseEntity.ok(new SimpleResponse("success", "Search was successful", users));
    }

    // Get self
    @GetMapping("/user")
    public ResponseEntity<Object> getSelf(Principal principal) {
        if (principal.getName() == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "You are not authorized", null));

        User user = userService.getUser(principal.getName());
        return ResponseEntity.ok(new SimpleResponse("success", "User is fetched", user));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        if (user == null)
            return ResponseEntity.badRequest().body(new SimpleResponse("error", "User not found", null));

        return ResponseEntity.ok(new SimpleResponse("success", "User is fetched", user));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/role/save")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        return ResponseEntity.ok(userService.saveRole(role));
    }

    @PostMapping("/role/addtouser")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<?> addRoleToUser(@RequestBody UserRoleRequest userRoleRequest) {
        userService.addRoleToUser(userRoleRequest.getUsername(), userRoleRequest.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('" + RoleConstants.ROLE_ADMIN + "')")
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(userService.getRoles());
    }

    // Helpers
    private boolean verifyEmail(String email) {
        return (Pattern.compile(
                "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$")
                .matcher(email)).matches();
    }
}