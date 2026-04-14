# Alinha JAVA_HOME com o mesmo JDK onde está o "javac" do PATH (útil com vários Java instalados).
# Uso (na raiz do projeto):
#   .\scripts\mvn-com-jdk-do-javac.ps1 clean verify
#   .\scripts\mvn-com-jdk-do-javac.ps1 package

param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $MavenArgs
)

$ErrorActionPreference = "Stop"

$javacCmd = Get-Command javac -ErrorAction SilentlyContinue
if (-not $javacCmd) {
    Write-Error "javac nao encontrado no PATH. Instale um JDK 17 e adicione ...\bin ao PATH."
}

$javacPath = $javacCmd.Source
$binDir = Split-Path -Parent $javacPath
$jdkHome = Split-Path -Parent $binDir

$env:JAVA_HOME = $jdkHome
if ($env:Path -notlike "*$binDir*") {
    $env:Path = "$binDir;$env:Path"
}

Write-Host "JAVA_HOME=$env:JAVA_HOME" -ForegroundColor Cyan
Write-Host "javac=$javacPath" -ForegroundColor Cyan

if (-not $MavenArgs -or $MavenArgs.Count -eq 0) {
    $MavenArgs = @("clean", "verify")
}

& mvn @MavenArgs
exit $LASTEXITCODE
