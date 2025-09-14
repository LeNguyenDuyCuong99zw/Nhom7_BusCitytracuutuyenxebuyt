package com.bushcm.javavbusproject.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        Object role = session != null ? session.getAttribute("role") : null;

        boolean isAdmin = role != null && "ADMIN".equals(String.valueOf(role));

        // protect /api/admin/** -> return 403 JSON
        if (path.startsWith(request.getContextPath() + "/api/admin") || path.equals("/api/admin") || path.startsWith("/api/admin")) {
            if (!isAdmin) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"unauthorized\"}");
                return false;
            }
            return true;
        }

        // protect UI /admin -> redirect to home if not admin
        if (path.equals(request.getContextPath() + "/admin") || path.equals("/admin") || path.startsWith("/admin")) {
            if (!isAdmin) {
                response.sendRedirect(request.getContextPath() + "/");
                return false;
            }
            return true;
        }

        return true;
    }
}
