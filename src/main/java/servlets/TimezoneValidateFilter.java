package servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@WebFilter(value = "/time")
public class TimezoneValidateFilter implements Filter {
    private List<String> VALID_TIMEZONE = Arrays.asList(
            "UTC 0", "UTC 1", "UTC 2", "UTC 3", "UTC 4", "UTC 5", "UTC 6", "UTC 7", "UTC 8", "UTC 9", "UTC 10",
            "UTC 11", "UTC 12", "UTC 13", "UTC-1", "UTC-2", "UTC-3", "UTC-4", "UTC-5", "UTC-6", "UTC-7", "UTC-8",
            "UTC-9", "UTC-10", "UTC-11", "UTC-12"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String timezone = req.getParameter("timezone");

        HttpServletResponse httpResponse = (HttpServletResponse) resp;

        if (isValidTimezone(timezone)) {
            // Якщо часовий пояс валідний, передаємо обробку далі
            chain.doFilter(req, resp);
        } else {
            // Якщо часовий пояс невалідний, виводимо відповідь з помилкою
            handleInvalidTimezone(httpResponse);
        }
    }

    private boolean isValidTimezone(String timezone) {
        return  timezone == null || VALID_TIMEZONE.contains(timezone);
    }
    private void handleInvalidTimezone(HttpServletResponse httpResponse) throws IOException {
        httpResponse.setContentType("timezone");
        httpResponse.setCharacterEncoding("UTF-8");

        httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        try (PrintWriter out = httpResponse.getWriter()) {
            out.println("<html>");
            out.println("<head><title>Invalid timezone</title></head>");
            out.println("<body>");
            out.println("<h2>Invalid timezone</h2>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
