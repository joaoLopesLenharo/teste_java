# Executa o JAR sombreado com o mesmo Java do JDK (prioriza JAVA_HOME).
# Uso (na pasta do projeto): .\scripts\executar-jar.ps1
# Uso com argumentos:         .\scripts\executar-jar.ps1 --console

param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $AppArgs
)

$ErrorActionPreference = "Stop"
$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$jar = Join-Path $projectRoot "target\teste-pratico-java-1.0-SNAPSHOT.jar"

if (-not (Test-Path $jar)) {
    Write-Host "JAR nao encontrado: $jar" -ForegroundColor Red
    Write-Host "Execute primeiro: mvn clean package -DskipTests" -ForegroundColor Yellow
    exit 1
}

$javaExe = "java"
if ($env:JAVA_HOME) {
    $cand = Join-Path $env:JAVA_HOME "bin\java.exe"
    if (Test-Path $cand) {
        $javaExe = $cand
    }
}

Write-Host "Java: $javaExe" -ForegroundColor Cyan
& $javaExe -version 2>&1 | Write-Host

& $javaExe -jar $jar @AppArgs
exit $LASTEXITCODE
