package se.sundsvall.businessengagements.integration.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler for removing expired cache entries from the database.
 * Checks for expired entities every half hour.
 */
@Component
@EnableScheduling
public class DbScheduledTasks {
    
    private static final Logger LOG = LoggerFactory.getLogger(DbScheduledTasks.class);
    
    /**
     * How long entities should live in the cache/db
     * @return
     */
    @Value("${bolagsverket.entity.ttl}")
    private long entityCacheTtlInSeconds;
    
    private final EngagementsCacheRepository repository;
    
    public DbScheduledTasks(final EngagementsCacheRepository repository) {
        this.repository = repository;
    }
    
    /**
     * <pre>
     * Checks for expired entries every other minutes.
     * Since it's the timestamp in the DB that determines if an entity should be removed or not
     * it's not that important with the interval.
     * </pre>
     */
    @Scheduled(initialDelay = 1, fixedRate = 2, timeUnit = TimeUnit.MINUTES)   //delay it since we don't want it to run immediately on startup
    @Transactional
    void removeOldCacheEntries() {
        LOG.debug("Checking for expired entities");
        LocalDateTime deleteBefore = LocalDateTime.now().minus(entityCacheTtlInSeconds, ChronoUnit.SECONDS);
        int amountRemoved = repository.deleteAllByUpdatedBefore(deleteBefore);
        
        //Don't clutter logs if we don't have anything to remove.
        if(amountRemoved > 0) {
            LOG.info("Scheduled task found {} old entities and removed them", amountRemoved);
        }
    }
}
