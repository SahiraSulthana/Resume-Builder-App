package resume;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/resume/load")
public class LoadResumeServlet extends HttpServlet {

    private static final long serialVersionUID = -1080366981541073439L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("userName");

        if (userName == null || userName.trim().isEmpty()) {
            userName = "anonymous";
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            String sql = "SELECT resume_data FROM resumes "
                       + "WHERE user_name = ? "
                       + "ORDER BY updated_at DESC LIMIT 1";

            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();

            PrintWriter out = response.getWriter();

            if (rs.next()) {
                // FIXED: write JSON data directly as a value — no manual escaping needed
                String data = rs.getString("resume_data");
                out.write("{\"status\":\"success\",\"data\":");
                out.write(data);
                out.write("}");
            } else {
                out.write("{\"status\":\"not_found\"}");
            }
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            PrintWriter out = response.getWriter();
            out.write("{\"status\":\"error\",\"message\":\"" + e.getMessage().replace("\"", "'").replace("\n", " ") + "\"}");
            out.flush();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }
}