package com.banking.service.impl;

import com.banking.entity.AuditLog;
import com.banking.repository.AuditLogRepository;
import com.banking.service.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void logAction(String userEmail, String action, String details, String ipAddress) {
        AuditLog log = new AuditLog(userEmail, action, details, ipAddress != null ? ipAddress : "127.0.0.1");
        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }
}
