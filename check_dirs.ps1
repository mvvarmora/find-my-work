function Get-DirSize($path) {
    if (Test-Path $path) {
        $size = (Get-ChildItem $path -Recurse -ErrorAction SilentlyContinue | Measure-Object -Property Length -Sum).Sum
        $mb = [math]::Round($size/1MB, 1)
        $gb = [math]::Round($size/1GB, 2)
        return "$mb MB ($gb GB)"
    } else {
        return "NOT FOUND"
    }
}

Write-Output "=== PROGRAMMING DATA ON C DRIVE ==="
Write-Output ""

Write-Output "1. npm global packages:"
Write-Output "   C:\Users\AM\AppData\Roaming\npm"
Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\AppData\Roaming\npm')"

Write-Output ""
Write-Output "2. npm cache:"
Write-Output "   C:\Users\AM\AppData\Local\npm-cache"
Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\AppData\Local\npm-cache')"

Write-Output ""
Write-Output "3. npm config:"
if (Test-Path 'C:\Users\AM\.npmrc') { Write-Output "   .npmrc exists" } else { Write-Output "   .npmrc NOT FOUND" }
if (Test-Path 'C:\Users\AM\.npm') { Write-Output "   .npm dir: $(Get-DirSize 'C:\Users\AM\.npm')" } else { Write-Output "   .npm dir: NOT FOUND" }

Write-Output ""
Write-Output "4. Gradle:"
Write-Output "   C:\Users\AM\.gradle"
Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\.gradle')"

Write-Output ""
Write-Output "5. Android Studio system:"
Write-Output "   C:\Users\AM\.android"
Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\.android')"
Write-Output "   C:\Users\AM\AppData\Local\Google"
Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\AppData\Local\Google')"

Write-Output ""
Write-Output "6. Java JDK:"
if (Test-Path 'C:\Program Files\Java') {
    Get-ChildItem 'C:\Program Files\Java' | ForEach-Object { Write-Output "   $($_.Name): $(Get-DirSize $_.FullName)" }
} else { Write-Output "   NOT FOUND" }

Write-Output ""
Write-Output "7. Python (if installed):"
if (Test-Path 'C:\Users\AM\AppData\Local\Programs\Python') {
    Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\AppData\Local\Programs\Python')"
} else { Write-Output "   NOT FOUND" }

Write-Output ""
Write-Output "8. VS Code extensions:"
if (Test-Path 'C:\Users\AM\.vscode') {
    Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\.vscode')"
} else { Write-Output "   NOT FOUND" }

Write-Output ""
Write-Output "9. Docker (if installed):"
if (Test-Path 'C:\Users\AM\AppData\Local\Docker') {
    Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\AppData\Local\Docker')"
} else { Write-Output "   NOT FOUND" }

Write-Output ""
Write-Output "10. Yarn cache:"
if (Test-Path 'C:\Users\AM\AppData\Local\Yarn') {
    Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\AppData\Local\Yarn')"
} else { Write-Output "   NOT FOUND" }

Write-Output ""
Write-Output "11. pnpm store:"
if (Test-Path 'C:\Users\AM\AppData\Local\pnpm') {
    Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\AppData\Local\pnpm')"
} else { Write-Output "   NOT FOUND" }
if (Test-Path 'C:\Users\AM\.pnpm-store') {
    Write-Output "   .pnpm-store: $(Get-DirSize 'C:\Users\AM\.pnpm-store')"
}

Write-Output ""
Write-Output "12. Windows Package Cache:"
if (Test-Path 'C:\Users\AM\AppData\Local\Package Cache') {
    Write-Output "   Size: $(Get-DirSize 'C:\Users\AM\AppData\Local\Package Cache')"
} else { Write-Output "   NOT FOUND" }
