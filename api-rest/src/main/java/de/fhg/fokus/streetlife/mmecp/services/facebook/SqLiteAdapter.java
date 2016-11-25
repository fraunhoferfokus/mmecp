package de.fhg.fokus.streetlife.mmecp.services.facebook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.sql.*;

/**
 * Created by csc on 12.02.2016.
 */
@ApplicationScoped
public class SqLiteAdapter {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String databaseName   = "fb_tokens.db";
    private static final String databaseTarget = "jdbc:sqlite:" + databaseName;

    public SqLiteAdapter()
    {}

    @PostConstruct
    void init()
    {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            LOG.error("class loader problem:" + e.getCause());
            return;
        }

        try {
            Connection conn = openDatabaseConnection();
            initSqlTable(conn);
            conn.close();
        } catch (SQLException e) {
            LOG.error("SQLite problem:" + e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    private Connection openDatabaseConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(databaseTarget);
        //LOG.info("opened SQLite database connection successfully");
        return conn;
    }

    private void initSqlTable(Connection connection) throws SQLException {

        Statement stmt = connection.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS FACEBOOKUSERS " +
                "(FB_USERID           PRIMARY KEY     NOT NULL," +
                " FB_SHORTACCESSTOKEN         TEXT    NOT NULL," +
                " FB_LONGACCESSTOKEN          TEXT    NOT NULL," +
                " FB_TOKENTYPE                TEXT    NOT NULL," +
                " EXPIRES                     INTEGER NOT NULL," +
                " DEVICEID                    TEXT    NOT NULL)";

        stmt.executeUpdate(sql);
        LOG.info("Facebook tokens SQLite table created successfully");
        stmt.close();
    }


    public synchronized void updateFacebookEntry(String userId, String shortToken, String longToken,
                                                 String tokenType, Integer expires, String deviceId)
    {
        try {
            Connection connection = openDatabaseConnection();
            initSqlTable(connection); // <- always make sure that the database table has been created, before writing any data

            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            String sql = "REPLACE INTO FACEBOOKUSERS (FB_USERID,FB_SHORTACCESSTOKEN,FB_LONGACCESSTOKEN,FB_TOKENTYPE,EXPIRES,DEVICEID) " +
                    "VALUES ('"+userId+"','"+shortToken+"','"+longToken+"','"+tokenType+"',strftime('%s','now')+"+expires+",'"+deviceId+"');";
            stmt.executeUpdate(sql);
            System.out.println("updated Facebook token record in Database successfully");

            stmt.close();
            //connection.commit();
            connection.close();
        } catch ( Exception e ) {
            LOG.error("SQLite problem:" + e.getClass().getName() + ": " + e.getMessage() );
        }
    }

}
