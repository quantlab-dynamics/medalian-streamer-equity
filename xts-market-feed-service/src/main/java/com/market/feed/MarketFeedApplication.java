package com.market.feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MarketFeedApplication implements CommandLineRunner {

    @Autowired
    private Environment environment;

    @Autowired
    private MDScheduler mdScheduler;

    private static final Logger logger = LoggerFactory.getLogger(MarketFeedApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(MarketFeedApplication.class, args);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void run(String... args) throws Exception {
        try {

            ZonedDateTime now = ZonedDateTime.now(); // current time with zone
            ZoneId zone = now.getZone(); // get current zone ID
            String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
            logger.info("Current Time Zone: " + zone);
            logger.info("Current Time: " + formattedTime);
            mdScheduler.executeMDfeed();
        }
        catch (Exception e){
        e.printStackTrace();
        }

    }


}
