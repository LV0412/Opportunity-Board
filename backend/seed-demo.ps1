param(
    [switch]$ConfirmReplaceData,
    [switch]$AllowRemoteDatabase
)

$ErrorActionPreference = "Stop"

if (-not $ConfirmReplaceData) {
    throw "Script sẽ thay thế toàn bộ dữ liệu nghiệp vụ. Chạy lại với -ConfirmReplaceData để xác nhận."
}

$backendRoot = $PSScriptRoot
$envPath = Join-Path $backendRoot ".env"
$seedPath = Join-Path $backendRoot "src\main\resources\db\demo\demo_flow_data.sql"

if (-not (Test-Path -LiteralPath $envPath)) {
    throw "Không tìm thấy file cấu hình $envPath"
}

foreach ($line in Get-Content -LiteralPath $envPath -Encoding utf8) {
    $trimmed = $line.Trim()
    if (-not $trimmed -or $trimmed.StartsWith("#") -or -not $trimmed.Contains("=")) {
        continue
    }

    $parts = $trimmed.Split("=", 2)
    [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
}

$jdbcUrl = $env:DATABASE_URL
if (-not $jdbcUrl -or -not $jdbcUrl.StartsWith("jdbc:postgresql://")) {
    throw "DATABASE_URL phải có định dạng jdbc:postgresql://host:port/database"
}

$databaseUri = [Uri]$jdbcUrl.Substring(5)
$isLocal = $databaseUri.Host -in @("localhost", "127.0.0.1", "::1")
if (-not $isLocal -and -not $AllowRemoteDatabase) {
    throw "Đang chặn seed database từ xa '$($databaseUri.Host)'. Chỉ dùng -AllowRemoteDatabase khi bạn chắc chắn muốn thay dữ liệu trên môi trường này."
}

$psqlCandidates = @(
    "C:\Program Files\PostgreSQL\15\bin\psql.exe",
    "C:\Program Files\PostgreSQL\16\bin\psql.exe",
    "C:\Program Files\PostgreSQL\17\bin\psql.exe"
)
$psql = $psqlCandidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
if (-not $psql) {
    $psqlCommand = Get-Command psql -ErrorAction SilentlyContinue
    $psql = $psqlCommand.Source
}
if (-not $psql) {
    throw "Không tìm thấy psql. Hãy cài PostgreSQL client hoặc thêm psql vào PATH."
}

$databaseName = $databaseUri.AbsolutePath.TrimStart("/")
if (-not $databaseName) {
    throw "DATABASE_URL chưa có tên database."
}

$env:PGPASSWORD = $env:DATABASE_PASSWORD
try {
    & $psql `
        --username $env:DATABASE_USERNAME `
        --dbname $databaseUri.AbsoluteUri `
        --set ON_ERROR_STOP=1 `
        --file $seedPath

    if ($LASTEXITCODE -ne 0) {
        throw "Nạp dữ liệu demo thất bại với mã lỗi $LASTEXITCODE."
    }
} finally {
    Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Host "Đã nạp dữ liệu demo thành công." -ForegroundColor Green
Write-Host "Tài khoản: student@opportunity.local | organization@opportunity.local | admin@opportunity.local"
Write-Host "Mật khẩu chung: password"
