package ru.spb.avetall.messagingdemo.messaginqueues.service;

import org.springframework.data.repository.CrudRepository;
import ru.spb.avetall.messagingdemo.messaginqueues.model.Metric;

public interface MessageService extends CrudRepository<Metric, Long> {

}
