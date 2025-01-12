//package com.otsMail.component;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//@Component
//public class H2DatabaseBackupRestoreRunner {
//
//    private final H2DatabaseService h2DatabaseService;
//
//    public H2DatabaseBackupRestoreRunner(H2DatabaseService h2DatabaseService) {
//        this.h2DatabaseService = h2DatabaseService;
//    }
//
//    @Bean
//    public CommandLineRunner run() {
//        return args -> {
//            // Restore the database at startup
//            h2DatabaseService.restoreDatabase();
//
//            // Add shutdown hook to take a backup
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                h2DatabaseService.backupDatabase();
//            }));
//        };
//    }
//}
