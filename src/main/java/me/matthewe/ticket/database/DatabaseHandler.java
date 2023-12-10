package me.matthewe.ticket.database;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.handler.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            this.connection = DriverManager.getConnection("jdbc:mysql://"+this.config.database.host.address+":"+this.config.database.host.port+"/"+this.config.database.database+"user="+config.database.auth.username+"&password="+config.database.auth.password);
        } catch (SQLException e) {
            e.printStackTrace();
            this.ticketBot.shutdown();
        }
    }

    @Override
    public void onDisable() {

    }
}
