package com.gialong.classroom.service;

import com.gialong.classroom.dto.submission.SubmissionRequest;
import com.gialong.classroom.dto.submission.SubmissionResponse;

public interface SubmissionService {
    SubmissionResponse submitAssignment(SubmissionRequest request);

    void gradeSubmission(Long submissionId, int score);
}
