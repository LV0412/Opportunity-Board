package com.opportunityboard.infrastructure.template;

import com.opportunityboard.common.enums.ApplicationStatus;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.infrastructure.mail.MailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailTemplateService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String frontendUrl;

    public EmailTemplateService(@Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    public MailMessage applicationStatusChanged(
            String recipientEmail,
            String recipientName,
            String opportunityTitle,
            ApplicationStatus status
    ) {
        String subject = "Opportunity Board - Cap nhat trang thai ung tuyen";
        String text = """
                Xin chao %s,

                Ho so ung tuyen cua ban cho co hoi "%s" vua duoc cap nhat sang trang thai %s.

                Ban co the xem chi tiet tai: %s/student/applications
                """
                .formatted(recipientName, opportunityTitle, status.name(), frontendUrl);
        String html = """
                <p>Xin chao %s,</p>
                <p>Ho so ung tuyen cua ban cho co hoi <strong>%s</strong> vua duoc cap nhat sang trang thai <strong>%s</strong>.</p>
                <p><a href="%s/student/applications">Mo trang theo doi ung tuyen</a></p>
                """
                .formatted(escapeHtml(recipientName), escapeHtml(opportunityTitle), status.name(), frontendUrl);
        return new MailMessage(recipientEmail, subject, html, text);
    }

    public MailMessage opportunityReviewed(
            String recipientEmail,
            String recipientName,
            String opportunityTitle,
            boolean approved,
            String note
    ) {
        String subject = approved
                ? "Opportunity Board - Co hoi da duoc phe duyet"
                : "Opportunity Board - Co hoi can dieu chinh";
        String statusLine = approved ? "da duoc phe duyet" : "da bi tu choi";
        String noteText = note == null || note.isBlank() ? "" : "\nGhi chu: " + note.trim();
        String text = """
                Xin chao %s,

                Co hoi "%s" cua ban %s.%s

                Quan ly bai dang tai: %s/organization/opportunities
                """
                .formatted(recipientName, opportunityTitle, statusLine, noteText, frontendUrl);
        String html = """
                <p>Xin chao %s,</p>
                <p>Co hoi <strong>%s</strong> cua ban <strong>%s</strong>.</p>
                %s
                <p><a href="%s/organization/opportunities">Mo trang quan ly co hoi</a></p>
                """
                .formatted(
                        escapeHtml(recipientName),
                        escapeHtml(opportunityTitle),
                        escapeHtml(statusLine),
                        note == null || note.isBlank() ? "" : "<p>Ghi chu: " + escapeHtml(note.trim()) + "</p>",
                        frontendUrl
                );
        return new MailMessage(recipientEmail, subject, html, text);
    }

    public MailMessage deadlineReminder(
            String recipientEmail,
            String recipientName,
            Opportunity opportunity,
            int daysLeft
    ) {
        String subject = "Opportunity Board - Nhac han dang ky con " + daysLeft + " ngay";
        String deadline = opportunity.getDeadlineAt() == null
                ? "Khong co han"
                : DATE_TIME_FORMATTER.format(opportunity.getDeadlineAt().atZone(ZoneId.of("Asia/Ho_Chi_Minh")));
        String text = """
                Xin chao %s,

                Co hoi "%s" se het han sau %d ngay.
                Han nop: %s

                Xem chi tiet: %s/opportunities/%s
                """
                .formatted(recipientName, opportunity.getTitle(), daysLeft, deadline, frontendUrl, opportunity.getId());
        String html = """
                <p>Xin chao %s,</p>
                <p>Co hoi <strong>%s</strong> se het han sau <strong>%d ngay</strong>.</p>
                <p>Han nop: %s</p>
                <p><a href="%s/opportunities/%s">Xem chi tiet co hoi</a></p>
                """
                .formatted(
                        escapeHtml(recipientName),
                        escapeHtml(opportunity.getTitle()),
                        daysLeft,
                        escapeHtml(deadline),
                        frontendUrl,
                        opportunity.getId()
                );
        return new MailMessage(recipientEmail, subject, html, text);
    }

    public MailMessage weeklyDigest(
            String recipientEmail,
            String recipientName,
            List<Opportunity> opportunities
    ) {
        String subject = "Opportunity Board - Weekly digest co hoi moi";
        String textItems = opportunities.stream()
                .map(item -> "- " + item.getTitle() + " (" + item.getCategory().getName() + ")")
                .reduce("", (left, right) -> left + right + "\n");
        String htmlItems = opportunities.stream()
                .map(item -> "<li><a href=\"" + frontendUrl + "/opportunities/" + item.getId() + "\">"
                        + escapeHtml(item.getTitle()) + "</a> - " + escapeHtml(item.getCategory().getName()) + "</li>")
                .reduce("", String::concat);

        String text = """
                Xin chao %s,

                Day la danh sach co hoi moi trong tuan nay:
                %s

                Kham pha them tai: %s/explore
                """
                .formatted(recipientName, textItems, frontendUrl);
        String html = """
                <p>Xin chao %s,</p>
                <p>Day la danh sach co hoi moi trong tuan nay:</p>
                <ul>%s</ul>
                <p><a href="%s/explore">Mo trang kham pha</a></p>
                """
                .formatted(escapeHtml(recipientName), htmlItems, frontendUrl);
        return new MailMessage(recipientEmail, subject, html, text);
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
