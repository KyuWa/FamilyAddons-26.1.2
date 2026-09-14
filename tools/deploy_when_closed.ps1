# Waits for Minecraft to exit, then replaces the FamilyAddons jar in each Modrinth profile
# with the freshly built -dev jar. Paths are per-machine: edit $profiles if a profile is renamed.
$log = "$PSScriptRoot\deploy_when_closed.log"
"waiting for game to close: $(Get-Date)" | Out-File $log -Encoding utf8
while (Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -match 'net\.minecraft\.client\.main\.Main' }) { Start-Sleep -Seconds 5 }
Start-Sleep -Seconds 3

$code = "$env:USERPROFILE\code"
$profiles = @{
    "26.1.2" = "$env:APPDATA\ModrinthApp\profiles\26.1.2 Fabric 1.0.0 (1)\mods"
    "26.2"   = "$env:APPDATA\ModrinthApp\profiles\Fabric 26.2\mods"
}
foreach ($mc in $profiles.Keys) {
    $mods = $profiles[$mc]
    $jar = "$code\FamilyAddons-$mc\build\libs\FamilyAddons-$mc-dev.jar"
    if (-not (Test-Path $mods)) { "skip ${mc}: no profile at $mods" | Out-File $log -Append -Encoding utf8; continue }
    if (-not (Test-Path $jar))  { "skip ${mc}: no build at $jar"    | Out-File $log -Append -Encoding utf8; continue }
    Get-ChildItem $mods | Where-Object { ($_.Name -like 'FamilyAddons*.jar' -and $_.Name -ne "FamilyAddons-$mc-dev.jar") -or $_.Name -eq 'fa_update_cleanup.bat' } | Remove-Item -Force
    Copy-Item -Force $jar $mods
    "copied ${mc}: $(Get-Date)" | Out-File $log -Append -Encoding utf8
    Get-ChildItem $mods -Filter 'FamilyAddons*' | Select-Object Name, Length, LastWriteTime | Out-String | Out-File $log -Append -Encoding utf8
}
