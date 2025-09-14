# Nhom7_BusCitytracuutuyenxebuyt

Nhóm 7 — Ứng dụng tra cứu tuyến xe buýt (BusCity)

Mô tả ngắn: đây là mã nguồn do nhóm thực hiện cho đề tài "Xây dựng ứng dụng tra cứu tuyến xe buýt thông minh dành cho người dùng".

Yêu cầu trước khi chạy

- Java 11 hoặc mới hơn (kiểm tra bằng `java -version`).
- Maven (hoặc sử dụng wrapper `mvnw` / `mvnw.cmd` có sẵn).
- Docker & Docker Compose (nếu muốn chạy cùng MySQL trong container).

Chạy ứng dụng

1. Chạy bằng Maven (phát triển / nhanh):

   - Mở terminal trong thư mục dự án và chạy:

     mvnw spring-boot:run

   (Trên Windows: sử dụng `mvn.cmd spring-boot:run` nếu không dùng WSL.)

2. Chạy bằng file jar (sản phẩm build):

   - Build trước:

     mvnw package

   - Sau khi build xong, chạy jar trong thư mục `target`:

     java -jar target/javavbusproject-0.0.1-SNAPSHOT.jar

Docker (tùy chọn)

- Nếu muốn khởi tạo database bằng Docker, dùng Docker Compose (thư mục chứa `docker-compose.yml`):

  docker-compose up -d

- Tập tin SQL khởi tạo nằm trong `mysql-init/` (ví dụ: `bushcm_2025-09-15_054050.sql`).

Môi trường phát triển (VS Code)

- Mở project trong VS Code, cài các extension Java/Spring Boot nếu cần. Dự án đã có Maven wrapper nên bạn không bắt buộc phải cài Maven toàn cục.

Ghi chú

- Tệp cấu hình ứng dụng: `src/main/resources/application.properties`.
- Tài nguyên tĩnh và template nằm trong `src/main/resources/static` và `src/main/resources/templates`.
- Nếu gặp lỗi khi khởi động liên quan đến database, kiểm tra cấu hình kết nối trong `application.properties` hoặc chạy MySQL bằng Docker trước khi khởi chạy ứng dụng.

Liên hệ

- Nếu cần thêm trợ giúp, mở issue hoặc liên hệ với thành viên nhóm.
