# Hệ thống Đấu giá trực tuyến — Team 15

## Hướng dẫn Build và Run

> Dự án **Client–Server** nên có **2 fat JAR** (Server + Client). Cả hai đều được đóng gói bằng **`maven-shade-plugin`** (uber JAR — gộp toàn bộ thư viện phụ thuộc) và chạy trực tiếp bằng `java -jar`.

### Build

**Yêu cầu:** JDK 25, Maven 3.8+, MySQL (database `auction`).

```bash
cd HeThongDauGia_Team15
mvn clean package
```

Sau khi build thành công, file JAR nằm trong thư mục `target/`:

| File | Loại |
|------|------|
| `target/hethongdaugia-1.0-SNAPSHOT-server.jar` | Fat JAR Server |
| `target/hethongdaugia-1.0-SNAPSHOT-client.jar` | Fat JAR Client (JavaFX) |

### Run

**Thứ tự:** MySQL → Server → Client.

**Terminal 1 — Server:**

```bash
java -jar target/hethongdaugia-1.0-SNAPSHOT-server.jar
```

**Terminal 2 — Client:**

```bash
java -jar target/hethongdaugia-1.0-SNAPSHOT-client.jar
```

Chỉ cần **JDK 25** đã cài — không cần copy thêm thư viện bên ngoài (đã gộp trong JAR).

**Gói ZIP (tùy chọn):** `target/hethongdaugia-1.0-SNAPSHOT-dist.zip` — gồm 2 JAR + script `run-server.bat` / `run-client.bat`.


## 1. Mô tả bài toán và phạm vi hệ thống

Hệ thống **đấu giá trực tuyến** cho phép người dùng đăng ký tài khoản, đăng sản phẩm, tạo phiên đấu giá, đặt giá (thủ công hoặc tự động), nạp tiền ký quỹ và quản trị phiên đấu giá qua giao diện desktop.

**Phạm vi:**

| Thành phần | Mô tả |
|------------|--------|
| **Server** | Lắng nghe kết nối Socket (cổng `5000`), xử lý yêu cầu theo giao thức `MessageProtocol`, đồng bộ dữ liệu với MySQL, điều phối timer đóng phiên đấu giá. |
| **Client** | Ứng dụng JavaFX (FXML): đăng nhập/đăng ký, xem danh sách phiên, đấu giá, quản lý sản phẩm, nạp tiền, admin duyệt phiên & ký quỹ. |
| **Cơ sở dữ liệu** | MySQL — lưu người dùng, sản phẩm, phiên đấu giá, lịch sử giá, ví/ký quỹ, thông báo. |
| **Vai trò** | `ADMIN` (quản trị), `MEMBER` (mua/bán), `GUEST` (chỉ xem, không cần đăng nhập). |

Kiến trúc: **Client–Server** qua **TCP Socket** + **Java Object Serialization** (`ObjectInputStream` / `ObjectOutputStream`).

---

## 2. Công nghệ, môi trường chạy và yêu cầu cài đặt

### Công nghệ sử dụng

| Hạng mục | Công nghệ / thư viện |
|----------|----------------------|
| Ngôn ngữ | Java 25 |
| Giao diện | JavaFX 25 (FXML, CSS) |
| Build | Apache Maven |
| Cơ sở dữ liệu | MySQL 8.x (`mysql-connector-j`) |
| Mạng | `java.net` (ServerSocket / Socket) |
| JSON / DTO | Gson |
| Bảo mật mật khẩu | jBCrypt |
| Ảnh sản phẩm | Cloudinary |
| Ghi log | SLF4J + Logback |
| Kiểm thử | JUnit 5, Mockito, TestFX |

### Yêu cầu môi trường

- **JDK 25** (khớp `pom.xml`)
- **Maven 3.8+**
- **MySQL** chạy tại `localhost:3306`, database tên **`auction`**
- **IntelliJ IDEA** (khuyến nghị) hoặc IDE hỗ trợ JavaFX + Maven

### Cấu hình database

1. Tạo database MySQL: `auction`.
2. Import schema/dữ liệu mẫu (theo script SQL của nhóm — nếu có trong báo cáo hoặc file riêng).
3. Chỉnh thông tin kết nối trong `src/main/java/com/example/auction/database/dao/BaseDAO.java` (URL, username, password) cho khớp máy local.

```text
jdbc:mysql://localhost:3306/auction
```

### Cấu hình Cloudinary (tùy chọn — upload ảnh sản phẩm)

Cấu hình API trong `com.example.auction.server.service.CloudinaryUploader` trước khi dùng chức năng thêm sản phẩm có ảnh.

---

## 3. Cấu trúc thư mục và các module chính

```text
HeThongDauGia_Team15/
├── pom.xml                          # Maven, dependencies, plugin test
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java     # Java module (com.auction)
│   │   │   └── com/example/auction/
│   │   │       ├── client/          # JavaFX UI + controller + socket client
│   │   │       │   ├── view/        # Main, AuctionClientApp, FXML entry
│   │   │       │   ├── controller/  # Login, Admin, Bid, Payment, ...
│   │   │       │   └── clientnetworkmanager/
│   │   │       ├── server/          # AuctionServer, handlers, services, entity
│   │   │       │   ├── handlers/    # Factory + xử lý từng loại request
│   │   │       │   ├── service/     # Auction, Bid, Payment, Client, ...
│   │   │       │   ├── entity/      # User, Auction, Item, Bid, ...
│   │   │       │   └── servernetworkmanager/
│   │   │       ├── database/dao/    # Truy cập MySQL (DAO pattern)
│   │   │       └── shared/dto/      # DTO + MessageProtocol
│   │   └── resources/
│   │       ├── com/example/auction/client/view/   # *.fxml, *.css
│   │       ├── images/
│   │       └── logback.xml
│   └── test/java/                   # Unit test server/client
├── docs/
│   ├── BAO_CAO.md                   # Nội dung báo cáo PDF (≤5 trang)
│   └── VIDEO_DEMO_SCRIPT.md         # Kịch bản quay demo
├── release/                         # run-server.bat, run-client.bat
├── src/assembly/dist.xml            # Cấu hình gói ZIP phát hành
└── README.md
```

### Module chính

| Package | Chức năng |
|---------|-----------|
| `server` | Khởi động server, session client, routing handler |
| `server.handlers` | Xử lý LOGIN, BID, SIGNUP, payment, autobid, ... |
| `client` | Giao diện người dùng, kết nối server |
| `database.dao` | CRUD MySQL |
| `shared.dto` | Đối tượng trao đổi client ↔ server |

---

