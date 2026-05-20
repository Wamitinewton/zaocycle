# ZaoCycle — Rider App API Reference

**Base URL:** `http://localhost:8080` (development) / configured via `SERVER_PORT`  
**API prefix:** `/api/v1`  
**Content-Type:** `application/json` (unless noted)  
**Authentication:** Bearer JWT in `Authorization` header — `Authorization: Bearer <accessToken>`

---

## Response Envelope

All successful responses (except `204 No Content`) are wrapped in a standard envelope:

```json
{
  "success": true,
  "data": <response body>,
  "message": null
}
```

`message` is non-null only when the endpoint sends an informational string alongside the data.  
Error responses are **not** wrapped — see [Section 5](#5-error-responses).

---

## Table of Contents

1. [Authentication & Tokens](#1-authentication--tokens)
2. [Profile Image](#2-profile-image)
3. [Pickups](#3-pickups)
4. [Enumerations Reference](#4-enumerations-reference)
5. [Error Responses](#5-error-responses)

---

## 1. Authentication & Tokens

Tokens expire: access token = 15 min, refresh token = 30 days.

---

### POST `/api/v1/auth/login` — Public

Login with a phone number and password. The backend resolves the role automatically from the identifier and credential format — no role selection needed.

**Request body:**

```json
{
  "identifier": "+254722000001",
  "credential": "riderpass"
}
```

| Field        | Type   | Required | Notes                       |
|--------------|--------|----------|-----------------------------|
| `identifier` | string | yes      | E.164 phone number          |
| `credential` | string | yes      | Password (min 6 characters) |

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "user": {
      "id": "f5a6b7c8-5717-4562-b3fc-2c963f66afa6",
      "role": "RIDER",
      "displayName": "Peter Mwangi"
    }
  },
  "message": null
}
```

| Field               | Notes                                            |
|---------------------|--------------------------------------------------|
| `data.accessToken`  | JWT — include in `Authorization: Bearer` header  |
| `data.refreshToken` | Long-lived token used to get new token pairs     |
| `data.expiresIn`    | Access token lifetime in seconds (900 = 15 min)  |
| `data.user.role`    | Will be `"RIDER"` for rider accounts             |

**Error responses:**

- `400 Bad Request` — blank `identifier` or `credential`
- `401 Unauthorized` — unrecognised phone number or wrong password
- `422 Unprocessable Entity` — account is inactive

---

### GET `/api/v1/auth/me` — Requires authentication

Returns the authenticated user's identity from the current access token. Use on app startup to restore session state without decoding the JWT client-side.

**Headers:** `Authorization: Bearer <accessToken>`

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "id": "f5a6b7c8-5717-4562-b3fc-2c963f66afa6",
    "role": "RIDER",
    "displayName": "Peter Mwangi",
    "phone": "+254722000001",
    "email": null
  },
  "message": null
}
```

| Field        | Notes                                        |
|--------------|----------------------------------------------|
| `data.phone` | Always present for riders                    |
| `data.email` | Always `null` for riders                     |

**Error responses:**

- `401 Unauthorized` — missing or expired access token

---

### POST `/api/v1/auth/refresh` — Public

Exchange a still-valid refresh token for a new token pair. Call this when the access token expires (`401` on any authenticated endpoint) before prompting the user to re-login.

**Request body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `200 OK`:** Envelope wrapping a new `TokenResponse` (same shape as `data` in `/auth/login`).

**Error responses:**

- `401 Unauthorized` — refresh token is expired or already revoked

---

### POST `/api/v1/auth/logout` — Public

Invalidates the refresh token server-side. Call this when the rider logs out of the app.

**Request body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `204 No Content`** — no body.

---

## 2. Profile Image

Requires: authenticated rider (`Authorization: Bearer <accessToken>`).

---

### POST `/api/v1/profile/image`

Upload a rider profile photo. Replaces any previously uploaded image.

**Content-Type:** `multipart/form-data`

**Form field:** `file` — image file (JPEG/PNG recommended).

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "imageUrl": "https://sampleokoasem.sfo3.digitaloceanspaces.com/profiles/f5a6b7c8.jpg"
  },
  "message": null
}
```

---

### GET `/api/v1/profile/image`

Get the authenticated rider's current profile image URL.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "imageUrl": "https://sampleokoasem.sfo3.digitaloceanspaces.com/profiles/f5a6b7c8.jpg"
  },
  "message": null
}
```

`imageUrl` is `null` if no profile photo has been uploaded yet.

---

## 3. Pickups

Requires role: **RIDER** (`Authorization: Bearer <accessToken>`).

All pickup responses use the `RiderPickupDetailResponse` shape documented below.

### `RiderPickupDetailResponse` shape

```json
{
  "id": "f1a2b3c4-...",
  "farmerId": "c0d1e2f3-...",
  "riderId": "f5a6b7c8-...",
  "applicationId": "d1e2f3a4-...",
  "requestedAt": "2025-05-10T07:30:00Z",
  "scheduledFor": "2025-05-11",
  "status": "ASSIGNED",
  "weightKg": null,
  "photoUrl": null,
  "notes": null,
  "payoutAmount": null,
  "collectedAt": null,
  "paidAt": null,
  "createdAt": "2025-05-10T07:30:00Z",
  "pickupLatitude": -0.6890,
  "pickupLongitude": 37.3513,
  "pickupTradingCenter": "Mwea Town",
  "farmerPhone": "+254711000001"
}
```

| Field                 | Type            | Notes                                                      |
|-----------------------|-----------------|------------------------------------------------------------|
| `id`                  | UUID            | Pickup ID                                                  |
| `farmerId`            | UUID            | ID of the farmer who requested the pickup                  |
| `riderId`             | UUID            | ID of the assigned rider — matches authenticated rider     |
| `applicationId`       | UUID            | Linked pesticide application ID                            |
| `requestedAt`         | ISO 8601 (UTC)  | When the farmer requested the pickup via USSD              |
| `scheduledFor`        | ISO 8601 date   | Date the pickup is scheduled for                           |
| `status`              | enum            | See [PickupStatus](#pickupstatus)                          |
| `weightKg`            | decimal / null  | Filled in after the rider marks collected                  |
| `photoUrl`            | string / null   | CDN URL of the proof photo, once uploaded                  |
| `notes`               | string / null   | Rider's collection notes                                   |
| `payoutAmount`        | decimal / null  | Calculated farmer payout amount, set on collection         |
| `collectedAt`         | ISO 8601 / null | Timestamp the rider marked collected                       |
| `paidAt`              | ISO 8601 / null | Timestamp the farmer payout was confirmed via M-Pesa       |
| `createdAt`           | ISO 8601 (UTC)  | Record creation time                                       |
| `pickupLatitude`      | double / null   | Farmer's registered GPS latitude (for map navigation)      |
| `pickupLongitude`     | double / null   | Farmer's registered GPS longitude (for map navigation)     |
| `pickupTradingCenter` | string / null   | Farmer's nearest trading centre                            |
| `farmerPhone`         | string / null   | Farmer's phone number (for calling from the app)           |

---

### GET `/api/v1/rider/pickups/today`

Get all pickups assigned to the authenticated rider and scheduled for today.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": [
    {
      "id": "f1a2b3c4-...",
      "farmerId": "c0d1e2f3-...",
      "riderId": "f5a6b7c8-...",
      "applicationId": "d1e2f3a4-...",
      "requestedAt": "2025-05-10T07:30:00Z",
      "scheduledFor": "2025-05-11",
      "status": "ASSIGNED",
      "weightKg": null,
      "photoUrl": null,
      "notes": null,
      "payoutAmount": null,
      "collectedAt": null,
      "paidAt": null,
      "createdAt": "2025-05-10T07:30:00Z",
      "pickupLatitude": -0.6890,
      "pickupLongitude": 37.3513,
      "pickupTradingCenter": "Mwea Town",
      "farmerPhone": "+254711000001"
    }
  ],
  "message": null
}
```

Returns an empty array `[]` if no pickups are scheduled for today.

---

### GET `/api/v1/rider/pickups/history`

Get the authenticated rider's full pickup history across all dates.

**Response `200 OK`:** Envelope wrapping array of `RiderPickupDetailResponse` (same shape as today's pickups). Ordered by `scheduledFor` descending.

---

### GET `/api/v1/rider/pickups/{id}`

Get a specific pickup by its ID.

**Path param:** `id` — UUID of the pickup.

**Response `200 OK`:** Envelope wrapping a single `RiderPickupDetailResponse`.

**Error responses:**

- `404 Not Found` — pickup does not exist

---

### POST `/api/v1/rider/pickups/{id}/collect`

Mark a pickup as collected. Uploads the recorded weight and an optional proof photo. This transitions the pickup from `ASSIGNED` → `COLLECTED` and triggers the farmer's M-Pesa payout automatically.

**Content-Type:** `multipart/form-data`

**Path param:** `id` — UUID of the pickup to collect.

**Form params:**

| Param       | Type    | Required | Notes                              |
|-------------|---------|----------|------------------------------------|
| `weightKg`  | decimal | yes      | Weight of agricultural waste collected |
| `photo`     | file    | no       | Photo of the collected waste       |
| `notes`     | string  | no       | Collection notes                   |

**Example curl:**

```bash
curl -X POST /api/v1/rider/pickups/f1a2b3c4-.../collect \
  -H "Authorization: Bearer <accessToken>" \
  -F "weightKg=12.5" \
  -F "photo=@waste.jpg" \
  -F "notes=Two bags collected, farmer was present"
```

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "id": "f1a2b3c4-...",
    "farmerId": "c0d1e2f3-...",
    "riderId": "f5a6b7c8-...",
    "applicationId": "d1e2f3a4-...",
    "requestedAt": "2025-05-10T07:30:00Z",
    "scheduledFor": "2025-05-11",
    "status": "COLLECTED",
    "weightKg": 12.50,
    "photoUrl": "https://sampleokoasem.sfo3.digitaloceanspaces.com/pickups/photo-uuid.jpg",
    "notes": "Two bags collected, farmer was present",
    "payoutAmount": 62.50,
    "collectedAt": "2025-05-11T09:15:00Z",
    "paidAt": null,
    "createdAt": "2025-05-10T07:30:00Z",
    "pickupLatitude": -0.6890,
    "pickupLongitude": 37.3513,
    "pickupTradingCenter": "Mwea Town",
    "farmerPhone": "+254711000001"
  },
  "message": null
}
```

**Error responses:**

- `422 Unprocessable Entity` — pickup is not in `ASSIGNED` status (e.g., already collected or cancelled)
- `404 Not Found` — pickup does not exist

---

## 4. Enumerations Reference

### PickupStatus

| Value       | Meaning                                                             |
|-------------|---------------------------------------------------------------------|
| `REQUESTED` | Farmer requested pickup via USSD — not yet assigned to a rider      |
| `ASSIGNED`  | Rider assigned by coop manager — rider should see this in today's list |
| `COLLECTED` | Rider has marked the pickup as collected                            |
| `PAID`      | Farmer payout sent and confirmed via M-Pesa                         |
| `CANCELLED` | Pickup cancelled by staff                                           |
| `FAILED`    | Pickup or payout failed                                             |

### Ward

| Value               | Display Name      |
|---------------------|-------------------|
| `MWEA`              | Mwea              |
| `GICHUGU`           | Gichugu           |
| `KIRINYAGA_CENTRAL` | Kirinyaga Central |
| `NDIA`              | Ndia              |

---

## 5. Error Responses

All errors return a consistent JSON body (not wrapped in `ApiResponse`):

```json
{
  "message": "Human-readable error description"
}
```

| HTTP Status                 | Trigger                                             |
|-----------------------------|-----------------------------------------------------|
| `400 Bad Request`           | Missing/invalid request fields (validation failure) |
| `401 Unauthorized`          | Bad credentials, expired/missing JWT                |
| `403 Forbidden`             | Authenticated but wrong role                        |
| `404 Not Found`             | Resource not found                                  |
| `422 Unprocessable Entity`  | Business rule violation (e.g. wrong pickup status)  |
| `500 Internal Server Error` | Unexpected server error                             |

**Validation error example:**

```json
{
  "message": "phone: must not be blank, password: size must be between 6 and 2147483647"
}
```

---

## Token Lifecycle — Recommended App Flow

```
App launch
  └─ Has stored tokens?
       ├─ Yes → GET /auth/me
       │          ├─ 200 → restore session, navigate to home
       │          └─ 401 → try POST /auth/refresh
       │                     ├─ 200 → store new tokens, retry /auth/me
       │                     └─ 401 → redirect to login screen
       └─ No  → show login screen

Login screen
  └─ POST /auth/login (phone + password)
       ├─ 200 → store accessToken + refreshToken, navigate to home
       ├─ 401 → "Invalid phone or password"
       └─ 422 → "Your account is inactive — contact your manager"

Any API call returns 401
  └─ POST /auth/refresh
       ├─ 200 → retry original request with new accessToken
       └─ 401 → clear tokens, redirect to login screen

Logout
  └─ POST /auth/logout (refreshToken)
       └─ 204 → clear stored tokens, show login screen
```
