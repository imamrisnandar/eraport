$baseUrl = "http://localhost:8080"
$adminEmail = "admin@mail.com"
$adminPassword = "password123"
$logFile = "test_results.txt"

# Clear log file
"" | Out-File $logFile

function Log-Result {
    param ([string]$Message)
    Write-Host $Message
    Add-Content -Path $logFile -Value $Message
}

function Test-Endpoint {
    param (
        [string]$Method,
        [string]$Uri,
        [hashtable]$Body = $null,
        [hashtable]$Headers = @{}
    )

    Log-Result "Testing $Method $Uri"
    try {
        $params = @{
            Uri         = $Uri
            Method      = $Method
            ContentType = "application/json"
        }
        if ($Body) {
            $params.Body = $Body | ConvertTo-Json -Depth 10
        }
        if ($Headers.Count -gt 0) {
            $params.Headers = $Headers
        }

        $response = Invoke-RestMethod @params
        Log-Result "SUCCESS"
        return $response
    }
    catch {
        Log-Result "FAILED: $_"
        return $null
    }
}

# 1. Login
Log-Result "`n--- 1. Login ---"
$loginBody = @{
    email    = $adminEmail
    password = $adminPassword
}
$loginResponse = Test-Endpoint -Method "POST" -Uri "$baseUrl/api/auth/login" -Body $loginBody

if (-not $loginResponse.success) {
    Log-Result "Login failed. Exiting."
    exit
}

$token = $loginResponse.data.token
$adminId = $loginResponse.data.userId
$headers = @{
    Authorization = "Bearer $token"
}
Log-Result "Token obtained."

# 2. Home Controller
Log-Result "`n--- 2. Home Controller ---"
Test-Endpoint -Method "GET" -Uri "$baseUrl/"
Test-Endpoint -Method "GET" -Uri "$baseUrl/health"

# 3. Create User
Log-Result "`n--- 3. Create User ---"
$rand = Get-Random
$newUserBody = @{
    username = "testuser_$rand"
    password = "password123"
    email    = "testuser_$rand@mail.com"
    fullName = "Test User $rand"
}
$createUserResponse = Test-Endpoint -Method "POST" -Uri "$baseUrl/api/users" -Headers $headers -Body $newUserBody
$newUserId = $createUserResponse.data.id

# 4. Get All Users
Log-Result "`n--- 4. Get All Users ---"
Test-Endpoint -Method "GET" -Uri "$baseUrl/api/users" -Headers $headers

# 5. Get User By ID
Log-Result "`n--- 5. Get User By ID ---"
if ($newUserId) {
    Test-Endpoint -Method "GET" -Uri "$baseUrl/api/users/$newUserId" -Headers $headers
}

# 6. Update User
Log-Result "`n--- 6. Update User ---"
if ($newUserId) {
    $updateUserBody = @{
        username = "testuser_updated_$rand"
        email    = "testuser_updated_$rand@mail.com"
        fullName = "Test User Updated"
    }
    Test-Endpoint -Method "PUT" -Uri "$baseUrl/api/users/$newUserId" -Headers $headers -Body $updateUserBody
}

# 7. Login History
Log-Result "`n--- 7. Login History ---"
if ($adminId) {
    Test-Endpoint -Method "GET" -Uri "$baseUrl/api/login-history/user/$adminId" -Headers $headers
    Test-Endpoint -Method "GET" -Uri "$baseUrl/api/login-history/user/$adminId/active-sessions" -Headers $headers
}

# 8. Activate User
Log-Result "`n--- 8. Activate User ---"
if ($newUserId) {
    Test-Endpoint -Method "POST" -Uri "$baseUrl/api/users/$newUserId/activate" -Headers $headers
}

# 9. Delete User
Log-Result "`n--- 9. Delete User ---"
if ($newUserId) {
    Test-Endpoint -Method "DELETE" -Uri "$baseUrl/api/users/$newUserId" -Headers $headers
}

# 10. Logout
Log-Result "`n--- 10. Logout ---"
Test-Endpoint -Method "POST" -Uri "$baseUrl/api/auth/logout" -Headers $headers

# 11. Actuator (Using Token)
Log-Result "`n--- 11. Actuator Endpoints ---"
# Re-login to get a valid token for actuator tests since we just logged out
$loginResponse = Test-Endpoint -Method "POST" -Uri "$baseUrl/api/auth/login" -Body $loginBody
if ($loginResponse.success) {
    $actuatorHeaders = @{ Authorization = "Bearer $($loginResponse.data.token)" }
    
    Log-Result "Testing /actuator"
    Test-Endpoint -Method "GET" -Uri "$baseUrl/actuator" -Headers $actuatorHeaders

    Log-Result "Testing /actuator/health"
    Test-Endpoint -Method "GET" -Uri "$baseUrl/actuator/health" -Headers $actuatorHeaders

    Log-Result "Testing /actuator/info"
    Test-Endpoint -Method "GET" -Uri "$baseUrl/actuator/info" -Headers $actuatorHeaders

    Log-Result "Testing /actuator/prometheus"
    Test-Endpoint -Method "GET" -Uri "$baseUrl/actuator/prometheus" -Headers $actuatorHeaders
}
else {
    Log-Result "Failed to re-login for actuator tests"
}

Log-Result "`n--- ALL TESTS COMPLETED ---"
