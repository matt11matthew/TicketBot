package me.matthewe.ticket.handler;

import me.matthewe.ticket.Main;
import me.matthewe.ticket.TicketBot;

/**
 * Created by Matthew E on 12/10/2023 at 11:54 AM for the project TicketBot
 */
public abstract class Handler {
    private TicketBot ticketBot;

    public Handler(TicketBot ticketBot) {
        this.ticketBot = ticketBot;
    }

    public abstract void onEnable();
    public abstract void onDisable();

}
