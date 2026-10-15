package com.example.talentbridge.service.impl;

import com.example.talentbridge.shared.presentation.FileUploads;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.example.talentbridge.dto.request.resume.ResumeRequestDto;
import com.example.talentbridge.dto.request.resume.UpdateResumeStatusRequestDto;
import com.example.talentbridge.dto.response.resume.CreateResumeResponseDto;
import com.example.talentbridge.dto.response.resume.DefaultResumeResponseDto;
import com.example.talentbridge.dto.response.resume.GetResumeFileResponseDto;
import com.example.talentbridge.dto.response.resume.ResumeForDisplayResponseDto;
import com.example.talentbridge.shared.domain.exception.AlreadyExistsException;
import com.example.talentbridge.model.Job;
import com.example.talentbridge.model.Resume;
import com.example.talentbridge.skill.infrastructure.persistence.SkillJpaEntity;
import com.example.talentbridge.model.User;
import com.example.talentbridge.repository.JobRepository;
import com.example.talentbridge.repository.ResumeRepository;
import com.example.talentbridge.repository.UserRepository;
import com.example.talentbridge.shared.application.FileStorage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ResumeServiceImpl implements com.example.talentbridge.service.ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    private final FileStorage s3Service;

    @Override
    public CreateResumeResponseDto saveResume(
            ResumeRequestDto resumeRequestDto,
            MultipartFile pdfFile) {
        Resume resume = new Resume(
                resumeRequestDto.getEmail(),
                resumeRequestDto.getStatus(),
                1L
        );

        User user = userRepository
                .findById(resumeRequestDto.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        if (!user.getEmail().equals(resume.getEmail()))
            throw new EntityNotFoundException("Truy cập bị từ chối");

        Job job = jobRepository
                .findById(resumeRequestDto.getJob().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy công việc"));

        if (resumeRepository.existsByUserIdAndJobId(user.getId(), job.getId()))
            throw new AlreadyExistsException("Người dùng đã nộp resume cho công việc này");


        resume.setJob(job);
        resume.setUser(user);


        Resume savedResume = resumeRepository.saveAndFlush(resume);

        if (pdfFile != null && !pdfFile.isEmpty()) {
            String safeEmail = savedResume.getEmail().replaceAll("[^a-zA-Z0-9]", "_");
            String folderName = "resume/" + safeEmail;
            String generatedFileName = "resume-" + savedResume.getId() + "-" + resume.getVersion() + ".pdf";


            String key = s3Service.uploadFile(FileUploads.from(pdfFile), folderName, generatedFileName, false);

            savedResume.setFileKey(key);
        } else throw new EntityNotFoundException("Không tìm thấy tệp pdf");

        return new CreateResumeResponseDto(
                savedResume.getId(),
                savedResume.getCreatedAt(),
                savedResume.getCreatedBy()
        );
    }

    @Override
    public Page<ResumeForDisplayResponseDto> findAllResumes(
            Specification<Resume> spec,
            Pageable pageable
    ) {
        return resumeRepository
                .findAll(spec, pageable)
                .map(this::mapToResumeForDisplayResponseDto);
    }

    @Override
    public Page<ResumeForDisplayResponseDto> findAllResumesForRecruiterCompany(
            Specification<Resume> spec,
            Pageable pageable
    ) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        if (user.getCompany() == null)
            throw new EntityNotFoundException("Không tìm thấy công ty người dùng");

        Page<ResumeForDisplayResponseDto> data = resumeRepository
                .findByUserCompanyId(user.getCompany().getId(), spec, pageable)
                .map(this::mapToResumeForDisplayResponseDto);

        List<ResumeForDisplayResponseDto> filteredList =
                data.getContent().stream()
                        .filter(x ->
                                x.getCompany().getId().equals(user.getCompany().getId())
                        )
                        .toList();

        return new PageImpl<>(
                filteredList,
                data.getPageable(),
                filteredList.size()
        );
    }

    @Override
    public Page<ResumeForDisplayResponseDto> findSelfResumes(
            Specification<Resume> spec,
            Pageable pageable) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return resumeRepository
                .findByUserEmail(email, spec, pageable)
                .map(this::mapToResumeForDisplayResponseDto);
    }


//    @Override
//    public ResumeForDisplayResponseDto findResumeById(Long id) {
//        Resume resume = resumeRepository
//                .findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy resume"));
//        return mapToResumeForDisplayResponseDto(resume);
//    }


    @Override
    public DefaultResumeResponseDto removeSelfResumeByJobId(Long jobId) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Resume resume = resumeRepository
                .findByUserEmailAndJobId(email, jobId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy resume"));

        DefaultResumeResponseDto res = mapToResponseDto(resume);

        resume.setUser(null);
        resume.setJob(null);

        s3Service.deleteFileByKey(resume.getFileKey());

        Resume savedResume = resumeRepository.saveAndFlush(resume);
        resumeRepository.delete(savedResume);

        return res;
    }

    @Override
    public DefaultResumeResponseDto updateSelfResumeFile(Long id, MultipartFile pdfFile) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Resume resume = resumeRepository
                .findByUserEmailAndId(email, id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy resume"));

        if (pdfFile != null && !pdfFile.isEmpty()) {
            resume.setVersion(resume.getVersion() + 1);
            String newKey = generateKey(resume.getEmail(), resume.getId(), resume.getVersion());

            s3Service.deleteFileByKey(resume.getFileKey());
            s3Service.uploadFile(FileUploads.from(pdfFile), newKey, false);
            resume.setFileKey(newKey);
        } else throw new EntityNotFoundException("Không tìm thấy tệp pdf");

        resumeRepository.save(resume);

        return mapToResponseDto(resume);
    }

    @Override
    public GetResumeFileResponseDto getResumeFileUrl(Long id) {
        Resume resume = resumeRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy resume"));

        assertCanReadResumeFile(resume);

        return new GetResumeFileResponseDto(
                s3Service.generatePresignedUrl(resume.getFileKey(), Duration.ofMinutes(15))
        );
    }

    @Override
    public DefaultResumeResponseDto updateResumeStatus(
            UpdateResumeStatusRequestDto updateResumeStatusRequestDto) {
        Resume resume = resumeRepository
                .findById(updateResumeStatusRequestDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy resume"));

        resume.setStatus(updateResumeStatusRequestDto.getStatus());
        resumeRepository.save(resume);
        return mapToResponseDto(resume);
    }

    @Override
    public DefaultResumeResponseDto updateResumeStatusForRecruiterCompany(
            UpdateResumeStatusRequestDto updateResumeStatusRequestDto) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        if (user.getCompany() == null)
            throw new EntityNotFoundException("Không tìm thấy công ty người dùng");

        Resume resume = resumeRepository
                .findById(updateResumeStatusRequestDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy resume"));

        if (!resume.getJob().getCompany().getId().equals(user.getCompany().getId()))
            throw new AccessDeniedException("Không có quyền truy cập");

        resume.setStatus(updateResumeStatusRequestDto.getStatus());
        resumeRepository.save(resume);
        return mapToResponseDto(resume);
    }

    /**
     * A CV may be read by its owner, by recruiters of the company that owns the job it was submitted to,
     * and by administrators. Anyone else gets 403 (the id alone must not be enough to fetch someone's CV).
     */
    private void assertCanReadResumeFile(Resume resume) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        boolean admin = user.getRole() != null && "ADMIN".equals(user.getRole().getName());
        boolean owner = resume.getUser() != null && resume.getUser().getId().equals(user.getId());
        boolean sameCompany = user.getCompany() != null
                && resume.getJob() != null
                && resume.getJob().getCompany() != null
                && resume.getJob().getCompany().getId().equals(user.getCompany().getId());

        if (!admin && !owner && !sameCompany)
            throw new AccessDeniedException("Không có quyền truy cập");
    }

    private String generateKey(String email, Long id, Long version) {
        String safeEmail = email.replaceAll("[^a-zA-Z0-9]", "_");
        String folderName = "resume/" + safeEmail;
        String generatedFileName = "resume-" + id + "-" + version + ".pdf";

        return folderName + "/" + generatedFileName;
    }

    private DefaultResumeResponseDto mapToResponseDto(Resume resume) {
        return new DefaultResumeResponseDto(
                resume.getId(),
                resume.getUser().getEmail(),
                resume.getJob().getName(),
                resume.getJob().getCompany().getName(),
                resume.getCreatedAt().toString(),
                resume.getUpdatedAt().toString()
        );
    }

    private ResumeForDisplayResponseDto mapToResumeForDisplayResponseDto(Resume resume) {
        ResumeForDisplayResponseDto resumeForDisplayResponseDto = new ResumeForDisplayResponseDto();

        resumeForDisplayResponseDto.setId(resume.getId());

        resumeForDisplayResponseDto.setPdfUrl(s3Service.generatePresignedUrl(resume.getFileKey(), Duration.ofMinutes(15)));
        resumeForDisplayResponseDto.setStatus(resume.getStatus().toString());

        ResumeForDisplayResponseDto.User user = new ResumeForDisplayResponseDto.User(
                resume.getUser().getId(),
                resume.getUser().getEmail()
        );
        resumeForDisplayResponseDto.setUser(user);

        ResumeForDisplayResponseDto.Job job = new ResumeForDisplayResponseDto.Job(
                resume.getJob().getId(),
                resume.getJob().getName(),
                resume.getJob().getLocation(),
                resume.getJob().getSkills().stream().map(SkillJpaEntity::getName).toList(),
                resume.getJob().getLevel(),
                resume.getJob().getDescription()
        );
        resumeForDisplayResponseDto.setJob(job);

        String logoUrl = null;
        if (resume.getJob().getCompany().getCompanyLogo() != null)
            logoUrl = resume.getJob().getCompany().getCompanyLogo().getLogoUrl();

        ResumeForDisplayResponseDto.Company company = new ResumeForDisplayResponseDto.Company(
                resume.getJob().getCompany().getId(),
                resume.getJob().getCompany().getName(),
                logoUrl
        );
        resumeForDisplayResponseDto.setCompany(company);

        resumeForDisplayResponseDto.setCreatedAt(resume.getCreatedAt().toString());
        resumeForDisplayResponseDto.setUpdatedAt(resume.getUpdatedAt().toString());


        return resumeForDisplayResponseDto;
    }


}
