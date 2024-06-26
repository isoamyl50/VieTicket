package com.example.VieTicketSystem.fileSever.config;

import java.io.IOException;

import com.example.VieTicketSystem.model.repo.UnverifiedUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.VieTicketSystem.model.entity.Organizer;
import com.example.VieTicketSystem.model.entity.User;
import com.example.VieTicketSystem.model.repo.OrganizerRepo;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthFilter implements Filter {
    @Autowired
    OrganizerRepo organizerRepo = new OrganizerRepo();

    @Autowired
    UnverifiedUserRepo unverifiedUserRepo;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();
        String requestURI = httpRequest.getRequestURI();

        // Bỏ qua filter cho các yêu cầu đến các tệp tĩnh (ví dụ: CSS, JavaScript)
        if (requestURI.startsWith("/static/")) {
            chain.doFilter(request, response);
            return;
        }

        User user = (User) session.getAttribute("activeOrganizer");
        user = user == null ? (User) session.getAttribute("activeUser") : user;

        // Check if the user is unverified
        boolean isUnverified = false;
        try {
            isUnverified = user != null && unverifiedUserRepo.isUnverified(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If the user is unverified and the request URI is not /auth/verify-email,
        // /change, or /editUser, then redirect them to /auth/verify-email
        if (isUnverified && !(requestURI.equals("/auth/verify-email") || requestURI.startsWith("/change")
                || requestURI.startsWith("/editUser") || requestURI.startsWith("/auth/verify-otp")
                || requestURI.startsWith("/auth/password-reset") || requestURI.startsWith("/auth/log-out"))) {
            httpResponse.sendRedirect("/auth/verify-email");
            return;
        }

        if (isUnverified && (requestURI.startsWith("/auth/log-out") || requestURI.startsWith("/auth/verify-otp")
                || requestURI.startsWith("/auth/password-reset"))) {
            chain.doFilter(request, response);
            return;
        }

        // tất cả các role đều có thể vào
        if ((requestURI.equals("/auth/login") || requestURI.equals("/auth/login/oauth2/google") || requestURI.isEmpty()
                || requestURI.equals("/auth/reset-password") || requestURI.equals("/auth/password-reset")
                || requestURI.equals("/auth/verify-otp") || requestURI.equals("/signup")
                || requestURI.equals("/auth/log-out") || requestURI.equals("/")|| requestURI.equals("/search-event") || requestURI.equals("/searchResults")|| requestURI.equals("/rating")
                || requestURI.startsWith("/viewdetailEvent") || requestURI.startsWith("/api"))
                && !isUnverified) {
            chain.doFilter(request, response);
            return;
        }

        // Kiểm tra role của người dùng và cho phép truy cập tài nguyên tương ứng
        if (isAdmin(user)) {
            // Người dùng có role ADMIN được truy cập tất cả các trang
            chain.doFilter(request, response);
        } else if (isUser(user) && (requestURI.startsWith("/change") || requestURI.startsWith("/editUser")
                || requestURI.startsWith("/upload") || requestURI.startsWith("/tickets"))
                || requestURI.startsWith("/auth/verify-email") || requestURI.startsWith("/purchase")) {
            // Người dùng có role USER chỉ được truy cập trang search
            chain.doFilter(request, response);
        } else if (isOrganizer(user)) {
            // Tìm thông tin Organizer dựa trên userId
            Organizer organizer = organizerRepo.getOrganizerByUserId(user.getUserId());

            if (organizer != null) {
                boolean eventCreated = Boolean.TRUE.equals(session.getAttribute("eventCreated"));
                if (organizer.isActive() && requestURI.startsWith("/createEvent")
                        || requestURI.startsWith("/inactive-account")
                        || (requestURI.startsWith("/change")
                                || requestURI.startsWith("/editUser")
                                || requestURI.startsWith("/upload")
                                || requestURI.startsWith("/upload/poster")
                                || requestURI.startsWith("/upload/banner")
                                || requestURI.startsWith("/add-event")
                                || requestURI.startsWith("createEvent") ||
                                (requestURI.startsWith("/seatMap") && eventCreated) ||
                                (requestURI.startsWith("/seatMap/NoSeatMap") && eventCreated) ||
                                (requestURI.startsWith("/seatMap/SeatMapBeta") && eventCreated)
                                || requestURI.startsWith("/viewMyListEvent")
                                || requestURI.startsWith("/allEvents")
                                || requestURI.startsWith("/pendingEvents")
                                || requestURI.startsWith("/approvedEvents")
                                || requestURI.startsWith("/passedEvents")
                                || requestURI.startsWith("/seatMapEdit")
                                || requestURI.startsWith("/seatMapDelete")
                                || requestURI.startsWith("/noModelEditPage")
                                || (requestURI.startsWith("/seatMap/NoSeatMapEdit")) ||
                                (requestURI.startsWith("/seatMap/SeatMapBetaEdit"))
                                || requestURI.startsWith("/editSuccess"))
                        || requestURI.startsWith("/eventEditPage")
                        || requestURI.startsWith("/eventEditSubmit")
                        || requestURI.startsWith("/viewStatistics")) {
                    if (requestURI.startsWith("/createEvent")) {
                        session.setAttribute("eventCreated", true);
                    }
                    // Người dùng có role ORGANIZER chỉ được truy cập các trang cho phép khi
                    // isActive
                    chain.doFilter(request, response);
                } else {
                    httpResponse.sendRedirect("/inactive-account");
                    return;
                }
            }
        } else {
            // Không có quyền truy cập
            httpResponse.sendRedirect("/");
        }

    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == 'a';
    }

    private boolean isUser(User user) {
        if (user != null) {
            char role = user.getRole();
            return role == 'u';
        }
        return false;
    }

    private boolean isOrganizer(User user) {
        if (user != null) {
            char role = user.getRole();
            return role == 'o';
        }
        return false;
    }

}