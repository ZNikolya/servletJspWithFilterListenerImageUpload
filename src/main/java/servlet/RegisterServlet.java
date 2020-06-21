package servlet;

import manager.UserManager;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;

@WebServlet(urlPatterns = "/register")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 5 * 5)
public class RegisterServlet extends HttpServlet {

    private UserManager userManager = new UserManager();

    private final String UPLOAD_DIR = "D:\\lessons\\FullStack20\\web\\ServletJspExample\\upload";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String surname = req.getParameter("surname");
        String email = req.getParameter("username");
        String password = req.getParameter("password");
        StringBuilder msg = new StringBuilder();
        if (name == null || name.length() == 0) {
            msg.append("Name field is required <br>");
        }
        if (surname == null || surname.length() == 0) {
            msg.append("surname field is required <br>");
        }
        if (email == null || email.length() == 0) {
            msg.append("email field is required <br>");
        } else if (userManager.getByEmail(email) != null) {
            msg.append("email already exists <br>");
        }
        if (password == null || password.length() == 0) {
            msg.append("password field is required <br>");
        }
        if (msg.toString().equals("")) {
            User user = User.builder()
                .name(name)
                .surname(surname)
                .email(email)
                .password(password)
                .build();
            for (Part part : req.getParts()) {
                if(getFileName(part) != null) {
                    String fileName = System.currentTimeMillis() + getFileName(part);
                    String fullFileName = UPLOAD_DIR + File.separator + fileName;
                    part.write(fullFileName);
                    user.setPictureUrl(fileName);
                }
            }
            userManager.register(user);
            msg.append("<span style=\"color:green\">User register Successfully, Please login</span>");
        }
        req.getSession().setAttribute("msg", msg.toString());
        resp.sendRedirect("/");
    }

    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename"))
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
        }
        return null;
    }
}
