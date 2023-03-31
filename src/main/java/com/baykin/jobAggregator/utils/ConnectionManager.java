package com.baykin.jobAggregator.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private final PropertiesUtil propertiesUtil;

    public ConnectionManager(PropertiesUtil propertiesUtil) {
        this.propertiesUtil = propertiesUtil;
        loadDriver();
    }

    private void loadDriver() {
        try {
            Class.forName(propertiesUtil.get("driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection open() {
        try {
            return DriverManager.getConnection(propertiesUtil.get("url"),
                    propertiesUtil.get("login"),
                    propertiesUtil.get("password"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
