package me.matthewe.ticket.io;

import me.matthewe.ticket.TicketBot;

/**
 * Created by Matthew E on 12/10/2023 at 12:02 PM for the project TicketBot
 */
public class BasicLogger {
    private TicketBot ticketBot;

    public BasicLogger(TicketBot ticketBot) {
        this.ticketBot = ticketBot;
    }

    public void info(String text) {
        System.out.println("[TicketBot] " + text);
    }

    public void printLine(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print("=");
        }
        System.out.println();
    }

    public void error(String text) {
        System.err.println("[TicketBot] " + text);
    }
}
