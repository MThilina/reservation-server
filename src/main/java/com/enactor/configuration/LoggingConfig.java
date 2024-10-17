package com.enactor.configuration;

import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;

public class LoggingConfig {
    public static void setup() {
        // Remove existing handlers attached to j.u.l root logger
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        // Install the SLF4J bridge
        SLF4JBridgeHandler.install();
    }
}
