package com.inventory.billing_system.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// @Component tells Spring to manage this class as a bean
// Spring will create one instance and share it wherever needed
@Component
public class DatabaseConnection {
    // @Value reads from application.properties automatically
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    // Returns a live Connection to MySQL
// Called by ProductDAO every time it needs to run a query
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}