package com.splitmate.api.config;

import com.splitmate.api.entity.*;
import com.splitmate.api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
@Profile("dev")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seedTestData(
            UserRepository userRepository,
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            ExpenseRepository expenseRepository,
            ExpenseSplitRepository expenseSplitRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            String testEmail = "murugan@example.com";

            User murugan = userRepository.findByEmail(testEmail).orElseGet(() -> {
                User u = new User();
                u.setDisplayName("Murugan");
                u.setEmail(testEmail);
                u.setPhone("+919876543210");
                u.setPasswordHash(passwordEncoder.encode("Password@123"));
                u.setActive(true);
                return userRepository.save(u);
            });

            if (groupRepository.countUserGroups(murugan.getId()) > 0) {
                log.info("Demo data already seeded for user: {}", testEmail);
                return;
            }

            // Seed peer users
            User rahul = createOrFindUser(userRepository, passwordEncoder, "Rahul", "rahul@example.com", "+919876543211");
            User anil = createOrFindUser(userRepository, passwordEncoder, "Anil", "anil@example.com", "+919876543212");
            User priya = createOrFindUser(userRepository, passwordEncoder, "Priya", "priya@example.com", "+919876543213");

            Instant now = Instant.now();

            // 1. Seed Groups
            Group officeTeam = createGroup(groupRepository, "Office Team", "Work colleagues and lunch splits", murugan, now.minus(2, ChronoUnit.HOURS));
            Group friendsTrip = createGroup(groupRepository, "Friends Trip", "Goa vacation expenses", rahul, now.minus(5, ChronoUnit.HOURS));
            Group birthdayParty = createGroup(groupRepository, "Birthday Party", "Surprise celebrations", priya, now.minus(1, ChronoUnit.DAYS));
            Group family = createGroup(groupRepository, "Family", "Household shared utilities", murugan, now.minus(2, ChronoUnit.DAYS));

            // 2. Seed Group Members
            addMember(groupMemberRepository, officeTeam, murugan, "ADMIN");
            addMember(groupMemberRepository, officeTeam, rahul, "MEMBER");
            addMember(groupMemberRepository, officeTeam, anil, "MEMBER");
            addMember(groupMemberRepository, officeTeam, priya, "MEMBER");

            addMember(groupMemberRepository, friendsTrip, murugan, "MEMBER");
            addMember(groupMemberRepository, friendsTrip, rahul, "ADMIN");
            addMember(groupMemberRepository, friendsTrip, anil, "MEMBER");

            addMember(groupMemberRepository, birthdayParty, murugan, "MEMBER");
            addMember(groupMemberRepository, birthdayParty, priya, "ADMIN");
            addMember(groupMemberRepository, birthdayParty, rahul, "MEMBER");

            addMember(groupMemberRepository, family, murugan, "ADMIN");

            // 3. Seed Expenses & Splits to match Dashboard Mockup
            // Expense 1: Dinner (Office Team, Rs. 2,500, paid by Rahul)
            // Murugan owes Rahul Rs. 1,250
            Expense dinner = createExpense(expenseRepository, officeTeam, rahul, "Dinner", new BigDecimal("2500.00"), now.minus(2, ChronoUnit.HOURS));
            createSplit(expenseSplitRepository, dinner, murugan, new BigDecimal("1250.00"), false);
            createSplit(expenseSplitRepository, dinner, rahul, new BigDecimal("1250.00"), true);

            // Expense 2: Fuel (Friends Trip, Rs. 3,200, paid by Murugan)
            // Rahul owes Rs. 1,200, Anil owes Rs. 1,200 (Total Owed To Murugan = Rs. 2,400)
            Expense fuel = createExpense(expenseRepository, friendsTrip, murugan, "Fuel", new BigDecimal("3200.00"), now.minus(5, ChronoUnit.HOURS));
            createSplit(expenseSplitRepository, fuel, murugan, new BigDecimal("800.00"), true);
            createSplit(expenseSplitRepository, fuel, rahul, new BigDecimal("1200.00"), false);
            createSplit(expenseSplitRepository, fuel, anil, new BigDecimal("1200.00"), false);

            // Expense 3: Cake (Birthday Party, Rs. 1,800, paid by Priya)
            Expense cake = createExpense(expenseRepository, birthdayParty, priya, "Cake", new BigDecimal("1800.00"), now.minus(1, ChronoUnit.DAYS));
            createSplit(expenseSplitRepository, cake, priya, new BigDecimal("900.00"), true);
            createSplit(expenseSplitRepository, cake, rahul, new BigDecimal("900.00"), false);

            // 4. Seed Notifications (3 unread notifications)
            createNotification(notificationRepository, murugan, "New Expense", "Rahul added 'Dinner' in Office Team.", false);
            createNotification(notificationRepository, murugan, "Group Invite", "You were added to 'Birthday Party'.", false);
            createNotification(notificationRepository, murugan, "Payment Reminder", "Friendly reminder for dinner split.", false);

            log.info("Demo dashboard data seeded successfully for {}", testEmail);
        };
    }

    private User createOrFindUser(UserRepository repo, PasswordEncoder encoder, String name, String email, String phone) {
        return repo.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setDisplayName(name);
            u.setEmail(email);
            u.setPhone(phone);
            u.setPasswordHash(encoder.encode("Password@123"));
            u.setActive(true);
            return repo.save(u);
        });
    }

    private Group createGroup(GroupRepository repo, String name, String description, User creator, Instant lastActivity) {
        Group g = new Group();
        g.setName(name);
        g.setDescription(description);
        g.setCreatedBy(creator);
        g.setLastActivityAt(lastActivity);
        return repo.save(g);
    }

    private void addMember(GroupMemberRepository repo, Group group, User user, String role) {
        GroupMember gm = new GroupMember();
        gm.setGroup(group);
        gm.setUser(user);
        gm.setRole(role);
        gm.setActive(true);
        repo.save(gm);
    }

    private Expense createExpense(ExpenseRepository repo, Group group, User payer, String desc, BigDecimal amount, Instant createdAt) {
        Expense e = new Expense();
        e.setGroup(group);
        e.setPaidBy(payer);
        e.setDescription(desc);
        e.setAmount(amount);
        e.setCurrency("INR");
        return repo.save(e);
    }

    private void createSplit(ExpenseSplitRepository repo, Expense expense, User user, BigDecimal owedAmount, boolean settled) {
        ExpenseSplit es = new ExpenseSplit();
        es.setExpense(expense);
        es.setUser(user);
        es.setOwedAmount(owedAmount);
        es.setSettled(settled);
        repo.save(es);
    }

    private void createNotification(NotificationRepository repo, User user, String title, String message, boolean read) {
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(read);
        repo.save(n);
    }
}
