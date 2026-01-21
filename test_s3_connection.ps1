# Test S3 Connection API
# Make sure to replace &lt;TOKEN&gt; with your actual JWT token from login

$baseUrl = "http://localhost:8080/api/s3"
$token = "&lt;TOKEN&gt;"

$headers = @{
    "Authorization" = "Bearer $token"
}

Write-Host "=== S3 Connection Test ===" -ForegroundColor Cyan
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/test-connection" -Headers $headers
    
    if ($response.data.connected) {
        Write-Host "✓ Connection Successful!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Details:" -ForegroundColor Cyan
        Write-Host "  Bucket Name: $($response.data.bucketName)" -ForegroundColor White
        Write-Host "  Endpoint: $($response.data.endpoint)" -ForegroundColor White
        Write-Host "  Region: $($response.data.region)" -ForegroundColor White
        Write-Host "  Message: $($response.data.message)" -ForegroundColor White
    }
    else {
        Write-Host "✗ Connection Failed!" -ForegroundColor Red
        Write-Host ""
        Write-Host "Details:" -ForegroundColor Cyan
        Write-Host "  Bucket Name: $($response.data.bucketName)" -ForegroundColor White
        Write-Host "  Endpoint: $($response.data.endpoint)" -ForegroundColor White
        Write-Host "  Region: $($response.data.region)" -ForegroundColor White
        Write-Host "  Error: $($response.data.message)" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "Full Response:" -ForegroundColor Gray
    Write-Host ($response | ConvertTo-Json -Depth 3) -ForegroundColor Gray
    
}
catch {
    Write-Host "✗ Request Failed: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
