# Google OAuth2 Testing Guide

This guide walks you through testing the Google OAuth2 implementation in the BagnSave backend.

## Prerequisites

- Backend running: `mvn spring-boot:run` (should see "Started BackendApplication")
- Google credentials configured in `application-local.yaml`
- Browser with developer tools (F12)

---

## Step 1: Start the Backend

```bash
cd backend
mvn spring-boot:run
```

Wait for: `Started BackendApplication in X seconds`

---

## Step 2: Test OAuth Login Flow

1. Open browser → `http://localhost:8080/oauth2/authorization/google`
2. You'll be redirected to **Google login page**
3. Login with your Google account
4. Should redirect back to `http://localhost:5173` (React frontend)

**Expected:** Google login popup, successful authentication

---

## Step 3: Check Session Cookie (Proof OAuth Worked)

1. Press **F12** (Open DevTools)
2. Go to **Application** tab → **Cookies**
3. Select `localhost:8080`
4. Look for `JSESSIONID` cookie

**Results:**
- ✅ `JSESSIONID` exists = OAuth login **worked!**
- ❌ `JSESSIONID` missing = session not created properly

---

## Step 4: Test Protected API Endpoint

1. Open new browser tab: `http://localhost:8080/api/user`
2. Should return JSON with your user info:

```json
{
  "id": 1,
  "email": "your-google-email@gmail.com",
  "name": "Your Google Name"
}
```

**Results:**
- ✅ Returns user data = **OAuth is fully working!**
- ❌ Empty/null response = session not persisted
- ❌ 403/401 error = authorization issue
- ❌ No response = user not authenticated

---

## Troubleshooting

### OAuth redirect loop / not redirecting to Google
- Check `application-local.yaml` has valid Google credentials
- Verify redirect URI in Google Cloud Console: `http://localhost:8080/login/oauth2/code/google`

### `/api/user` returns null
- Session might not be persisting across requests
- Check browser cookies - `JSESSIONID` should be present
- Try clearing cookies and re-authenticating

### 403 Unauthorized on `/api/user`
- Check `SecurityConfig.java` - `/api/user` should have `.authenticated()`
- Verify OAuth login completed before accessing protected endpoint

### Backend crashes with H2 driver error
- Add H2 dependency to `pom.xml`: 
  ```xml
  <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
  </dependency>
  ```

---

## Files Involved

- **OAuth Config:** `src/main/java/com/BagnSave/backend/shared/config/SecurityConfig.java`
- **Login Handler:** `src/main/java/com/BagnSave/backend/shared/config/OAuth2SuccessHandler.java`
- **User Endpoint:** `src/main/java/com/BagnSave/backend/oauth/AccountController.java`
- **User Service:** `src/main/java/com/BagnSave/backend/oauth/AccountServiceImpl.java`
- **User Entity:** `src/main/java/com/BagnSave/backend/oauth/Account.java`
- **Properties:** `src/main/resources/application-local.yaml`

---

## Next Steps

Once testing is complete:
1. Integrate OAuth button in React frontend
2. Update frontend to redirect to `http://localhost:8080/oauth2/authorization/google`
3. After login, redirect to authenticated pages
4. Add logout endpoint: `/logout`
