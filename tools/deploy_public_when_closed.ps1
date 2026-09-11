$log = "$PSScriptRoot\deploy_public_when_closed.log"
"waiting for game to close (PUBLIC test build): $(Get-Date)" | Out-File $log -Encoding utf8
while (Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -match 'net\.minecraft\.client\.main\.Main' }) { Start-Sleep -Seconds 5 }
Start-Sleep -Seconds 3
$m1 = "C:\Users\hkarb\AppData\Roaming\ModrinthApp\profiles\26.1.2 Fabric\mods"
Get-ChildItem $m1 | Where-Object { ($_.Name -like 'FamilyAddons*.jar' -and $_.Name -ne 'FamilyAddons-26.1.2.jar') -or $_.Name -eq 'fa_update_cleanup.bat' } | Remove-Item -Force
Copy-Item -Force "C:\Users\hkarb\code\FamilyAddons-26.1.2\build\libs\FamilyAddons-26.1.2.jar" $m1
"copied PUBLIC jar: $(Get-Date)" | Out-File $log -Append -Encoding utf8
Get-ChildItem $m1 -Filter 'FamilyAddons*' | Select-Object Name, Length, LastWriteTime | Out-String | Out-File $log -Append -Encoding utf8
