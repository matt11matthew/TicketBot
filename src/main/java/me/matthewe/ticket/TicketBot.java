package me.matthewe.ticket;

import me.matthewe.ticket.handler.HandlerManager;
import me.matthewe.ticket.io.BasicLogger;

import java.util.logging.Logger;

/**
 * Created by Matthew E on 12/10/2023 at 11:58 AM for the project TicketBot
 */
public class TicketBot {
    private final BasicLogger logger;
    private boolean running;

    private HandlerManager handlerManager;
    public TicketBot() {
        this.handlerManager = new HandlerManager(this);
        this.running = false;
        this.logger = new BasicLogger(this);
    }

    private void registerHandlers() {

    }
    public void start() {
        if (this.running) {
            logger.error("The bot is already running");
            return;
        }
        this.registerHandlers();
        this.handlerManager.enableHandlers();
        this.running = true;

    }

    public BasicLogger getLogger() {
        return logger;
    }
}
