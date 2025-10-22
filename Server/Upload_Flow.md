```
graph TD
A[Client gửi request với MultipartFile] --> B[FileUploadController]
B --> C[LessonService]
C --> D[Upload lên Cloudinary]
D --> E[Nhận kết quả từ Cloudinary]
E --> F[LessonService cập nhật DB]
F --> G[Trả response về Client]
```