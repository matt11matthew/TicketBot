package me.matthewe.ticket.discord.ticket;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * Created by Matthew E on 12/10/2023 at 3:08 PM for the project TicketBot
 */
public class Ticket {
    private long id;
    private long clientId;
    private long channelId;

    private TicketStatus ticketStatus;
    private TextChannel textChannel;

    public Ticket(long id, long clientId, long channelId, TicketStatus ticketStatus) {
        this.id = id;
        this.clientId = clientId;
        this.channelId = channelId;
        this.ticketStatus = ticketStatus;
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
}

