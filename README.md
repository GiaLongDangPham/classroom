# 📚 Classroom Management System

> Hệ thống quản lý lớp học hiện đại với tính năng chat thời gian thực, chia sẻ bài viết và quản lý bài tập.

## 🌟 Tổng quan

Classroom Management System là một ứng dụng web full-stack được xây dựng để hỗ trợ việc quản lý lớp học trực tuyến. Hệ thống cho phép giáo viên tạo và quản lý lớp học, học sinh tham gia lớp học, chia sẻ bài viết, chat thời gian thực và quản lý bài tập.

### 🎯 Tính năng chính

- **🏫 Quản lý lớp học**: Tạo lớp, tham gia lớp bằng mã, rời lớp, xóa lớp
- **💬 Chat thời gian thực**: WebSocket chat trong từng lớp học
- **📝 Bài viết**: Tương tác (like, comment)
- **📋 Bài tập**: Tạo và nộp bài tập, chấm điểm
- **👥 Quản lý thành viên**: Xem danh sách thành viên trong lớp
- **🔍 Khám phá**: Tìm kiếm và tham gia các lớp học khác

## 📷 Screenshots

### 🏫 Giao diện quản lý lớp học
![Classroom Management Interface](docs/classroom.png)

### 💬 Tính năng chat thời gian thực  
![Real-time Chat Feature](docs/chat.png)

### 👤 Trang hồ sơ người dùng
![User Profile Page](docs/profile.png)

## 🏗️ Kiến trúc hệ thống

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.5.3
- **Database**: MySQL với Spring Data JPA
- **Security**: Spring Security + JWT
- **WebSocket**: STOMP Protocol
- **API**: RESTful API

### Frontend (Angular)
- **Framework**: Angular 17
- **UI**: TailwindCSS
- **WebSocket**: SockJS + STOMP

## 📦 Cấu trúc dự án

```
classroom/
├── classroom/                    # Backend (Spring Boot)
│   ├── src/main/java/com/gialong/classroom/
│   │   ├── config/              # Cấu hình (Security, WebSocket, CORS)
│   │   ├── controller/          # REST Controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── exception/           # Exception Handling
│   │   ├── model/               # JPA Entities
│   │   ├── repository/          # JPA Repositories
│   │   └── service/             # Business Logic
│   └── src/main/resources/
│       ├── application.yml      # Cấu hình ứng dụng
│       └── static/              # Static files
│
└── classroom-fe/                # Frontend (Angular)
    ├── src/app/
    │   ├── guards/              # Route Guards
    │   ├── interceptors/        # HTTP Interceptors
    │   ├── models/              # TypeScript Models
    │   ├── pages/               # Page Components
    │   ├── services/            # Angular Services
    │   └── layout/              # Layout Components
    └── src/assets/              # Static Assets
```



---
⭐ **Nếu dự án này hữu ích, hãy cho một star nhé!** ⭐

