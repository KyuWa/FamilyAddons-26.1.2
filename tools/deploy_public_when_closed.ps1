# Same as deploy_when_closed.ps1 but installs the PUBLIC jar (Dev category hidden) for testing
# what everyone else gets. 26.1.2 profile only.
$log = "$PSScriptRoot\deploy_public_when_closed.log"
"waiting for game to close (PUBLIC test build): $(Get-Date)" | Out-File $log -Encoding utf8
while (Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -match 'net\.minecraft\.client\.main\.Main' }) { Start-Sleep -Seconds 5 }
Start-Sleep -Seconds 3
$mods = "$env:APPDATA\ModrinthApp\profiles\26.1.2 Fabric 1.0.0 (1)\mods"
$jar = "$env:USERPROFILE\code\FamilyAddons-26.1.2\build\libs\FamilyAddons-26.1.2.jar"
Get-ChildItem $mods | Where-Object { ($_.Name -like 'FamilyAddons*.jar' -and $_.Name -ne 'FamilyAddons-26.1.2.jar') -or $_.Name -eq 'fa_update_cleanup.bat' } | Remove-Item -Force
Copy-Item -Force $jar $mods
"copied PUBLIC jar: $(Get-Date)" | Out-File $log -Append -Encoding utf8
Get-ChildItem $mods -Filter 'FamilyAddons*' | Select-Object Name, Length, LastWriteTime | Out-String | Out-File $log -Append -Encoding utf8
