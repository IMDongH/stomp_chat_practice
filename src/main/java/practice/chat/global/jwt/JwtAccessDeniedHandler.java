package practice.chat.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import practice.chat.global.response.ErrorCode;
import practice.chat.global.response.ResponseDTO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 필요한 권한이 없이 접근하려 할때 403
        // 즉 ADMIN 페이지에 USER 가 접근하려고 할 때
        sendResponse(request,response, accessDeniedException);

    }

    private void sendResponse(HttpServletRequest request,HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        String result = objectMapper.writeValueAsString(ResponseDTO.of(false, ErrorCode.AUTHORITY_FORBIDDEN,"접근 권한이 없습니다."));


        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(result);
        response.setStatus(response.SC_FORBIDDEN);
    }
}