package se.sundsvall.businessengagements.integration.db;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class, SoftAssertionsExtension.class })
class DbScheduledTasksTest {
    
    @Mock
    private EngagementsCacheRepository mockRepository;
    
    private DbScheduledTasks scheduledTasks;
    
    @BeforeEach
    void setup() {
        scheduledTasks = new DbScheduledTasks(mockRepository);
    }
    
    @Test
    void testRemoveEntity_whenTtlHasBeenPassed() {
        when(mockRepository.deleteAllByUpdatedBefore(any(LocalDateTime.class))).thenReturn(1);
        scheduledTasks.removeOldCacheEntries();
        verify(mockRepository, times(1)).deleteAllByUpdatedBefore(any(LocalDateTime.class));
    }
    
    @Test
    void testShouldNotRemoveEntity_whenTtlHasNotBeenPassed() {
        when(mockRepository.deleteAllByUpdatedBefore(any(LocalDateTime.class))).thenReturn(0);
        scheduledTasks.removeOldCacheEntries();
        verify(mockRepository, times(1)).deleteAllByUpdatedBefore(any(LocalDateTime.class));
    }
}