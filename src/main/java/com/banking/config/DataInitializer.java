package com.banking.config;

import com.banking.constants.AccountStatus;
import com.banking.constants.AccountType;
import com.banking.constants.RoleName;
import com.banking.constants.TransactionStatus;
import com.banking.constants.TransactionType;
import com.banking.entity.Account;
import com.banking.entity.AuditLog;
import com.banking.entity.Role;
import com.banking.entity.Transaction;
import com.banking.entity.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.AuditLogRepository;
import com.banking.repository.RoleRepository;
import com.banking.repository.TransactionRepository;
import com.banking.repository.UserRepository;
import com.banking.util.AccountNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            AuditLogRepository auditLogRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private User upsertSeedUser(String email, String password, String firstName, String lastName,
            String phone, String address, Role role) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(User::new);

        boolean isNew = user.getId() == null;
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email.toLowerCase(Locale.ROOT));
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setAddress(address);
        user.setEnabled(true);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        User savedUser = userRepository.save(user);
        if (isNew) {
            logger.info("Created seed user: {}", savedUser.getEmail());
        } else {
            logger.info("Updated seed user credentials: {}", savedUser.getEmail());
        }
        return savedUser;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing Seed Data for Apex Online Banking System...");

        // 1. Roles Initializer
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_CUSTOMER)));

        // 2. Admin Seed User: basavaraj@bank.com / Basavaraj@123
        User admin = upsertSeedUser(
                "Basavaraj@bank.com",
                "Basavaraj@123",
                "System",
                "Admin",
                "+1-800-555-0100",
                "Apex Bank HQ, Financial District",
                adminRole);
        logger.info("Ensured Default Admin User: basavaraj@bank.com / Basavaraj@123");

        // 3. Customer 1 Seed User: basu@bank.com / Basu@123
        User basu = upsertSeedUser(
                "basu@bank.com",
                "Basu@123",
                "Basu",
                "Dev",
                "+1-800-555-0101",
                "123 Innovation Way, Suite 400",
                customerRole);

        if (accountRepository.findByUserId(basu.getId()).isEmpty()) {
            Account accountBasu = new Account();
            accountBasu.setAccountNumber("100020003001");
            accountBasu.setBalance(new BigDecimal("50000.00"));
            accountBasu.setAccountType(AccountType.SAVINGS);
            accountBasu.setAccountStatus(AccountStatus.ACTIVE);
            accountBasu.setBranch("Main Financial District Branch");
            accountBasu.setIfscCode("APEX000101");
            accountBasu.setUser(basu);
            Account savedAccBasu = accountRepository.save(accountBasu);

            Transaction txnBasu = new Transaction();
            txnBasu.setTransactionId(AccountNumberGenerator.generateTransactionId());
            txnBasu.setReceiverAccount(savedAccBasu);
            txnBasu.setAmount(new BigDecimal("50000.00"));
            txnBasu.setTransactionType(TransactionType.DEPOSIT);
            txnBasu.setStatus(TransactionStatus.SUCCESS);
            txnBasu.setRemarks("Initial Account Opening Deposit");
            transactionRepository.save(txnBasu);

            logger.info("Created Default Customer 1: basu@bank.com / Basu@123 (Acc: 100020003001)");
        }

        // 4. Customer 2 Seed User: bharath@bank.com / Bharath@123
        User bharath = upsertSeedUser(
                "bharath@bank.com",
                "Bharath@123",
                "Bharath",
                "Kumar",
                "+1-800-555-0102",
                "456 Tech Boulevard, Floor 12",
                customerRole);

        if (accountRepository.findByUserId(bharath.getId()).isEmpty()) {
            Account accountBharath = new Account();
            accountBharath.setAccountNumber("100020003002");
            accountBharath.setBalance(new BigDecimal("75000.50"));
            accountBharath.setAccountType(AccountType.SAVINGS);
            accountBharath.setAccountStatus(AccountStatus.ACTIVE);
            accountBharath.setBranch("Main Financial District Branch");
            accountBharath.setIfscCode("APEX000101");
            accountBharath.setUser(bharath);
            Account savedAccBharath = accountRepository.save(accountBharath);

            Transaction txnBharath = new Transaction();
            txnBharath.setTransactionId(AccountNumberGenerator.generateTransactionId());
            txnBharath.setReceiverAccount(savedAccBharath);
            txnBharath.setAmount(new BigDecimal("75000.50"));
            txnBharath.setTransactionType(TransactionType.DEPOSIT);
            txnBharath.setStatus(TransactionStatus.SUCCESS);
            txnBharath.setRemarks("Initial Account Opening Deposit");
            transactionRepository.save(txnBharath);

            logger.info("Created Default Customer 2: bharath@bank.com / Bharath@123 (Acc: 100020003002)");
        }

        // 5. Initial System Audit Log
        if (auditLogRepository.count() == 0) {
            AuditLog auditLog = new AuditLog("SYSTEM", "INITIALIZATION",
                    "Apex Online Banking System database initialized with default roles and accounts.", "127.0.0.1");
            auditLogRepository.save(auditLog);
        }

        logger.info("Seed Data Initialization Completed Successfully!");
    }
}
