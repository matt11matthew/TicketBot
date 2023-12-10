package me.matthewe.ticket.database;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.handler.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Matthew E on 12/10/2023 at 12:03 PM for the project TicketBot
 */
public class DatabaseHandler  extends Handler {
    private Connection connection;
    public DatabaseHandler(TicketBot ticketBot, Config config) {
        super(ticketBot, config);
    }

    @Override
    public void onEnable() {

        this.startConnection();

    }

    private void startConnection() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://"+this.config.database.host.address+":"+this.config.database.host.port+"/"+this.config.database.database,this.config.database.auth.username,this.config.database.auth.password);
        } catch (SQLException e) {
            e.printStackTrace();
            this.ticketBot.shutdown();
         }finally {
            this.ticketBot.getLogger().info("[DatabaseHandler] MySQL Connected!");
        }
        this.createTablesIfNotExist();
    }

    private void createTablesIfNotExist() {
        String firstTable = "CREATE TABLE IF NOT EXISTS users (\n" +
                "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    username VARCHAR(255) NOT NULL\n" +
                ");";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(firstTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.ticketBot.getLogger().info("[DatabaseHandler] Created tables");
        }

    }

    @Override
    public void onDisable() {

    }
}
