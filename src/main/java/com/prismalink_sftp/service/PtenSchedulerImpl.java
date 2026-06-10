//package com.prismalink_sftp.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PtenSchedulerImpl implements PtenScheduler {
//
//    private final PtenService ptenService;
//
//    @Autowired
//    public PtenSchedulerImpl(PtenService ptenService) {
//        this.ptenService = ptenService;
//    }
//
//    @Scheduled(fixedDelay = 300000) // 5 menit
//    @Override
//    public void checkFolder() {
//        ptenService.processNewFolders();
//    }
//}
