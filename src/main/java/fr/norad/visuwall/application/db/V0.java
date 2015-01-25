package fr.norad.visuwall.application.db;

import static fr.norad.updater.Version.V;
import com.datastax.driver.core.Session;
import fr.norad.updater.ApplicationVersion;
import fr.norad.updater.Update;

public class V0 extends ApplicationVersion {

    public static final long DEFAULT_TTL_MONTH = 6;

    public V0(final Session session) {
        super(V(0), new Update("First version of tables") {
                    @Override
                    public void runUpdate() {
//                        session.execute("CREATE TABLE IF NOT EXISTS messages.latest (" +
//                                "    user_id blob," +
//                                "    bucket text," +
//                                "    thread_id blob," +
//                                "    interlocutor_avatar text," +
//                                "    interlocutor_birthday timestamp," +
//                                "    interlocutor_gender text," +
//                                "    interlocutor_id blob," +
//                                "    interlocutor_name text," +
//                                "    last_message_date timestamp," +
//                                "    message_count bigint," +
//                                "    offer_id blob," +
//                                "    previous_bucket int static," +
//                                "    private_thread boolean," +
//                                "    read boolean," +
//                                "    trip_date timestamp," +
//                                "    trip_from text," +
//                                "    trip_id blob," +
//                                "    trip_to text," +
//                                "    PRIMARY KEY ((user_id, bucket), thread_id)" +
//                                ") WITH CLUSTERING ORDER BY (thread_id ASC)" +
//                                "    AND bloom_filter_fp_chance = 0.01" +
//                                "    AND caching = '{\"keys\":\"ALL\", \"rows_per_partition\":\"NONE\"}'" +
//                                "    AND comment = 'Create table for entity \"com.blablacar.api.messages.storage.entity.MessageLatestEntity\"'" +
//                                "    AND compaction = {'min_threshold': '4', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32'}" +
//                                "    AND compression = {'sstable_compression': 'org.apache.cassandra.io.compress.LZ4Compressor'}" +
//                                "    AND dclocal_read_repair_chance = 0.1" +
//                                "    AND default_time_to_live = 0" +
//                                "    AND gc_grace_seconds = 864000" +
//                                "    AND max_index_interval = 2048" +
//                                "    AND memtable_flush_period_in_ms = 0" +
//                                "    AND min_index_interval = 128" +
//                                "    AND read_repair_chance = 0.0" +
//                                "    AND speculative_retry = '99.0PERCENTILE';");
//
//                        session.execute("CREATE TABLE IF NOT EXISTS messages.unread_count (" +
//                                "    user_id blob," +
//                                "    bucket text," +
//                                "    type text," +
//                                "    interlocutor_id blob," +
//                                "    \"count\" counter," +
//                                "    PRIMARY KEY ((user_id, bucket), type, interlocutor_id)" +
//                                ") WITH CLUSTERING ORDER BY (type ASC, interlocutor_id ASC)" +
//                                "    AND bloom_filter_fp_chance = 0.01" +
//                                "    AND caching = '{\"keys\":\"ALL\", \"rows_per_partition\":\"NONE\"}'" +
//                                "    AND comment = 'Create table for entity \"com.blablacar.api.messages.storage.entity.MessagesUnreadCountEntity\"'" +
//                                "    AND compaction = {'min_threshold': '4', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32'}" +
//                                "    AND compression = {'sstable_compression': 'org.apache.cassandra.io.compress.LZ4Compressor'}" +
//                                "    AND dclocal_read_repair_chance = 0.1" +
//                                "    AND default_time_to_live = " + ofDays(31).getSeconds() * DEFAULT_TTL_MONTH +
//                                "    AND gc_grace_seconds = 864000" +
//                                "    AND max_index_interval = 2048" +
//                                "    AND memtable_flush_period_in_ms = 0" +
//                                "    AND min_index_interval = 128" +
//                                "    AND read_repair_chance = 0.0" +
//                                "    AND speculative_retry = '99.0PERCENTILE';");
//
//                        session.execute("CREATE TABLE IF NOT EXISTS messages.private_timeline (" +
//                                "    user_id blob," +
//                                "    thread_id blob," +
//                                "    created_at timestamp," +
//                                "    id blob," +
//                                "    interlocutor_avatar text," +
//                                "    interlocutor_birthday timestamp," +
//                                "    interlocutor_gender text," +
//                                "    interlocutor_id blob," +
//                                "    interlocutor_name text," +
//                                "    offer_id blob," +
//                                "    read boolean," +
//                                "    received boolean," +
//                                "    text text," +
//                                "    trip_date timestamp," +
//                                "    trip_from text," +
//                                "    trip_id blob," +
//                                "    trip_to text," +
//                                "    PRIMARY KEY ((user_id, thread_id), created_at)" +
//                                ") WITH CLUSTERING ORDER BY (created_at ASC)" +
//                                "    AND bloom_filter_fp_chance = 0.01" +
//                                "    AND caching = '{\"keys\":\"ALL\", \"rows_per_partition\":\"NONE\"}'" +
//                                "    AND comment = 'Create table for entity \"com.blablacar.api.messages.storage.entity.PrivateMessageTimelineEntity\"'" +
//                                "    AND compaction = {'min_threshold': '4', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32'}" +
//                                "    AND compression = {'sstable_compression': 'org.apache.cassandra.io.compress.LZ4Compressor'}" +
//                                "    AND dclocal_read_repair_chance = 0.1" +
//                                "    AND default_time_to_live = 0" +
//                                "    AND gc_grace_seconds = 864000" +
//                                "    AND max_index_interval = 2048" +
//                                "    AND memtable_flush_period_in_ms = 0" +
//                                "    AND min_index_interval = 128" +
//                                "    AND read_repair_chance = 0.0" +
//                                "    AND speculative_retry = '99.0PERCENTILE';");
//
//                        session.execute("CREATE TABLE IF NOT EXISTS messages.public_offer (" +
//                                "    offer_id blob," +
//                                "    thread_id blob," +
//                                "    message_id blob," +
//                                "    created_at timestamp," +
//                                "    driver_id blob," +
//                                "    from_driver boolean," +
//                                "    guest_avatar text," +
//                                "    guest_birthday timestamp," +
//                                "    guest_gender text," +
//                                "    guest_id blob," +
//                                "    guest_name text," +
//                                "    moderated text," +
//                                "    read_by_receiver boolean," +
//                                "    text text," +
//                                "    trip_date timestamp," +
//                                "    trip_from text," +
//                                "    trip_id blob," +
//                                "    trip_to text," +
//                                "    PRIMARY KEY (offer_id, thread_id, message_id)" +
//                                ") WITH CLUSTERING ORDER BY (thread_id ASC, message_id ASC)" +
//                                "    AND bloom_filter_fp_chance = 0.01" +
//                                "    AND caching = '{\"keys\":\"ALL\", \"rows_per_partition\":\"NONE\"}'" +
//                                "    AND comment = 'Create table for entity \"com.blablacar.api.messages.storage.entity.PublicMessageByOfferEntity\"'" +
//                                "    AND compaction = {'min_threshold': '4', 'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32'}" +
//                                "    AND compression = {'sstable_compression': 'org.apache.cassandra.io.compress.LZ4Compressor'}" +
//                                "    AND dclocal_read_repair_chance = 0.1" +
//                                "    AND default_time_to_live = 0" +
//                                "    AND gc_grace_seconds = 864000" +
//                                "    AND max_index_interval = 2048" +
//                                "    AND memtable_flush_period_in_ms = 0" +
//                                "    AND min_index_interval = 128" +
//                                "    AND read_repair_chance = 0.0" +
//                                "    AND speculative_retry = '99.0PERCENTILE';");
//
//                        session.execute("CREATE TABLE messages.public_count(" +
//                                "offer_id blob," +
//                                "count counter," +
//                                "PRIMARY KEY(offer_id))" +
//                                "WITH comment = 'Create table for entity \"com.blablacar.api.messages.storage.entity.PublicCountEntity\"'");
                    }
                }
        );
    }
}