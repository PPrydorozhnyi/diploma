package com.diploma.backend.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diploma.backend.error.exceptions.ResourceNotFoundException;
import com.diploma.backend.model.entities.Comment;
import com.diploma.backend.model.entities.Ticket;
import com.diploma.backend.model.entities.TicketRelation;
import com.diploma.backend.model.enums.RelationType;
import com.diploma.backend.model.enums.TicketType;
import com.diploma.backend.model.pojo.ChangeStatus;
import com.diploma.backend.repository.TicketRepository;
import com.diploma.backend.service.CommentService;
import com.diploma.backend.service.TicketRelationService;
import com.diploma.backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final CommentService commentService;
    private final TicketRelationService relationService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Ticket createTicket(Ticket ticket, Integer parentTicketId) {
        final Ticket created = ticketRepository.save(ticket);
        if (parentTicketId != null) {
            final Ticket parent = getTicket(parentTicketId);

            TicketRelation parentRelation = new TicketRelation();
            parentRelation.setSource(parent);
            parentRelation.setTarget(created);
            parentRelation.setRelationType(RelationType.PARENT);
            relationService.create(parentRelation);


            TicketRelation childRelation = new TicketRelation();
            childRelation.setTarget(parent);
            childRelation.setSource(created);
            childRelation.setRelationType(RelationType.CHILD);
            relationService.create(childRelation);
        }
        return created;
    }

    @Override
    public Ticket updateTicket(Integer id, Ticket ticket) {
        Ticket ticketFromCache = ticketRepository.getOne(id);
        BeanUtils.copyProperties(ticket, ticketFromCache, "id", "type", "reporter",
                "relations");
        return ticketRepository.save(ticketFromCache);
    }

    @Override
    public Ticket getTicket(Integer id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteTicket(Integer id) {
        ticketRepository.deleteTicketById(id);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getAllEpics() {
        return ticketRepository.getAllTicketByType(TicketType.EPIC);
    }

    @Override
    public List<Ticket> getTicketsByProjectId(Integer projectId) {
        return ticketRepository.getTicketsByProjectId(projectId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Ticket changeStatus(ChangeStatus changeStatus) {
        Ticket ticket = getTicket(changeStatus.getTicketId());
        ticket.setStatus(changeStatus.getNewStatus());
        ticketRepository.save(ticket);
        if (StringUtils.isNotBlank(changeStatus.getComment())) {
            Comment comment = new Comment(changeStatus.getComment(), changeStatus.getTicketId());
            commentService.createComment(comment);
        }
        return ticket;
    }

}
