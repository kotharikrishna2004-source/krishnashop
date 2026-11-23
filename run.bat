@echo off
title Krishna Shop - E-Commerce Platform
color 0A

echo ===============================================================
echo            KRISHNA SHOP - E-COMMERCE PLATFORM
echo ===============================================================
echo.

:: 1. Check if MySQL is running on port 3306
netstat -ano | findstr ":3306 " >nul
if %errorlevel% neq 0 (
    echo [INFO] MySQL is not running. Starting XAMPP MySQL daemon...
    if exist "C:\xampp\mysql\bin\mysqld.exe" (
        start "" "C:\xampp\mysql\bin\mysqld.exe" --defaults-file="C:\xampp\mysql\bin\my.ini" --standalone
        timeout /t 3 /nobreak >nul
    ) else (
        echo [WARNING] XAMPP MySQL not found at C:\xampp\mysql\bin\mysqld.exe.
        echo Please ensure your MySQL server is running on port 3306.
    )
) else (
    echo [OK] MySQL is running on port 3306.
)

:: 2. Launch Krishna Shop application
echo.
echo [INFO] Starting Krishna Shop Spring Boot Application...
echo [INFO] URL: http://localhost:8080
echo.
echo ===============================================================
echo   Default Credentials:
echo   - Admin Panel:   admin@krishnashop.com  /  admin123
echo   - Customer Demo: user@krishnashop.com   /  user123
echo ===============================================================
echo.

start http://localhost:8080

java "@target/run_options.txt" com.krishnashop.KrishnaShopApplication

pause
