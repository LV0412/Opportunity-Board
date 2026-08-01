package com.opportunityboard.infrastructure.mail;

public record MailMessage(
        String to,
        String subject,
        String htmlBody,
        String textBody
) {
}
