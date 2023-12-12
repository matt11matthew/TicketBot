package me.matthewe.ticket.discord.ticket;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * Created by Matthew E on 12/10/2023 at 3:08 PM for the project TicketBot
 */
public class Ticket {
    private long id;
    private long clientId;
    private long channelId;
    private long creationDate;
    private TicketStatus ticketStatus;
    private String description;
    private TextChannel textChannel;
    private boolean posted;
    private boolean awaitingDescription ;
    private String department;
    private long freelancerId;

    public Ticket(long id, long clientId, long channelId, long creationDate, TicketStatus ticketStatus, String description, TextChannel textChannel, boolean posted, boolean awaitingDescription, String department, long freelancerId) {
        this.id = id;
        this.clientId = clientId;
        this.channelId = channelId;
        this.creationDate = creationDate;
        this.ticketStatus = ticketStatus;
        this.description = description;
        this.textChannel = textChannel;
        this.posted = posted;
        this.awaitingDescription = awaitingDescription;
        this.department = department;
        this.freelancerId = freelancerId;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getFreelancerId() {
        return freelancerId;
    }

    public void setFreelancerId(long freelancerId) {
        this.freelancerId = freelancerId;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public long getId() {
        return id;
    }

    public long getClientId() {
        return clientId;
    }

    public long getChannelId() {
        return channelId;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public boolean isAwaitingDescription() {
        return awaitingDescription;
    }

    public void setWaitingDescription(boolean awaitingDescription) {
        this.awaitingDescription = awaitingDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPosted() {
        return posted;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

