package me.matthewe.ticket.database;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.discord.ticket.Ticket;
import me.matthewe.ticket.handler.Handler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 12/10/2023 at 12:03 PM for the project TicketBot
 */
public class DatabaseHandler  extends Handler {
    private Connection connection;
    public DatabaseHandler(TicketBot ticketBot, Config config) {
        super(ticketBot, config);
        setShutdownPriority(5);
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
        String ticketsTable = "CREATE TABLE IF NOT EXISTS Tickets (\n" +
                "    id BIGINT AUTO_INCREMENT PRIMARY KEY,\n" +
                "    clientId BIGINT,\n" +
                "    channelId BIGINT,\n" +
                "    creationDate BIGINT,\n" +
                "    ticketStatus ENUM('OPEN', 'CLOSED', 'ACCEPTED'),\n" +
                "    description TEXT,\n" +
                "    posted BOOLEAN,\n" +
                "    awaitingDescription BOOLEAN,\n" +
                "    department VARCHAR(255),\n" +
                "    freelancerId BIGINT\n" +
                ");";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(ticketsTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.ticketBot.getLogger().info("[DatabaseHandler] Created tables");
        }

    }

    @Override
    public void onDisable() {

    }

    public void deleteTicketFromDatabase(Ticket ticket) {
        String sql = "DELETE FROM Tickets WHERE id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, ticket.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Ticket with ID " + ticket.getId() + " was deleted successfully.");
            } else {
                System.out.println("No ticket found with ID " + ticket.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error deleting ticket: " + e.getMessage());
            e.printStackTrace();
        }

    }
    public void updateTicketInDatabase(Ticket ticket) {
        String sql = "UPDATE Tickets SET " +
                "clientId = ?, " +
                "channelId = ?, " +
                "creationDate = ?, " +
                "ticketStatus = ?, " +
                "description = ?, " +
                "posted = ?, " +
                "awaitingDescription = ?, " +
                "department = ?, " +
                "freelancerId = ? " +
                "WHERE id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, ticket.getClientId());
            pstmt.setLong(2, ticket.getChannelId());
            pstmt.setLong(3, ticket.getCreationDate());
            pstmt.setString(4, ticket.getTicketStatus().toString()); // Assuming ticketStatus is an enum
            pstmt.setString(5, ticket.getDescription());
            pstmt.setBoolean(6, ticket.isPosted());
            pstmt.setBoolean(7, ticket.isAwaitingDescription());
            pstmt.setString(8, ticket.getDepartment());
            pstmt.setLong(9, ticket.getFreelancerId());
            pstmt.setLong(10, ticket.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Ticket with ID " + ticket.getId() + " was updated successfully.");
            } else {
                System.out.println("No ticket found with ID " + ticket.getId() + " or no update was necessary.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating ticket: " + e.getMessage());
            // Handle exception or rethrow it as needed
        }
    }

    public void deleteAllTicketsFromDatabase() {
        String sql = "DELETE FROM Tickets;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("All tickets were deleted successfully. Rows affected: " + affectedRows);
            } else {
                System.out.println("No tickets were deleted (table might already be empty).");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting tickets: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<Ticket> downloadTicketsFromDatabase() {
        //TODO
        return new ArrayList<>();
    }

    public void uploadAllTicketsToDatabase(List<Ticket> tickets) {
        String sql = "UPDATE Tickets SET " +
                "clientId = ?, " +
                "channelId = ?, " +
                "creationDate = ?, " +
                "ticketStatus = ?, " +
                "description = ?, " +
                "posted = ?, " +
                "awaitingDescription = ?, " +
                "department = ?, " +
                "freelancerId = ? " +
                "WHERE id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Ticket ticket : tickets) {
                pstmt.setLong(1, ticket.getClientId());
                pstmt.setLong(2, ticket.getChannelId());
                pstmt.setLong(3, ticket.getCreationDate());
                pstmt.setString(4, ticket.getTicketStatus().toString()); // Assuming ticketStatus is an enum
                pstmt.setString(5, ticket.getDescription());
                pstmt.setBoolean(6, ticket.isPosted());
                pstmt.setBoolean(7, ticket.isAwaitingDescription());
                pstmt.setString(8, ticket.getDepartment());
                pstmt.setLong(9, ticket.getFreelancerId());
                pstmt.setLong(10, ticket.getId());

                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("All tickets updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating tickets: " + e.getMessage());
            // Handle exception or rethrow it as needed
        }
    }
    public void createTicketDatabase(Ticket ticket) {
        String sql = "INSERT INTO Tickets (clientId, channelId, creationDate, ticketStatus, description, posted, awaitingDescription, department, freelancerId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, ticket.getClientId());
            pstmt.setLong(2, ticket.getChannelId());
            pstmt.setLong(3, ticket.getCreationDate());
            pstmt.setString(4, ticket.getTicketStatus().toString()); // Assuming ticketStatus is an enum
            pstmt.setString(5, ticket.getDescription());
            pstmt.setBoolean(6, ticket.isPosted());
            pstmt.setBoolean(7, ticket.isAwaitingDescription());
            pstmt.setString(8, ticket.getDepartment());
            pstmt.setLong(9, ticket.getFreelancerId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("New ticket created successfully.");
            } else {
                System.out.println("No new ticket was created.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating a new ticket: " + e.getMessage());
            // Handle exception or rethrow it as needed
        }
    }
    

}
