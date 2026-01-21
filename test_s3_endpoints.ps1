# Test S3 API Endpoints
# Make sure to replace &lt;TOKEN&gt; with your actual JWT token from login

$baseUrl = "http://localhost:8080/api/s3"
$token = "&lt;TOKEN&gt;"

$headers = @{
    "Authorization" = "Bearer $token"
}

Write-Host "=== S3 API Test Script ===" -ForegroundColor Cyan
Write-Host ""

# 1. Upload File
Write-Host "1. Testing File Upload..." -ForegroundColor Yellow
$testFile = "test-file.txt"
"This is a test file for S3 upload" | Out-File -FilePath $testFile -Encoding UTF8

$form = @{
    file = Get-Item $testFile
    folder = "test-documents"
}

try {
    $uploadResponse = Invoke-RestMethod -Uri "$baseUrl/upload" -Method Post -Form $form -Headers $headers
    Write-Host "✓ Upload successful!" -ForegroundColor Green
    Write-Host "Response: $($uploadResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Gray
    $fileKey = $uploadResponse.data.key
    Write-Host "File Key: $fileKey" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Upload failed: $_" -ForegroundColor Red
}

Write-Host ""

# 2. List Files
Write-Host "2. Testing List Files..." -ForegroundColor Yellow
try {
    $listResponse = Invoke-RestMethod -Uri "$baseUrl/list?prefix=test-documents" -Headers $headers
    Write-Host "✓ List successful!" -ForegroundColor Green
    Write-Host "Total Files: $($listResponse.data.totalFiles)" -ForegroundColor Cyan
    Write-Host "Response: $($listResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Gray
} catch {
    Write-Host "✗ List failed: $_" -ForegroundColor Red
}

Write-Host ""

# 3. Get File Metadata
if ($fileKey) {
    Write-Host "3. Testing Get File Metadata..." -ForegroundColor Yellow
    try {
        $metadataResponse = Invoke-RestMethod -Uri "$baseUrl/metadata/$fileKey" -Headers $headers
        Write-Host "✓ Get metadata successful!" -ForegroundColor Green
        Write-Host "Response: $($metadataResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ Get metadata failed: $_" -ForegroundColor Red
    }
    Write-Host ""
}

# 4. Get Presigned URL
if ($fileKey) {
    Write-Host "4. Testing Get Presigned URL..." -ForegroundColor Yellow
    try {
        $urlResponse = Invoke-RestMethod -Uri "$baseUrl/url/$fileKey?expirationMinutes=30" -Headers $headers
        Write-Host "✓ Get presigned URL successful!" -ForegroundColor Green
        Write-Host "URL: $($urlResponse.data.url)" -ForegroundColor Cyan
        Write-Host "Expires in: $($urlResponse.data.expiresInSeconds) seconds" -ForegroundColor Cyan
    } catch {
        Write-Host "✗ Get presigned URL failed: $_" -ForegroundColor Red
    }
    Write-Host ""
}

# 5. Download File
if ($fileKey) {
    Write-Host "5. Testing File Download..." -ForegroundColor Yellow
    try {
        $downloadPath = "downloaded-file.txt"
        Invoke-RestMethod -Uri "$baseUrl/download/$fileKey" -OutFile $downloadPath -Headers $headers
        Write-Host "✓ Download successful!" -ForegroundColor Green
        Write-Host "File saved to: $downloadPath" -ForegroundColor Cyan
        
        # Verify content
        $content = Get-Content $downloadPath -Raw
        Write-Host "Downloaded content: $content" -ForegroundColor Gray
        
        # Cleanup
        Remove-Item $downloadPath -Force
    } catch {
        Write-Host "✗ Download failed: $_" -ForegroundColor Red
    }
    Write-Host ""
}

# 6. Delete File
if ($fileKey) {
    Write-Host "6. Testing File Delete..." -ForegroundColor Yellow
    try {
        $deleteResponse = Invoke-RestMethod -Uri "$baseUrl/$fileKey" -Method Delete -Headers $headers
        Write-Host "✓ Delete successful!" -ForegroundColor Green
        Write-Host "Response: $($deleteResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Gray
    } catch {
        Write-Host "✗ Delete failed: $_" -ForegroundColor Red
    }
    Write-Host ""
}

# Cleanup test file
Remove-Item $testFile -Force -ErrorAction SilentlyContinue

Write-Host "=== Test Complete ===" -ForegroundColor Cyan
