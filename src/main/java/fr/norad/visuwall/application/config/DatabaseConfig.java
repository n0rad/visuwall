package fr.norad.visuwall.application.config;


import static com.datastax.driver.core.Cluster.builder;
import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.cassandra.config.Config;
import org.apache.cassandra.config.Config.CommitLogSync;
import org.apache.cassandra.config.ConfigurationLoader;
import org.apache.cassandra.config.SeedProviderDef;
import org.apache.cassandra.service.CassandraDaemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import fr.norad.core.io.PortFinder;
import fr.norad.visuwall.application.VisuwallHome;

@Component
@Configuration
public class DatabaseConfig {

    private String cql3Hosts;
    private Integer cql3Port;

    @Value("${cassandra.cql3.local.data.center.name:yop}")
    private String localDataCenterName; // TODO

    @Autowired
    private VisuwallHome home;

    @Bean
    public Cassandra cassandra() {
        return new CassandraEmbedded().start();
    }

    public class Cassandra {
        public String getHosts() {
            return cql3Hosts;
        }

        public Integer getPort() {
            return cql3Port;
        }

    }

    public class CassandraEmbedded extends Cassandra {

        private final Logger log = LoggerFactory.getLogger(getClass());

        private ExecutorService executor;

        public CassandraEmbedded() {
        }

        public CassandraEmbedded start() {
            if (isAlreadyRunning()) {
                log.warn("Cassandra is already running, not starting new one");
                return this;
            }

            log.info("Starting Embedded Cassandra...");
            prepare();
            run();
            initializeKeyspace();
            return this;
        }

        private void initializeKeyspace() {
            Cluster cluster = builder().addContactPoints(getHosts()).withPort(getPort()).build();
            Session session = cluster.connect();
            session.execute("CREATE KEYSPACE IF NOT EXISTS messages WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}");
            session.close();
            cluster.close();
        }

        private void run() {
            final CountDownLatch startupLatch = new CountDownLatch(1);
            executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                CassandraDaemon cassandraDaemon = new CassandraDaemon();
                cassandraDaemon.activate();
                startupLatch.countDown();
            });
            try {
                startupLatch.await(30, SECONDS);
            } catch (InterruptedException e) {
                log.error("Timeout starting Cassandra embedded", e);
                throw new IllegalStateException("Timeout starting Cassandra embedded", e);
            }
        }

        private void prepare() {
            CassandraEmbeddedConfigLoader.homeFolder = home.getCassandraEmbeddedDirectory();
            Config config = new CassandraEmbeddedConfigLoader().loadConfig();
            cql3Hosts = "localhost";
            cql3Port = config.native_transport_port;

            new File(config.data_file_directories[0]).mkdirs();
            new File(config.saved_caches_directory).mkdirs();
            new File(config.commitlog_directory).mkdirs();

            System.setProperty("cassandra.config.loader", CassandraEmbeddedConfigLoader.class.getName());
            System.setProperty("cassandra-foreground", "true");
        }


        private boolean isAlreadyRunning() {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                MBeanInfo mBeanInfo = mbs.getMBeanInfo(new ObjectName("org.apache.cassandra.db:type=StorageService"));
                if (mBeanInfo != null) {
                    return true;
                }
                return false;
            } catch (InstanceNotFoundException e) {
                return false;
            } catch (IntrospectionException | MalformedObjectNameException | ReflectionException e) {
                throw new IllegalStateException("Cannot check if cassandra is already running", e);
            }
        }

    }

    public static class CassandraEmbeddedConfigLoader implements ConfigurationLoader {

        private static Config config;
        protected static File homeFolder;

        @Override
        public synchronized Config loadConfig() {
            if (config == null) {
                config = defaultValues();
                // ports
                config.storage_port = PortFinder.randomAvailable();
                config.ssl_storage_port = PortFinder.randomAvailable();
                config.native_transport_port = PortFinder.randomAvailable();
                config.rpc_port = PortFinder.randomAvailable();


                // paths
                String absolutePath = homeFolder.getAbsolutePath();
                config.client_encryption_options.keystore = absolutePath + "/keystore";
                config.data_file_directories = new String[]{absolutePath + "/data"};
                config.commitlog_directory = absolutePath + "/commitlog";
                config.server_encryption_options.keystore = absolutePath + "/keystore";
                config.server_encryption_options.truststore = absolutePath + "/truststore";
                config.client_encryption_options.keystore = absolutePath + "/keystore";
                config.saved_caches_directory = absolutePath + "/saved_caches";
            }
            return config;
        }

        private Config defaultValues() {
            Config config = new Config();
            config.start_native_transport = true;
            config.commitlog_sync = CommitLogSync.periodic;
            config.commitlog_sync_period_in_ms = 10000;
            config.partitioner = org.apache.cassandra.dht.Murmur3Partitioner.class.getName();
            config.endpoint_snitch = "SimpleSnitch";
            LinkedHashMap<String, Object> p = new LinkedHashMap<>();
            p.put("class_name", org.apache.cassandra.locator.SimpleSeedProvider.class.getName());
            p.put("parameters", newArrayList(of("seeds", "127.0.0.1")));
            config.seed_provider = new SeedProviderDef(p);
            return config;
        }
    }

}
