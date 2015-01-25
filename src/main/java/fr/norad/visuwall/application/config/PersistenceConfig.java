package fr.norad.visuwall.application.config;

import static com.datastax.driver.core.Cluster.builder;
import static com.datastax.driver.core.ProtocolOptions.Compression.LZ4;
import static com.datastax.driver.core.ProtocolOptions.Compression.NONE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import fr.norad.visuwall.application.config.DatabaseConfig.Cassandra;
import fr.norad.visuwall.application.db.DbUpdater;
import info.archinnov.achilles.persistence.AsyncManager;
import info.archinnov.achilles.persistence.PersistenceManager;
import info.archinnov.achilles.persistence.PersistenceManagerFactory;
import info.archinnov.achilles.persistence.PersistenceManagerFactory.PersistenceManagerFactoryBuilder;
import info.archinnov.achilles.type.ConsistencyLevel;

@Configuration
public class PersistenceConfig {

    @Value("${cassandra.cql3.compression:false}")
    private boolean activateCQL3Compression;

    @Value("${cassandra.keyspace:messages}")
    String keyspace;

    @Autowired
    private Cassandra cassandra;

    @Bean(destroyMethod = "close")
    public Cluster cqlCluster() {
        String[] contactPointsList = StringUtils.split(cassandra.getHosts(), ",");
        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setKeepAlive(true);

        Cluster cluster = builder().addContactPoints(contactPointsList).withPort(cassandra.getPort())
                .withCompression(activateCQL3Compression ? LZ4 : NONE)
//                .withLoadBalancingPolicy(new DCAwareRoundRobinPolicy(localDataCenterName)) TODO
                .withSocketOptions(socketOptions)
                .withClusterName("visuwall")
                .build();

        updateDb(cluster);
        return cluster;
    }


    @Bean(destroyMethod = "close")
    public Session session() {
        return cqlCluster().connect();
    }

    @Bean
    public PersistenceManager persistenceManager() {
        return persistenceManagerFactory().createPersistenceManager();
    }

    @Bean
    public AsyncManager asyncManager() {
        return persistenceManagerFactory().createAsyncManager();
    }

    @Bean
    public PersistenceManagerFactory persistenceManagerFactory() {
        PersistenceManagerFactory pmFactory = PersistenceManagerFactoryBuilder.builder(cqlCluster())
                .withEntityPackages("com.blablacar.api.messages")
                .withDefaultReadConsistency(ConsistencyLevel.valueOf("LOCAL_ONE"))
                .withDefaultWriteConsistency(ConsistencyLevel.valueOf("LOCAL_ONE"))
//                .withExecutorServiceMinThreadCount(min) TODO
//                .withExecutorServiceMaxThreadCount(max)
                .withKeyspaceName("messages")
                .forceTableCreation(false)
                .build();

        return pmFactory;
    }

    //////////////////////////////////

    private void updateDb(Cluster cluster) {
        Session session = cluster.connect();
        session.execute("USE " + keyspace);
        new DbUpdater(session).updateToLatest();
        session.close();
    }

}
