package se.sundsvall.businessengagements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import se.sundsvall.dept44.ServiceApplication;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

@ServiceApplication
@ConfigurationPropertiesScan("se.sundsvall.businessengagements")
public class BusinessEngagements {
    public static void main(String[] args) {
        SpringApplication.run(BusinessEngagements.class, args);
        printMemoryStatistics();
    }
    
    public static void printMemoryStatistics() {
        
        int mb = 1024*1024;
        
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        
        long xmx = memoryMXBean.getHeapMemoryUsage().getMax() / mb;
        long xms = memoryMXBean.getHeapMemoryUsage().getInit() / mb;
        long used = memoryMXBean.getHeapMemoryUsage().getUsed() / mb;
    
        System.out.println("Xmx (max): " + xmx + " MB.");
        System.out.println("Xms (initial): " + xms +" MB.");
        System.out.println("Used: " + used + " MB.");
    }
}
