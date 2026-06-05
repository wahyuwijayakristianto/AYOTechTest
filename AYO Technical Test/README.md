# Booking Test - Studi Kasus 1

Test automation untuk validasi harga dan double booking.
Data diambil dari `schedules.xlsx` dan `bookings.xlsx` di folder `data/`.

## Requirement

- Python 3 (untuk setup)
- Java 11 atau lebih baru

Cek dengan:
```
java -version
python --version
```

## Setup

Jalankan sekali di terminal:
```
python setup.py
```

Ini akan download JUnit dan generate config editor otomatis.

## Struktur folder

```
├── data/
│   ├── schedules.xlsx
│   └── bookings.xlsx
├── src/
│   ├── main/java/com/booking/
│   │   ├── ExcelReader.java
│   │   └── BookingValidator.java
│   └── test/java/com/booking/
│       └── BookingTest.java
├── pom.xml
└── setup.py
```

## Cara run test

### IntelliJ
Buka folder ini → tunggu Maven sync → buka `BookingTest.java` → klik ▶

### VS Code
Install extension **Extension Pack for Java**, lalu buka folder ini.
VS Code akan otomatis detect `pom.xml` dan setup project.
Buka `BookingTest.java` → klik `Run Test` yang muncul di atas nama class.

### Terminal (tanpa IDE)
```
mvn test
```

## Test cases

| ID | Skenario | Expected |
|----|----------|----------|
| TC-001-A | Semua harga booking vs schedule | FAIL - BK/000001 harga salah |
| TC-001-B | Harga BK/000001 | FAIL - tersimpan 1.200.000, harusnya 1.000.000 |
| TC-001-C | Harga BK/000005 | PASS |
| TC-002-A | Tidak ada double booking | FAIL - BK/000001 & BK/000005 bentrok |
| TC-002-B | BK/000001 dan BK/000005 konflik | PASS |
| TC-002-N1 | Slot berbeda bukan konflik | PASS |
| TC-002-N2 | Venue berbeda bukan konflik | PASS |
