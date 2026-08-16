package com.banking.service;

import com.banking.entity.AuditLog;

import java.util.List;

public interface AuditLogService {
    void logAction(String userEmail, String action, String details, String ipAddress);
    List<AuditLog> getRecentAuditLogs();
}
