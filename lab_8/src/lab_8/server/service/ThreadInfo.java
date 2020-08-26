package lab_8.server.service;

import lab_8.server.ticket.Ticket;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * класс для хранения служебной информации для потоков
 */
public class ThreadInfo {
    public SocketAddress address;
    public HashMap<String, SocketAddress> allAddresses;
    public DatagramChannel channel;
    public ArrayList<Ticket> tickets;
    public ZonedDateTime first_date;

    public ThreadInfo(SocketAddress address,
                      HashMap<String, SocketAddress> allAddresses,
                      DatagramChannel channel,
                      ArrayList<Ticket> tickets,
                      ZonedDateTime first_date) {
        this.address = address;
        this.allAddresses = allAddresses;
        this.channel = channel;
        this.tickets = tickets;
        this.first_date = first_date;
    }
}
