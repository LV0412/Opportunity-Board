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
        String subject = "Opportunity Board - Cập nhật trạng thái ứng tuyển";
        String statusLabel = applicationStatusLabel(status);
        String text = """
                Xin chào %s,

                Hồ sơ ứng tuyển của bạn cho cơ hội "%s" vừa được cập nhật sang trạng thái %s.

                Bạn có thể xem chi tiết tại: %s/student/applications
                """
                .formatted(recipientName, opportunityTitle, statusLabel, frontendUrl);
        String html = """
                <p>Xin chào %s,</p>
                <p>Hồ sơ ứng tuyển của bạn cho cơ hội <strong>%s</strong> vừa được cập nhật sang trạng thái <strong>%s</strong>.</p>
                <p><a href="%s/student/applications">Mở trang theo dõi ứng tuyển</a></p>
                """
                .formatted(escapeHtml(recipientName), escapeHtml(opportunityTitle), escapeHtml(statusLabel), frontendUrl);
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
                ? "Opportunity Board - Cơ hội đã được phê duyệt"
                : "Opportunity Board - Cơ hội cần điều chỉnh";
        String statusLine = approved ? "đã được phê duyệt" : "đã bị từ chối";
        String noteText = note == null || note.isBlank() ? "" : "\nGhi chú: " + note.trim();
        String text = """
                Xin chào %s,

                Cơ hội "%s" của bạn %s.%s

                Quản lý bài đăng tại: %s/organization/opportunities
                """
                .formatted(recipientName, opportunityTitle, statusLine, noteText, frontendUrl);
        String html = """
                <p>Xin chào %s,</p>
                <p>Cơ hội <strong>%s</strong> của bạn <strong>%s</strong>.</p>
                %s
                <p><a href="%s/organization/opportunities">Mở trang quản lý cơ hội</a></p>
                """
                .formatted(
                        escapeHtml(recipientName),
                        escapeHtml(opportunityTitle),
                        escapeHtml(statusLine),
                        note == null || note.isBlank() ? "" : "<p>Ghi chú: " + escapeHtml(note.trim()) + "</p>",
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
        String subject = "Opportunity Board - Nhắc hạn đăng ký còn " + daysLeft + " ngày";
        String deadline = opportunity.getDeadlineAt() == null
                ? "Không có hạn"
                : DATE_TIME_FORMATTER.format(opportunity.getDeadlineAt().atZone(ZoneId.of("Asia/Ho_Chi_Minh")));
        String text = """
                Xin chào %s,

                Cơ hội "%s" sẽ hết hạn sau %d ngày.
                Hạn nộp: %s

                Xem chi tiết: %s/opportunities/%s
                """
                .formatted(recipientName, opportunity.getTitle(), daysLeft, deadline, frontendUrl, opportunity.getId());
        String html = """
                <p>Xin chào %s,</p>
                <p>Cơ hội <strong>%s</strong> sẽ hết hạn sau <strong>%d ngày</strong>.</p>
                <p>Hạn nộp: %s</p>
                <p><a href="%s/opportunities/%s">Xem chi tiết cơ hội</a></p>
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
        String subject = "Opportunity Board - Tổng hợp cơ hội mới trong tuần";
        String textItems = opportunities.stream()
                .map(item -> "- " + item.getTitle() + " (" + item.getCategory().getName() + ")")
                .reduce("", (left, right) -> left + right + "\n");
        String htmlItems = opportunities.stream()
                .map(item -> "<li><a href=\"" + frontendUrl + "/opportunities/" + item.getId() + "\">"
                        + escapeHtml(item.getTitle()) + "</a> - " + escapeHtml(item.getCategory().getName()) + "</li>")
                .reduce("", String::concat);

        String text = """
                Xin chào %s,

                Đây là danh sách cơ hội mới trong tuần này:
                %s

                Khám phá thêm tại: %s/explore
                """
                .formatted(recipientName, textItems, frontendUrl);
        String html = """
                <p>Xin chào %s,</p>
                <p>Đây là danh sách cơ hội mới trong tuần này:</p>
                <ul>%s</ul>
                <p><a href="%s/explore">Mở trang khám phá</a></p>
                """
                .formatted(escapeHtml(recipientName), htmlItems, frontendUrl);
        return new MailMessage(recipientEmail, subject, html, text);
    }

    public MailMessage emailVerification(
            String recipientEmail,
            String recipientName,
            String verificationToken
    ) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
        String subject = "Opportunity Board - Xác thực email đăng ký";
        String text = """
                Xin chào %s,

                Cảm ơn bạn đã đăng ký tài khoản Opportunity Board.
                Vui lòng xác thực email bằng đường dẫn sau:

                %s

                Đường dẫn này sẽ hết hạn sau 24 giờ.
                """
                .formatted(recipientName, verificationUrl);
        String html = """
                <p>Xin chào %s,</p>
                <p>Cảm ơn bạn đã đăng ký tài khoản Opportunity Board.</p>
                <p>Vui lòng xác thực email để kích hoạt tài khoản.</p>
                <p><a href="%s">Xác thực email</a></p>
                <p>Đường dẫn này sẽ hết hạn sau 24 giờ.</p>
                """
                .formatted(escapeHtml(recipientName), verificationUrl);
        return new MailMessage(recipientEmail, subject, html, text);
    }

    private String applicationStatusLabel(ApplicationStatus status) {
        return switch (status) {
            case APPLIED -> "đã ứng tuyển";
            case REVIEWING -> "đang được xem xét";
            case ACCEPTED -> "đã được chấp nhận";
            case REJECTED -> "đã bị từ chối";
        };
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
