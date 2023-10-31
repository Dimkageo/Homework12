package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {

    public static final String DEFAULT_TIMEZONE = "UTC";
    public static final int MILLISECONDS_IN_HOUR = 3600000;
    public static final String COOKIE_NAME = "lastTimezone";

    private TemplateEngine engine;


    @Override
    public void init() throws ServletException{
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("D:/demo/GoIT/JavaDev/Homework12_3/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Отримуємо значення параметра timezone з запиту
        String timezoneParam = request.getParameter("timezone");

        // Отримуємо часовий пояс з Cookie
        String lastTimezone = getLastTimezoneFromCookie(request);

        TimeZone timeZone;
        if (timezoneParam != null) {
            // Якщо переданий параметр timezone, використовуємо його
            timeZone = getTimeZoneFromParameter(timezoneParam);
            // Зберігаємо в Cookie останній валідний часовий пояс на 1 хвилин
            saveLastTimezoneToCookie(response, timezoneParam, 1 * 60);
        } else if (lastTimezone != null) {
            // Якщо параметр timezone не передано, але є в Cookie, використовуємо його
            timeZone = getTimeZoneFromParameter(lastTimezone);
        } else {
            // Якщо параметр timezone не передано і його немає в Cookie, використовуємо UTC
            timeZone = TimeZone.getTimeZone(DEFAULT_TIMEZONE);
        }

        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(timeZone);
        String formattedDate = sdf.format(currentDate);

        // Використання Thymeleaf для обробки шаблону
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("title", "Current Time (UTC)");
        params.put("formattedDate", formattedDate);

        Context context = new Context(request.getLocale(), params);
        engine.process("time-template", context, response.getWriter());
    }

    private TimeZone getTimeZoneFromParameter(String timezoneParam) {
        String stringZoneID = timezoneParam.substring(3).trim();
        TimeZone timeZone = TimeZone.getTimeZone(DEFAULT_TIMEZONE);
        int zoneId = Integer.parseInt(stringZoneID);
        timeZone.setRawOffset(zoneId * MILLISECONDS_IN_HOUR);
        return timeZone;
    }

    private void saveLastTimezoneToCookie(HttpServletResponse response, String timezone, int maxAgeInSeconds) {
        try {
            timezone = URLEncoder.encode(timezone, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // Обробка помилки кодування URL
        }
        // Зберігання останнього валідного часового поясу в Cookie
        Cookie cookie = new Cookie(COOKIE_NAME, timezone);
        cookie.setMaxAge(maxAgeInSeconds);
        response.addCookie(cookie);
    }

    private String getLastTimezoneFromCookie(HttpServletRequest request) {
        // Отримання значення часового поясу з Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
