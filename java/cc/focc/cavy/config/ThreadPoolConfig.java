package cc.focc.cavy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "backUpJobPool")
    public ThreadPoolExecutor backUpJobPool() {
        return new ThreadPoolExecutor(
                5,
                5,
                5,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                new CustomizableThreadFactory("backUpJob"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }


}
