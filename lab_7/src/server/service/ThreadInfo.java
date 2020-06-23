package lab_7.server.service;

import lab_7.server.ticket.Ticket;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * класс для хранения служебной информации для потоков
 */
public class ThreadInfo {
    public SocketAddress address;
    public DatagramChannel channel;
    public ArrayList<Ticket> tickets;
    public ZonedDateTime first_date;

    public ThreadInfo(SocketAddress address,
                      DatagramChannel channel,
                      ArrayList<Ticket> tickets,
                      ZonedDateTime first_date) {
        this.address = address;
        this.channel = channel;
        this.tickets = tickets;
        this.first_date = first_date;
    }
}
