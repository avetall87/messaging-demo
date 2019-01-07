package ru.spb.avetall.messagingdemo.messaginqueues.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_metrics.metric")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Metric implements Serializable {

    @Id
    @GeneratedValue(/*strategy = GenerationType.SEQUENCE*/)
//    @SequenceGenerator(name="seq-gen", sequenceName="MY_SEQ_GEN",initialValue=10, allocationSize=0)
    @Column(name="\"ID\"",unique=true,nullable=false)
    private Long id;

    @Column(name = "value", columnDefinition = "varchar", length = 512)
    private String value;

    @Column(name = "metric_date")
    @ColumnDefault(value = "current_timestamp")
    private LocalDateTime metricDate;

    @Column(name = "is_send")
    @ColumnDefault(value = "false")
    private boolean isSend;

}
