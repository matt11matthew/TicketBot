package me.matthewe.ticket;

import java.util.Scanner;

/**
 * Created by Matthew E on 12/10/2023 at 11:54 AM for the project TicketBot
 *
 * This class  exists for handling system processes
 */
public class Main {


    public static void main(String[] args) {
        TicketBot ticketBot = new TicketBot();
        ticketBot.start();

        Thread shutdownHook = new Thread(ticketBot::handleShutdown);
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        /*
        Handles commands
         */
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String next = scanner.nextLine();
            if (next.isEmpty()) continue;
            ticketBot.onMessage(next);
        }

    }
}
