package linkedhu.request;

import lombok.Data;

@Data
public class SignupRequest {
    String username;
    String password;
    String name;
    String department;
    String title;
    String role;
}