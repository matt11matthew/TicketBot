package me.matthewe.ticket.io.console.command;

import me.matthewe.ticket.TicketBot;

/**
 * Created by Matthew E on 12/10/2023 at 12:10 PM for the project TicketBot
 */
public abstract class ConsoleCommand {
    private String name;
    private TicketBot ticketBot;

    protected ConsoleCommand(String name, TicketBot ticketBot) {
        this.name = name;
        this.ticketBot = ticketBot;
    }

    public String getName() {
        return name;
    }

    public  abstract void onCommand(String[] args);
}
