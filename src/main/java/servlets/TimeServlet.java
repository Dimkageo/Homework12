package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public static final int SECONDS_TO_HOUR = 36000;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("text/html;charset=UTF-8");

        String timezoneParam = request.getParameter("timezone");
        TimeZone timeZone = TimeZone.getTimeZone(DEFAULT_TIMEZONE);

        if (timezoneParam != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("zzz");
            SimpleDateFormat outputFormat = new SimpleDateFormat("Z");

            String offset = null;
            try {
                Date parsedDate = inputFormat.parse(timezoneParam);
                offset = outputFormat.format(parsedDate);
                timeZone.setRawOffset(Integer.parseInt(offset));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int zoneId = Integer.parseInt(offset);
            timeZone.setRawOffset(zoneId * SECONDS_TO_HOUR);
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
}
