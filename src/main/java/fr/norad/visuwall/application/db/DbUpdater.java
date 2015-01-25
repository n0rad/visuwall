package fr.norad.visuwall.application.db;

import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static fr.norad.visuwall.application.Visuwall.VISUWALL_NAME;
import java.util.HashSet;
import java.util.Set;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import fr.norad.updater.ApplicationUpdater;
import fr.norad.updater.Version;

public class DbUpdater extends ApplicationUpdater {

    private final Session session;
    private final PreparedStatement updateStatement;
    private final PreparedStatement selectStatement;

    public DbUpdater(Session session) {
        super(VISUWALL_NAME, new V0(session));
        this.session = session;
        session.execute("CREATE TABLE IF NOT EXISTS version (version text PRIMARY KEY, names set<text>)");
//        session.execute("CREATE TABLE IF NOT EXISTS version_lock (lock text PRIMARY KEY) with default_time_to_live = 30");
        updateStatement = session.prepare("INSERT INTO version(version, names) VALUES (?, ?) IF NOT EXISTS");
        selectStatement = session.prepare("SELECT names FROM version WHERE version = ?");
    }

//    private void create() {
//        // INSERT INTO version (name, lockId) VALUES (?, ?) IF NOT EXISTS USING TTL ?;
//        ResultSet result = null;
//        if (result.one().getBool("[applied]")) {
//            // locked
//        } else {
//            // fail
//        }
//
//        // UPDATE USING TTL ? SET lockid=? where name=? IF lockId=?;
//
//        // DELETE FROM clusterlock WHERE name=? IF lockId=?;
//    }

    @Override
    protected Version getCurrentVersion() {
        try {
            Row version = session.execute(select("version").from("version")).one();
            if (version == null) {
                return null;
            }
            return Version.parse(version.getString(0));
        } catch (InvalidQueryException e) {
            return null;
        }
    }

    @Override
    protected Set<String> getUpdatedNames(Version version) {
        ResultSet res = session.execute(selectStatement.bind(version.toString()));
        if (res.isExhausted()) {
            return null;
        }
        Set<String> set = res.one().getSet("names", String.class);
        return set;
    }

    @Override
    protected void addUpdatedName(Version version, String name) {
        HashSet<Object> hashSet = new HashSet<>();
        hashSet.add(name);
        Set<String> updatedNames = getUpdatedNames(version);
        if (updatedNames != null) {
            hashSet.addAll(updatedNames);
        }
        session.execute(updateStatement.bind(version.toString(), hashSet));
    }
}
