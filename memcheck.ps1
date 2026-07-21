Get-Process | Sort-Object WorkingSet64 -Descending | Select-Object -First 10 Name, @{N='MB';E={[math]::Round($_.WorkingSet64/1MB,1)}} | Format-Table -AutoSize
Write-Output "---"
$os = Get-CimInstance Win32_OperatingSystem
$free = [math]::Round($os.FreePhysicalMemory/1024, 1)
$total = [math]::Round($os.TotalVisibleMemorySize/1024, 1)
Write-Output "Free: $free GB / Total: $total GB"
