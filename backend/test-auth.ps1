# test-auth.ps1
# Usage: Start your backend, then run: .\test-auth.ps1

$base = "http://localhost:8080"
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession

# Unique user for this run
$username = "demo_auto_$([int](Get-Date -UFormat %s))"
$password = "Passw0rd!"

function Send-Request {
    param(
        [string]$Method,
        [string]$Uri,
        [string]$Body = $null,
        [Microsoft.PowerShell.Commands.WebRequestSession]$WebSession = $null
    )
    try {
        $invokeParams = @{ Method = $Method; Uri = $Uri; ContentType = "application/json"; ErrorAction = 'Stop' }
        if ($Body -ne $null) { $invokeParams.Body = $Body }
        if ($WebSession -ne $null) { $invokeParams.WebSession = $WebSession }
        # Ensure we never send a request body for GET/HEAD methods (some servers/clients reject it)
        if ($Method -match '^(GET|HEAD)$') {
            if ($invokeParams.ContainsKey('Body')) { $invokeParams.Remove('Body') }
        }
        $resp = Invoke-WebRequest @invokeParams
        $status = if ($resp.StatusCode) { [int]$resp.StatusCode } else { 200 }
        return @{ Status = $status; Content = $resp.Content; Raw = $resp }
    } catch [System.Net.WebException] {
        $webResp = $_.Exception.Response
        if ($webResp -ne $null) {
            $sr = New-Object System.IO.StreamReader($webResp.GetResponseStream())
            $content = $sr.ReadToEnd()
            $sr.Close()
            $status = [int]$webResp.StatusCode
            return @{ Status = $status; Content = $content }
        } else {
            return @{ Status = 0; Content = $_.Exception.Message }
        }
    } catch {
        return @{ Status = 0; Content = $_.Exception.Message }
    }
}

$allPass = $true

function Check-Step {
    param($num, $desc, $result, $expectedStatus, $extraCheckScript = $null)
    $ok = $false
    if ($result.Status -eq $expectedStatus) {
        $ok = $true
    }
    if ($ok -and $extraCheckScript) {
        try {
            # run extra checks - the script should return $true on success
            $checkResult = & $extraCheckScript $result
            if (-not $checkResult) { $ok = $false }
        } catch {
            $ok = $false
        }
    }

    if ($ok) {
        Write-Host "Step ${num}: $desc => PASS ($($result.Status))" -ForegroundColor Green
    } else {
        Write-Host "Step ${num}: $desc => FAIL (got $($result.Status), expected $expectedStatus)" -ForegroundColor Red
        Write-Host "  Response content: $($result.Content)" -ForegroundColor Yellow
        $script:allPass = $false
    }
}

Write-Host "Running auth/session flow tests against $base"
Write-Host "Generated username: $username"

# Step 1: Register (valid new user) -> 201 Created
$body1 = @{ username = $username; password = $password } | ConvertTo-Json
$r1 = Send-Request -Method Post -Uri "$base/api/auth/register" -Body $body1
Check-Step 1 "POST /api/auth/register (valid new user)" $r1 201

# Step 2: Register same username again -> 409 Conflict
$r2 = Send-Request -Method Post -Uri "$base/api/auth/register" -Body $body1
Check-Step 2 "POST /api/auth/register (same username again)" $r2 409

# Step 3: Register (missing fields) -> 400 Bad Request
# Send a payload missing password
$badBody = @{ username = ""; password = "" } | ConvertTo-Json
$r3 = Send-Request -Method Post -Uri "$base/api/auth/register" -Body $badBody
Check-Step 3 "POST /api/auth/register (missing fields)" $r3 400

# Step 4: Login wrong password -> 401 Unauthorized
$badLogin = @{ username = $username; password = "WrongPass" } | ConvertTo-Json
$r4 = Send-Request -Method Post -Uri "$base/api/auth/login" -Body $badLogin
Check-Step 4 "POST /api/auth/login (wrong password)" $r4 401

# Step 5: Login correct credentials -> 200 OK + Set-Cookie: JSESSIONID=...
# Use the session object to capture cookies
$goodLogin = @{ username = $username; password = $password } | ConvertTo-Json
$r5 = Send-Request -Method Post -Uri "$base/api/auth/login" -Body $goodLogin -WebSession $session
# extra check: ensure session cookie exists (JSESSIONID)
$checkCookie = {
    param($res)
    # try multiple ways to find JSESSIONID in the session
    $has = $false
    try {
        $uri = [Uri]"$base/"
        $cookieCollection = $session.Cookies.GetCookies($uri)
        foreach ($c in $cookieCollection) { if ($c.Name -eq 'JSESSIONID') { $has = $true; break } }
    } catch {
        # fallback: iterate session.Cookies if it behaves like an array
        try {
            foreach ($c in $session.Cookies) { if ($c.Name -eq 'JSESSIONID') { $has = $true; break } }
        } catch { }
    }
    return $has
}
Check-Step 5 "POST /api/auth/login (correct credentials)" $r5 200 $checkCookie

# Step 6: GET /lists/me without cookie -> 401 Unauthorized
# call with a fresh session (no cookies)
$freshSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$r6 = Send-Request -Method Get -Uri "$base/api/lists/me" -WebSession $freshSession
Check-Step 6 "GET /api/lists/me (no cookie)" $r6 401

# Step 7: GET /lists/me with cookie from step 5 -> 200 OK + mock list JSON
# Use the session that has cookie from login
$r7 = Send-Request -Method Get -Uri "$base/api/lists/me" -WebSession $session
$checkListBody = {
    param($res)
    if ($res.Status -ne 200) { return $false }
    # try parse JSON
    try {
        $json = $res.Content | ConvertFrom-Json -ErrorAction Stop
        if ($json.username -and $json.lists) { return $true }
        return $false
    } catch {
        return $false
    }
}
Check-Step 7 "GET /api/lists/me (with cookie from step 5)" $r7 200 $checkListBody

# Summary
if ($allPass) {
    Write-Host "ALL TESTS PASSED" -ForegroundColor Green
    exit 0
} else {
    Write-Host "SOME TESTS FAILED" -ForegroundColor Red
    exit 1
}

