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

## 4. Vị trí file `.jar`

### Lệnh build

```bash
cd HeThongDauGia_Team15
mvn clean package
```

> Chạy kèm **97 unit test** (JUnit 5). Nếu cần bỏ qua test tạm: thêm `-Dmaven.test.skip=true`.

Plugin dùng trong `pom.xml`:
- **`maven-shade-plugin`** — tạo fat JAR Server và Client (gộp dependency).
- **`javafx-maven-plugin`** — chạy dev: `mvn javafx:run`.
- **`maven-assembly-plugin`** — gói ZIP phát hành kèm script.

JavaFX dependency có **classifier theo OS** (`win` / `linux` / `mac`), tự chọn qua profile Maven khi build.

### File sau `mvn package`

| Thành phần | Đường dẫn | Mô tả |
|------------|-----------|--------|
| **Server JAR** | `target/hethongdaugia-1.0-SNAPSHOT-server.jar` | Fat JAR, không gói JavaFX (~10 MB) |
| **Client JAR** | `target/hethongdaugia-1.0-SNAPSHOT-client.jar` | Fat JAR có JavaFX + native libs (~19 MB) |
| **Gói phát hành ZIP** | `target/hethongdaugia-1.0-SNAPSHOT-dist.zip` | Gồm 2 JAR + `run-server.bat` + `run-client.bat` |
| JAR mặc định (thin) | `target/hethongdaugia-1.0-SNAPSHOT.jar` | Chỉ mã nguồn, **không** dùng để chạy trực tiếp |

Script chạy nhanh (trong thư mục `release/` hoặc sau khi giải nén ZIP):

```bash
# Server
java -jar target/hethongdaugia-1.0-SNAPSHOT-server.jar

# Client (cần JDK 25)
java -jar target/hethongdaugia-1.0-SNAPSHOT-client.jar
```

Hoặc double-click `run-server.bat` / `run-client.bat` sau khi giải nén `hethongdaugia-1.0-SNAPSHOT-dist.zip`.

---

## 5. Hướng dẫn chạy Server / Client theo thứ tự

### Thứ tự bắt buộc

```text
(1) MySQL đang chạy, database "auction" sẵn sàng
        ↓
(2) Khởi động SERVER  (port 5000)
        ↓
(3) Khởi động CLIENT  (kết nối localhost:5000)
```

Nếu chạy Client trước Server, ứng dụng hiển thị màn hình chờ (`waitting.fxml`) cho đến khi Server sẵn sàng.

### Cách 1 — Chạy bằng fat JAR (khuyến nghị khi nộp bài)

Xem [Hướng dẫn Build và Run](#hướng-dẫn-build-và-run) ở đầu README.

Hoặc giải nén `target/hethongdaugia-1.0-SNAPSHOT-dist.zip` và dùng `run-server.bat` → `run-client.bat`.

### Cách 2 — IntelliJ IDEA

1. Mở project Maven, đợi index xong.
2. **Run Server:** class `com.example.auction.server.AuctionServer`  
   - Console: `Server đã mở tại port: 5000`
3. **Run Client:** class `com.example.auction.client.view.Main`  
   - Entry JavaFX: `AuctionClientApp` → màn hình đăng nhập / chính.

### Cách 3 — Maven dev (chỉ Client UI)

```bash
mvn clean compile
mvn javafx:run
```

(Server vẫn phải chạy riêng bằng IDE hoặc JAR.)

### Cách 4 — Chạy test

```bash
mvn clean test
```

### Thông số mạng mặc định

| Tham số | Giá trị |
|---------|---------|
| Server port | `5000` |
| Client host | `localhost` |
| MySQL port | `3306` |
| Database | `auction` |

### Kiểm tra nhanh

- Server log: client kết nối → `Đã kết nối tới client: ...`
- Client: mở được giao diện chính sau khi Server chạy.

---

## 6. Danh sách chức năng đã hoàn thành

### Xác thực & tài khoản

- [x] Đăng ký (`SIGNUP`) — tạo tài khoản, ví ký quỹ ban đầu
- [x] Đăng nhập (`LOGIN`) — phân quyền theo `Role`
- [x] Đăng xuất (`LOGOUT`)
- [x] Cập nhật thông tin tài khoản (`UPDATE_ACCOUNT`)
- [x] Khách vãng lai (`GUEST`) — xem không cần đăng nhập

### Sản phẩm & phiên đấu giá

- [x] Thêm sản phẩm (`ADD_PRODUCT`) — nhiều loại (điện tử, nội thất, nghệ thuật, ...), upload ảnh Cloudinary
- [x] Xem sản phẩm đã thêm (`GET_ADDED_PRODUCT`)
- [x] Tạo phiên đấu giá (`ADD_AUCTION`)
- [x] Danh sách phiên đang/active (`GET_AUCTIONS_LIST`)
- [x] Admin duyệt phiên chờ (`GET_PENDING_AUCTIONS`, `APPROVE_AUCTION`)
- [x] Timer tự đóng phiên khi hết giờ; xử lý phiên quá hạn khi server khởi động lại

### Đấu giá

- [x] Đặt giá thủ công (`BID`)
- [x] Đấu giá tự động (`AUTOBID`, `GETCONFIG`)
- [x] Thông báo bị vượt giá (`OUT_BID`)
- [x] Lịch sử giá (`BID_HISTORY`)

### Thanh toán & ký quỹ

- [x] Yêu cầu nạp ký quỹ (`NEW_DEPOSIT_PENDING`)
- [x] Admin duyệt nạp (`APPROVE_DEPOSIT`, `GET_DEPOSIT_PENDING`)
- [x] Lịch sử giao dịch (`GET_TRANSACTION_HISTORY`)

### Thông báo & quản trị

- [x] Lấy danh sách thông báo (`GET_ALL_NOTIFICATION`)
- [x] Giao diện Admin (dashboard, duyệt phiên, duyệt nạp tiền)
- [x] Broadcast / quản lý phiên realtime qua `ClientService`

### Kỹ thuật & chất lượng

- [x] Factory pattern routing handler (`HandlerFactory`)
- [x] DAO truy cập MySQL
- [x] Unit test (JUnit, Mockito) cho handler/service/entity
- [x] Ghi log SLF4J/Logback


## 7. Link báo cáo PDF và video demo

| Tài liệu | Link / File nguồn |
|----------|---------------------|
| **Báo cáo PDF** | https://docs.google.com/document/d/1M7P63hwL60RAXgtx0sR6goIcfAbr-Hsk/edit?usp=drive_link&ouid=109442020964628838245&rtpof=true&sd=true |
| **Video demo** | https://drive.google.com/file/d/1nAnng6pw9m72cDProGnMc29Crw1i8R0p/view?usp=sharing |

---

## 8. Đối chiếu thang điểm (barem)

Chi tiết đầy đủ (giải pháp + lý do từng tiêu chí): **link báo cáo pdf ở trên** — mục 3.

### Bắt buộc (9,5 điểm)

| # | Tiêu chí | Điểm | Trạng thái | Tham chiếu code |
|---|----------|------|------------|-----------------|
| 1 | Thiết kế lớp & cây kế thừa | 0,5 | ✅ | `Entity` → `User`, `Item`, `Auction`, `Bid`; `Item` → `ArtItem`, `ElectronicsItem`, `FurnitureItem` |
| 2 | OOP (4 tính chất) | 1,0 | ✅ | Encapsulation `Entity`; Polymorphism `Item.getDetailed()`; Abstract `RequestHandler`, `Item` |
| 3 | Design Patterns | 1,0 | ✅ | `HandlerFactory`, `ItemFactory`; Singleton DAO / `AuctionClientSocket` / `ClientService` |
| 4 | Quản lý người dùng & sản phẩm | 1,0 | ✅ | `LoginHandler`, `SignupHandler`, `AddProductHandler`, `UserDAO`, `ItemDAO` |
| 5 | Chức năng đấu giá | 1,0 | ✅ | `BidHandler`, `AddAuctionHandler`, `AuctionService` (timer đóng phiên) |
| 6 | Xử lý lỗi & ngoại lệ | 1,0 | ✅ | `RequestTypeException`, `MessageProtocol` status ERROR, validate entity, SLF4J |
| 7 | Đấu giá đồng thời (concurrency) | 1,0 | ✅ | `BidHandler` lock theo `auctionId`; virtual thread server; `ConcurrentHashMap` |
| 8 | Realtime update (Socket) | 0,5 | ✅ | Broadcast `NEW_BID`, `OUT_BID`; `MessageHandler` + `Platform.runLater` |
| 9 | Kiến trúc Client–Server | 0,5 | ✅ | `AuctionServer`, `AuctionClientSocket`, port 5000 |
| 10 | MVC (JavaFX + FXML + DAO) | 0,5 | ✅ | `*.fxml` + `*Controller`; entity/service; `database.dao.*` |
| 11 | Maven / coding convention | 0,5 | ✅ | `pom.xml`, fat JAR shade, package naming |
| 12 | Unit Test (JUnit) | 0,5 | ✅ | `mvn test` — **97 tests**; JaCoCo ≥ 60% |
| 13 | CI/CD (GitHub Actions) | 0,5 | ✅ | [`.github/workflows/main.yml`](.github/workflows/main.yml) |

### Tùy chọn (+1 điểm)

| # | Tiêu chí | Điểm | Trạng thái | Tham chiếu code |
|---|----------|------|------------|-----------------|
| 14 | Auto-Bidding | 0,5 | ✅ | `AutobidHandler`, `AutobidConfigDAO`, `Autobid-dialog.fxml` |
| 15 | Anti-sniping | 0,5 | ✅ | `AuctionService.antiSniping()` (+30s, reschedule timer) |
| 16 | Bid History Visualization | 0,5 | ✅ | `BidHistoryHandler`, `DetailController` TableView lịch sử giá |

**Tổng ước tính:** 9,5 (bắt buộc) + 1,5 (tùy chọn) = **11/10** *(nếu chấm đủ optional)*.
