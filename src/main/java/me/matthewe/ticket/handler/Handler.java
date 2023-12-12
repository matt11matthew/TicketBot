package me.matthewe.ticket.handler;

import me.matthewe.ticket.Main;
import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;

/**
 * Created by Matthew E on 12/10/2023 at 11:54 AM for the project TicketBot
 */
public abstract class Handler {
    protected TicketBot ticketBot;
    protected Config config;

    protected int shutdownPriority;

    public Handler(TicketBot ticketBot, Config config) {
        this.ticketBot = ticketBot;
        this.config = config;
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public int getShutdownPriority() {
        return shutdownPriority;
    }

}
