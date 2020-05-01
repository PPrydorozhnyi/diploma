package com.diploma.backend.service;

import java.util.List;

import com.diploma.backend.model.entities.Ticket;

public interface TicketService {

    Ticket createTicket(Ticket ticket);

    Ticket updateTicket(Integer id, Ticket ticket);

    Ticket getTicket(Integer id);

    void deleteTicket(Integer id);

    List<Ticket> getAllTickets();

    List<Ticket> getAllEpics();

    List<Ticket> getTicketsByProjectId(Integer projectId);

}
