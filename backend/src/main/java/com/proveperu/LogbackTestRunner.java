package com.proveperu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogbackTestRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.debug("Mensaje DEBUG");
        log.info("Mensaje INFO");
        log.warn("Mensaje WARN");
        log.error("Mensaje ERROR");
    }
}