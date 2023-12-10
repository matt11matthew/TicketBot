package me.matthewe.ticket.handler;

import me.matthewe.ticket.Main;
import me.matthewe.ticket.TicketBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 12/10/2023 at 11:55 AM for the project TicketBot
 */
public class HandlerManager {
    private Map<String, Handler> handlerMap;

    private TicketBot ticketBot;

    public HandlerManager(TicketBot ticketBot) {
        this.ticketBot = ticketBot;
        this.handlerMap = new HashMap<>();
    }


    public <T extends Handler> void registerHandler(T handler) {
        if (handlerMap.containsKey(handler.getClass().getSimpleName())){
            return;
        }
        handlerMap.put(handler.getClass().getSimpleName(), handler);
    }

    public void enableHandlers() {
        this.ticketBot.getLogger().info("enabling");
        this.handlerMap.values().forEach(Handler::onEnable);
    }
    public void disableHandlers() {
        this.handlerMap.values().forEach(Handler::onEnable);
    }
}
