package com.example.techadvisor.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("✅✅✅ CHECKPOINT 2: FirebaseTokenFilter ĐANG CHẠY cho request: " + request.getRequestURI());


        // Lấy header "Authorization" từ request
        String header = request.getHeader("Authorization");

        // Nếu header không tồn tại hoặc không bắt đầu bằng "Bearer ", bỏ qua
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // Cắt bỏ chữ "Bearer "
        try {
            // Xác thực token với Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            // Nếu token hợp lệ, lấy UID và tạo đối tượng xác thực
            String uid = decodedToken.getUid();

            System.out.println("✅✅✅ CHECKPOINT 4: Token HỢP LỆ! UID là: " + uid);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    uid, null, new ArrayList<>() // principal là UID, credentials là null, authorities là rỗng
            );

            // Đặt đối tượng xác thực vào SecurityContext.
            // Từ đây Spring Security sẽ biết user này đã đăng nhập.
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            System.err.println("🔥🔥🔥 LỖI XÁC THỰC FIREBASE TOKEN: " + e.getMessage());

            // Nếu token không hợp lệ, xóa mọi thông tin xác thực
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Firebase Token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
