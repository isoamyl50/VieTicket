package com.example.VieTicketSystem.model.repo;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Date;

import org.springframework.stereotype.Repository;

import com.example.VieTicketSystem.model.entity.User;

@Repository
public class LoginRepo {
    public User CheckLogin(String usernameInput, String passwordInput) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection connection = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = connection.prepareStatement("Select * from User where username = ? and password = ?");
        ps.setString(1, usernameInput);
        ps.setString(2, passwordInput);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int userId = rs.getInt("user_id");
            String fullName = rs.getString("full_name");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String phone = rs.getString("phone");
            Date dob = rs.getDate("dob");
            char gender = rs.getString("gender").charAt(0);
            String avatar = rs.getString("avatar");
            char role = rs.getString("role").charAt(0);
            User user = new User(userId, fullName, username, password, phone, dob, gender, avatar, role);
            ps.close();
            return user;
        }
        return null;

    }
}
