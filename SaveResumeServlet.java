package resume;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/api/resume/save")
public class SaveResumeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("userName");
        String resumeData = request.getParameter("resumeData");

        if (userName == null || userName.trim().isEmpty()) {
            userName = "anonymous";
        }

        if (resumeData == null || resumeData.trim().isEmpty()) {
            sendJson(response, 500, "{\"status\":\"error\",\"message\":\"No data received\"}");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();

            String sql = "INSERT INTO resumes (user_name, resume_data, updated_at) "
                       + "VALUES (?, ?, ?) "
                       + "ON DUPLICATE KEY UPDATE resume_data = ?, updated_at = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, resumeData);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setString(4, resumeData);
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

            int rows = ps.executeUpdate();
            sendJson(response, 200, "{\"status\":\"success\",\"rowsAffected\":" + rows + "}");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(response, 500, "{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    private void sendJson(HttpServletResponse response, int status, String json) throws IOException {
        response.setStatus(status);
        PrintWriter out = response.getWriter();
        out.write(json);
        out.flush();
    }
}