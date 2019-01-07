package ru.spb.avetall.messagingdemo.controllers;

import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import static ru.spb.avetall.messagingdemo.configuration.JmsConfiguration.ORDER_QUEUE;

@RestController()
public class MainControllerRestApi {

    private static Logger log = LoggerFactory.getLogger(MainControllerRestApi.class);

    private JmsTemplate jmsTemplate;

    @Autowired
    public MainControllerRestApi(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;

    }

    @RequestMapping(method = RequestMethod.GET, path = "/metric")
    public void putMetric(String message) {
        send(message);
    }

    private void send(String myMessage) {
        log.info("sending with convertAndSend() to queue <" + myMessage + ">");
        jmsTemplate.convertAndSend(ORDER_QUEUE, myMessage);

    }

}
