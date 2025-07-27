package com.gialong.classroom.service.classroom;


import com.gialong.classroom.dto.submission.SubmissionRequest;
import com.gialong.classroom.dto.submission.SubmissionResponse;
import com.gialong.classroom.exception.AppException;
import com.gialong.classroom.exception.ErrorCode;
import com.gialong.classroom.model.Assignment;
import com.gialong.classroom.model.Submission;
import com.gialong.classroom.model.User;
import com.gialong.classroom.repository.AssignmentRepository;
import com.gialong.classroom.repository.SubmissionRepository;
import com.gialong.classroom.service.user.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final AuthService authService;

    public SubmissionResponse submitAssignment(SubmissionRequest request) {
        User student = authService.getCurrentUser();
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .fileUrl(request.getFileUrl())
                .submittedAt(LocalDateTime.now())
                .build();

        submissionRepository.save(submission);

        return SubmissionResponse.builder()
                .id(submission.getId())
                .studentName(String.format("%s %s", student.getFirstName(), student.getLastName()))
                .fileUrl(submission.getFileUrl())
                .submittedAt(submission.getSubmittedAt())
                .score(null)
                .build();
    }

//    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
//        return submissionRepository.findByAssignmentId(assignmentId)
//                .stream()
//                .map(s -> SubmissionResponse.builder()
//                        .id(s.getId())
//                        .studentName(s.getStudent().getFullName())
//                        .fileUrl(s.getFileUrl())
//                        .submittedAt(s.getSubmittedAt())
//                        .score(s.getScore())
//                        .build())
//                .toList();
//    }

    public void gradeSubmission(Long submissionId, int score) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));
        submission.setScore(score);
        submissionRepository.save(submission);
    }
}