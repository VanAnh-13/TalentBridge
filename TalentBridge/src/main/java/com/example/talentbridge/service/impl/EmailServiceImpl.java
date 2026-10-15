package com.example.talentbridge.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.dto.email.JobMailDto;
import com.example.talentbridge.job.infrastructure.persistence.JobJpaEntity;
import com.example.talentbridge.subscriber.infrastructure.persistence.SubscriberJpaEntity;
import com.example.talentbridge.job.infrastructure.persistence.JobJpaRepository;
import com.example.talentbridge.subscriber.infrastructure.persistence.SubscriberJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "spring.mail.host",
        matchIfMissing = false
)
public class EmailServiceImpl implements com.example.talentbridge.service.EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private final JobJpaRepository jobRepository;
    private final SubscriberJpaRepository subscriberRepository;

    @Value("${mail.from}")
    private String sender;

    @Override
    public void sendJobNotificationForSubscriber(SubscriberJpaEntity subscriber) throws MessagingException {
        List<String> skillNames = subscriber.getSkills().stream()
                .map(skill -> skill.getName())
                .toList();

        List<JobJpaEntity> jobs = jobRepository.findDistinctTop3BySkills_NameInOrderByCreatedAtDesc(skillNames);

        List<JobMailDto> jobMailDtos = jobs.stream()
                .map(this::mapToEmailJobInform)
                .toList();

        Context context = new Context();
        context.setVariable("jobs", jobMailDtos);
        String html = templateEngine.process("job-notification-email.html", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(sender);
        helper.setTo(subscriber.getEmail());
        helper.setSubject("🔥 Cơ hội việc làm mới dành cho bạn!");
        helper.setText(html, true);

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendJobNotificationManually(String email) throws MessagingException {
        SubscriberJpaEntity subscriber = subscriberRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        sendJobNotificationForSubscriber(subscriber);
    }

    private JobMailDto mapToEmailJobInform(JobJpaEntity job) {
        String applyUrl = "http://localhost:3000/jobs/" + job.getId();

        JobMailDto jobMailDto = new JobMailDto(job.getId(), job.getName(), job.getSalary(), applyUrl);

        if (job.getCompany() != null) {
            JobMailDto.CompanyDto companyDto =
                    new JobMailDto.CompanyDto(
                            job.getCompany().getId(),
                            job.getCompany().getName(),
                            job.getCompany().getAddress()
                    );
            jobMailDto.setCompany(companyDto);
        }

        if (job.getSkills() != null) {
            List<JobMailDto.SkillDto> skillDtos = job
                    .getSkills()
                    .stream()
                    .map(x -> new JobMailDto.SkillDto(x.getId(), x.getName()))
                    .toList();
            jobMailDto.setSkills(skillDtos);
        }

        return jobMailDto;
    }


}
