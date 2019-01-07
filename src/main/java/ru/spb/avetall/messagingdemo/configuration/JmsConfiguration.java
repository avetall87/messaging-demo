package ru.spb.avetall.messagingdemo.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.DeadLetterStrategy;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableJms
public class JmsConfiguration {

    public static final String ORDER_QUEUE = "order-queue";
    public static final String DEAD_QUEUE = "dead-queue";

    @Value("${jdbc.datasource.url}")
    public String jdbcUrl;

    @Value("${jdbc.datasource.username}")
    public String dbLogin;

    @Value("${jdbc.datasource.password}")
    public String dbPass;



    @Bean
    public DefaultJmsListenerContainerFactory myFactory(DefaultJmsListenerContainerFactoryConfigurer configurer) throws JMSException {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory());
        factory.setTransactionManager(new JmsTransactionManager());
        factory.setMessageConverter(myMessageConverter());
        factory.setConcurrency(String.valueOf(Runtime.getRuntime().availableProcessors()));
        return factory;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public BrokerService broker(JDBCPersistenceAdapter persistenceAdapter) throws Exception {
        final BrokerService broker = new BrokerService();
//        broker.addConnector("tcp://localhost:61616");
//        broker.addConnector("vm://localhost");
//        PersistenceAdapter persistenceAdapter = new KahaDBPersistenceAdapter();
//        File dir = new File("/Users/avetall/Development/Storage/db_volumns/kaha_storage/");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        persistenceAdapter.setDirectory(dir);

        PolicyEntry policyEntry = new PolicyEntry();
        DeadLetterStrategy strategy =  new SharedDeadLetterStrategy();
        ((SharedDeadLetterStrategy) strategy).setDeadLetterQueue(new ActiveMQQueue(DEAD_QUEUE));
        policyEntry.setDeadLetterStrategy(strategy);
        policyEntry.setPersistJMSRedelivered(true);
        policyEntry.setPrioritizedMessages(true);

        PolicyMap policyMap = new PolicyMap();
        policyMap.setDefaultEntry(policyEntry);

        broker.setDestinationPolicy(policyMap);

        broker.setUseShutdownHook(true);
        broker.setPersistenceAdapter(persistenceAdapter);
        broker.setPersistent(true);
        broker.start(true);
        return broker;
    }

    @Bean
    public JDBCPersistenceAdapter getJdbcPersistenceAdapter(DataSource dataSource) throws IOException {
        JDBCPersistenceAdapter adapter = new JDBCPersistenceAdapter();
        adapter.setDataSource(dataSource);
        adapter.setCreateTablesOnStartup(false);
        adapter.setLockAcquireSleepInterval(-1L);
        adapter.setLockKeepAlivePeriod(-1L);
        adapter.setUseLock(false);
        adapter.setBrokerName("pg-broker");
        try {
            adapter.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adapter;
    }

    @Bean
    public DataSource getDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(dbLogin);
        dataSource.setPassword(dbPass);
        return dataSource;
    }

    private MessageConverter myMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        return converter;
    }

    private ConnectionFactory connectionFactory() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.createConnection();
        factory.setDispatchAsync(true);
        factory.setMaxThreadPoolSize(Runtime.getRuntime().availableProcessors());
        return factory;
    }
}
