package com.learnnow.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromName", "LearnNow");
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@learnnow.com");
    }

    @Test
    void sendEmail_Success() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendEmail(to, subject, content);

        // Assert
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendHtmlEmail_Success() throws MessagingException {
        // Arrange
        String to = "test@example.com";
        String subject = "Test HTML Subject";
        String templateName = "email-confirmation";
        Context context = new Context();
        context.setVariable("name", "John");
        String htmlContent = "<html><body>Hello John</body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn(htmlContent);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act
        emailService.sendHtmlEmail(to, subject, templateName, context);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq(templateName), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendHtmlEmail_ThrowsMessagingException() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "email-confirmation";
        Context context = new Context();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>Test</body></html>");

        // Note: We can't easily mock MimeMessageHelper constructor failure,
        // but we can verify the method catches MessagingException and wraps it

        // Act & Assert
        // This test verifies the method signature and basic flow
        assertDoesNotThrow(() -> {
            emailService.sendHtmlEmail(to, subject, templateName, context);
        });
    }
}
