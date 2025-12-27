# verify_apis.ps1

$headers = @{ "Content-Type" = "application/json" }

function Test-Endpoint {
    param($Name, $Method, $Uri, $Body, $Token, $ExpectedStatus)
    Write-Host " Testing: $Name..." -NoNewline
    
    $localHeaders = $headers.Clone()
    if ($Token) { $localHeaders["Authorization"] = "Bearer $Token" }

    try {
        if ($Body) {
             $response = Invoke-RestMethod -Method $Method -Uri $Uri -Headers $localHeaders -Body ($Body | ConvertTo-Json) -ResponseHeadersVariable resHeaders -SkipHttpErrorCheck -StatusCodeVariable statusCode
        } else {
             $response = Invoke-RestMethod -Method $Method -Uri $Uri -Headers $localHeaders -SkipHttpErrorCheck -StatusCodeVariable statusCode
        }

        if ($statusCode -eq $ExpectedStatus) {
            Write-Host " [PASS] (Status: $statusCode)" -ForegroundColor Green
            return $response
        } else {
            Write-Host " [FAIL] (Expected: $ExpectedStatus, Got: $statusCode)" -ForegroundColor Red
            return $null
        }
    } catch {
        Write-Host " [ERROR] $_" -ForegroundColor Red
        return $null
    }
}

# 1. Login User
$userLogin = @{ email = "newuser@visita.com"; password = "password123" }
$userAuth = Test-Endpoint -Name "User Login" -Method Post -Uri "http://localhost:8080/auth/login" -Body $userLogin -ExpectedStatus 200
$userToken = $userAuth.result.token

# 2. Login Admin
$adminLogin = @{ username = "admin"; password = "admin" }
$adminAuth = Test-Endpoint -Name "Admin Login" -Method Post -Uri "http://localhost:8080/auth/login" -Body $adminLogin -ExpectedStatus 200
$adminToken = $adminAuth.result.token

# 3. Get All Users (Admin)
Test-Endpoint -Name "Get All Users (Admin Token)" -Method Get -Uri "http://localhost:8080/admins/users" -Token $adminToken -ExpectedStatus 200 | Out-Null

# 4. Get All Users (User - Should Fail)
Test-Endpoint -Name "Get All Users (User Token)" -Method Get -Uri "http://localhost:8080/admins/users" -Token $userToken -ExpectedStatus 403 | Out-Null
