package com.gialong.classroom.service;

import com.gialong.classroom.dto.PageResponse;
import com.gialong.classroom.dto.classroom.ClassroomRequest;
import com.gialong.classroom.dto.classroom.ClassroomResponse;
import com.gialong.classroom.dto.classroom.MemberResponse;
import com.gialong.classroom.model.ClassroomElasticSearch;
import com.gialong.classroom.model.Enrollment;

import java.util.List;
import java.util.UUID;

public interface ClassroomService {
    ClassroomResponse createClass(ClassroomRequest request);

    PageResponse<ClassroomElasticSearch> searchElastic(int page, int size, String keyword);

    ClassroomResponse joinClass(String joinCode);

    List<ClassroomResponse> getMyClasses();

    ClassroomResponse getClassDetail(Long classId);

    void deleteClass(Long classId);

    void leaveClass(Long classId);

    List<ClassroomResponse> getExploreClasses();
}
