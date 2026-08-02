$ErrorActionPreference = "Stop"

$backendRoot = $PSScriptRoot
$envPath = Join-Path $backendRoot ".env"

if (Test-Path -LiteralPath $envPath) {
    foreach ($line in Get-Content -LiteralPath $envPath -Encoding utf8) {
        $trimmed = $line.Trim()
        if (-not $trimmed -or $trimmed.StartsWith("#") -or -not $trimmed.Contains("=")) {
            continue
        }

        $parts = $trimmed.Split("=", 2)
        [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
    }
}

$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$maven = "C:\Tools\apache-maven-3.9.16\bin\mvn.cmd"

if (-not (Test-Path -LiteralPath $maven)) {
    throw "Maven was not found at $maven"
}

Set-Location -LiteralPath $backendRoot
& $maven spring-boot:run
