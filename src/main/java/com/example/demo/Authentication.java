package com.example.demo;
import java.sql.*;
import java.util.Objects;


public class Authentication
{

    public static Employee login(String username, String password)
    {

        try (Connection conn = Database.getConnection())
        {
            String SELECT_QUERY = "SELECT e.employee_id, e.username, e.password, e.name, e.surname , r.role_name " +
                    "FROM employees e " +
                    "JOIN roles r ON e.role = r.role_id " +
                    "WHERE e.username = ? AND e.password = ?";

            PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                username = rs.getString("username");
                password = rs.getString("password");
                int employee_id = rs.getInt("employee_id");
                String role = rs.getString("role_name");
                String name = rs.getString("name");
                String surname = rs.getString("surname");

                if (Objects.equals(role, "Manager")) {
                    return new Manager(employee_id, username, password, role, name, surname);
                }
                else if (Objects.equals(role, "Cashier")){
                    return new Cashier(employee_id, username, password, role, name, surname);
                }
                else {
                    return new Admin(employee_id, username, password,role, name, surname);
                }


            }
        }
        catch (SQLException e)
        {
            Main.showError(e.getMessage());
        }

        return null;
    }
}
