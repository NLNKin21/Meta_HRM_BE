package com.metahrms.employee_management.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

public class SecurityUtils {
    public static Integer getCurrentUserId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        ServletRequestAttributes sra = (ServletRequestAttributes) attrs;
        HttpServletRequest request = sra.getRequest();
        Object userObj = request.getAttribute("user");
        if (userObj instanceof Map) {
            Map<?, ?> userMap = (Map<?, ?>) userObj;
            Object idObj = userMap.get("id");
            if (idObj == null) return null;
            try {
                return Integer.valueOf(idObj.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static String getCurrentUserRole() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        ServletRequestAttributes sra = (ServletRequestAttributes) attrs;
        HttpServletRequest request = sra.getRequest();
        Object userObj = request.getAttribute("user");
        if (userObj instanceof Map) {
            Map<?, ?> userMap = (Map<?, ?>) userObj;
            Object roleObj = userMap.get("role");
            if (roleObj == null) return null;
            return roleObj.toString();
        }
        return null;
    }
}
