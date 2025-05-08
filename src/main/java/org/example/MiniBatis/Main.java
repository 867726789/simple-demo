package org.example.MiniBatis;

import org.example.MiniBatis.entity.User;
import org.example.MiniBatis.entity.UserMapper;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        MySqlSessionFactory sqlSessionFactory = new MySqlSessionFactory();
        UserMapper userMapper = sqlSessionFactory.getMapper(UserMapper.class);
//        User user = userMapper.selectById(1);
//        User user = jdbcSelectById(1);
//        System.out.println(user);
        System.out.println(userMapper.selectByAge(11));
    }

    private static  User jdbcSelectById(int id) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mybatis_db";
        String dbUser = "root";
        String dbPass = "root";

        String sql = "SELECT id, name, age FROM user WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(jdbcUrl,dbUser,dbPass);
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setAge(rs.getInt("age"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
}
