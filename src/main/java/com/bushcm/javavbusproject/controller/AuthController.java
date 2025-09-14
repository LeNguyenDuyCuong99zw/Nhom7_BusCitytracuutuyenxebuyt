package com.bushcm.javavbusproject.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bushcm.javavbusproject.entity.User;
import com.bushcm.javavbusproject.services.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body, HttpSession session) {
        final String username = body.get("username");
        final String password = body.get("password");
        final String fullName = body.getOrDefault("fullName", "");
        final String role = body.getOrDefault("role", "user");
        if (username == null || password == null) return ResponseEntity.badRequest().body(Map.of("error","missing"));
        if (userService.findByUsername(username).isPresent()) return ResponseEntity.status(409).body(Map.of("error","exists"));
        final User u = userService.registerUser(username, password, fullName, role);
        // set session attribute
        session.setAttribute("userId", u.getId());
        session.setAttribute("username", u.getUsername());
        session.setAttribute("fullName", u.getFullName());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        final String username = body.get("username");
        final String password = body.get("password");
        if (username == null || password == null) return ResponseEntity.badRequest().body(Map.of("error","missing"));
        // Hardcoded admin credentials (local/debug only)
        if ("admin".equals(username) && "1234".equals(password)) {
            session.setAttribute("userId", 0L);
            session.setAttribute("username", "admin");
            session.setAttribute("fullName", "Administrator");
            session.setAttribute("role", "ADMIN");
            return ResponseEntity.ok(Map.of("ok", true));
        }
        final var found = userService.findByUsername(username);
        if (found.isEmpty()) return ResponseEntity.status(401).body(Map.of("error","invalid"));
        final User u = found.get();
        // check password via service
        if (!userService.checkPassword(u, password)) {
            return ResponseEntity.status(401).body(Map.of("error","invalid"));
        }
        session.setAttribute("userId", u.getId());
        session.setAttribute("username", u.getUsername());
        session.setAttribute("fullName", u.getFullName());
        session.setAttribute("role", u.getRole());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        try { session.invalidate(); } catch (Exception ex) {}
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        final Object uid = session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(401).body(Map.of("error","unauth"));
        return ResponseEntity.ok(Map.of(
            "userId", uid,
            "username", session.getAttribute("username"),
            "fullName", session.getAttribute("fullName"),
            "role", session.getAttribute("role")
        ));
    }
}
