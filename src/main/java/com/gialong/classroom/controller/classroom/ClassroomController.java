package com.gialong.classroom.controller.classroom;

import com.gialong.classroom.dto.PageResponse;
import com.gialong.classroom.dto.ResponseData;
import com.gialong.classroom.dto.classroom.ClassroomRequest;
import com.gialong.classroom.dto.classroom.ClassroomResponse;
import com.gialong.classroom.model.ClassroomElasticSearch;
import com.gialong.classroom.service.classroom.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseData<?> createClass(@RequestBody @Valid ClassroomRequest request) {
        ClassroomResponse classroomResponse = classroomService.createClass(request);
        return ResponseData.builder()
                .code(HttpStatus.CREATED.value())
                .message("create class successfully")
                .data(classroomResponse)
                .build();
    }

    @GetMapping("/search-classroom-with-elasticsearch")
    public ResponseData<PageResponse<ClassroomElasticSearch>> searchElastic(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        var result = classroomService.searchElastic(page, size, keyword);
        return ResponseData.<PageResponse<ClassroomElasticSearch>>builder()
                .code(HttpStatus.OK.value())
                .message("Get classrooms with elastic search")
                .data(result)
                .build();
    }

    @PostMapping("/join")
    public ResponseData<?> joinClass(@RequestParam String joinCode) {
        ClassroomResponse classroomResponse = classroomService.joinClass(joinCode);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("join class successfully")
                .data(classroomResponse)
                .build();
    }

    @GetMapping("/my-classes")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseData<?> getMyClasses() {
        List<ClassroomResponse> classroomResponses = classroomService.getMyClasses();
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("get all my classes successfully")
                .data(classroomResponses)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseData<?> getClassDetail(@PathVariable Long id) {
        ClassroomResponse classroomResponse = classroomService.getClassDetail(id);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("get class-detail successfully")
                .data(classroomResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseData<?> deleteClass(@PathVariable Long id) {
        classroomService.deleteClass(id);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("delete successfully")
                .build();
    }

    @DeleteMapping("/leave/{classId}")
    public ResponseData<?> leaveClass(@PathVariable Long classId) {
        classroomService.leaveClass(classId);
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("leave successfully")
                .build();
    }

    @GetMapping("explore")
    public ResponseData<?> getExploreClasses() {
        List<ClassroomResponse> classes = classroomService.getExploreClasses();
        return ResponseData.builder()
                .code(HttpStatus.OK.value())
                .message("get explore classes successfully")
                .data(classes)
                .build();
    }

}
