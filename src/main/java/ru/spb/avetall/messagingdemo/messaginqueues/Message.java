package ru.spb.avetall.messagingdemo.messaginqueues;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spb.avetall.messagingdemo.messaginqueues.model.Metric;
import ru.spb.avetall.messagingdemo.messaginqueues.service.MessageService;

import javax.jms.Session;

import java.time.LocalDateTime;

import static ru.spb.avetall.messagingdemo.configuration.JmsConfiguration.DEAD_QUEUE;
import static ru.spb.avetall.messagingdemo.configuration.JmsConfiguration.ORDER_QUEUE;

@Component
public class Message {

    private static Logger log = LoggerFactory.getLogger(Message.class);

    private MessageService messageService;

    @Autowired
    public Message(MessageService messageService) {
        this.messageService = messageService;
    }

    @JmsListener(destination = ORDER_QUEUE)
    public void receiveMessage(@Payload String order,
                               @Headers MessageHeaders headers,
                               String message, Session session) {
        try {
            System.out.println(10/0);
            log.info("received <" + order + ">");
            log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
            log.info("######          Message Details           #####");
            log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
            log.info("headers: " + headers);
            log.info("message: " + message);
            log.info("session: " + session);
            log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
            messageService.save(Metric.builder().value(message).build());
            session.commit();
        } catch (Throwable th) {
            rollbackJmsSession(session, th);
        }
    }

    @JmsListener(destination = DEAD_QUEUE)
    public void deadReceiveMessage(
                               @Payload String order,
                               @Headers MessageHeaders headers,
                               String message, Session session) {
        try {
            messageService.save(Metric.builder().value(message).metricDate(LocalDateTime.now()).build());
            log.info("received from dead list <" + message + ">");
            session.commit();
        } catch (Throwable th) {
            rollbackJmsSession(session, th);

        }
    }

    private void rollbackJmsSession(Session session, Throwable th) {
        try {
            session.rollback();
            log.error("Session:" + session+" was rolled back",th.getMessage());
        } catch (Exception e) {
            log.error("Rolled back exception for session:"+ session, e);
        }
    }

}
