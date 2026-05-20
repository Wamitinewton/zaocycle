# ZaoCycle Backend — API Reference

**Base URL:** `http://localhost:8080` (development) / configured via `SERVER_PORT`  
**API prefix:** `/api/v1`  
**Content-Type:** `application/json` (unless noted)  
**Authentication:** Bearer JWT in `Authorization` header — `Authorization: Bearer <accessToken>`

---

## Response Envelope

All successful responses (except `204 No Content`, webhooks, and USSD) are wrapped in a standard envelope:

```json
{
  "success": true,
  "data": <response body>,
  "message": null
}
```

`message` is non-null only when the endpoint sends an informational string alongside the data.
Error responses are **not** wrapped — see [Section 22](#22-error-responses).

---

## Table of Contents

1. [Authentication & Tokens](#1-authentication--tokens) — unified login, `/me`, refresh, logout
2. [Buyer Profile](#2-buyer-profile)
3. [Products (Public)](#3-products-public)
4. [Buyer Orders](#4-buyer-orders)
5. [Farmers (Public)](#5-farmers-public)
6. [Pesticide Applications (Public)](#6-pesticide-applications-public)
7. [Chemicals (Public)](#7-chemicals-public)
8. [Certificate Verification (Public)](#8-certificate-verification-public)
9. [Profile Image](#9-profile-image)
10. [Farmer — Pickups & Earnings](#10-farmer--pickups--earnings)
11. [Rider — Pickups](#11-rider--pickups)
12. [Dashboard — Pickups (Staff)](#12-dashboard--pickups-staff)
13. [Dashboard — Inventory: Intake (Staff)](#13-dashboard--inventory-intake-staff)
14. [Dashboard — Inventory: Batches & Stock (Staff)](#14-dashboard--inventory-batches--stock-staff)
15. [Dashboard — Orders (Staff)](#15-dashboard--orders-staff)
16. [Dashboard — Certificates (Staff)](#16-dashboard--certificates-staff)
17. [Admin — Staff Management](#17-admin--staff-management)
18. [Admin — Rider Management](#18-admin--rider-management)
19. [Admin — Scheduler](#19-admin--scheduler)
20. [Webhooks & Callbacks](#20-webhooks--callbacks)
21. [USSD Gateway (Telecom)](#21-ussd-gateway-telecom)
22. [Error Responses](#22-error-responses)
23. [Enumerations Reference](#23-enumerations-reference)

---

## 1. Authentication & Tokens

Tokens expire: access token = 15 min, refresh token = 30 days.

### POST `/api/v1/auth/login` — Public

Single login endpoint for all user types. The backend detects identity from the identifier format — no role selection
needed on the frontend.

| `identifier` format     | Credential          | Resolved role            |
|-------------------------|---------------------|--------------------------|
| E.164 phone (`+254...`) | PIN (short numeric) | `FARMER`                 |
| E.164 phone (`+254...`) | Password            | `RIDER`                  |
| Email address           | Password            | `COOP_MANAGER` / `ADMIN` |
| Email address           | Password            | `BUYER`                  |

**Request body:**

```json
{
  "identifier": "+254712345678",
  "credential": "1234"
}
```

| Field        | Type   | Required | Notes                                  |
|--------------|--------|----------|----------------------------------------|
| `identifier` | string | yes      | E.164 phone number or email address    |
| `credential` | string | yes      | PIN (farmers) or password (all others) |

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
      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "role": "FARMER",
      "displayName": "John Kamau"
    }
  },
  "message": null
}
```

**Error responses:**

- `400 Bad Request` — blank `identifier` or `credential`
- `401 Unauthorized` — unrecognised identifier, wrong credential, or incomplete farmer registration
- `422 Unprocessable Entity` — account is inactive

The frontend should redirect to the appropriate dashboard based on `data.user.role` in the response.

---

### GET `/api/v1/auth/me` — Requires authentication

Returns the authenticated user's identity from the current access token. Use this on app startup to restore session
state without decoding the JWT client-side.

**Headers:** `Authorization: Bearer <accessToken>`

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "role": "FARMER",
    "displayName": "John Kamau",
    "phone": "+254712345678",
    "email": null
  },
  "message": null
}
```

| Field          | Notes                                                       |
|----------------|-------------------------------------------------------------|
| `data.phone`   | Present for farmers and riders; `null` for staff            |
| `data.email`   | Present for staff and buyers; `null` for farmers and riders |

**Error responses:**

- `401 Unauthorized` — missing or expired access token

---

### POST `/api/v1/auth/buyer/register` — Public

Register a new buyer account and receive tokens immediately.

**Request body:**

```json
{
  "email": "alice@school.go.ke",
  "password": "secret123",
  "phone": "+254712345678",
  "buyerType": "SCHOOL",
  "displayName": "St. Mary's Primary",
  "contactPerson": "Alice Wanjiru",
  "address": "Mwea Town, Kirinyaga",
  "ward": "MWEA"
}
```

| Field           | Type   | Required | Notes                                             |
|-----------------|--------|----------|---------------------------------------------------|
| `email`         | string | yes      | Valid email, used as login credential             |
| `password`      | string | yes      | Min 6 characters                                  |
| `phone`         | string | yes      | E.164 format                                      |
| `buyerType`     | enum   | yes      | `SCHOOL`, `INDIVIDUAL`, `INSTITUTION`, `BUSINESS` |
| `displayName`   | string | yes      | Organisation/person name shown in UI              |
| `contactPerson` | string | no       | Contact name for organisations                    |
| `address`       | string | no       | Physical delivery address                         |
| `ward`          | string | no       | One of the 4 Kirinyaga ward names                 |

**Response `201 Created`:** Envelope wrapping `TokenResponse` (same shape as `data` in `/auth/login`).

---

### POST `/api/v1/auth/refresh` — Public

Exchange a still-valid refresh token for a new token pair.

**Request body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `200 OK`:** Envelope wrapping new `TokenResponse`.

---

### POST `/api/v1/auth/logout` — Public

Invalidates the refresh token (server-side revocation via Redis).

**Request body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `204 No Content`** — no body.

---

## 2. Buyer Profile

Requires role: **BUYER**

### GET `/api/v1/buyer/me`

Get the authenticated buyer's profile.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "alice@school.go.ke",
    "phone": "+254712345678",
    "buyerType": "SCHOOL",
    "displayName": "St. Mary's Primary",
    "contactPerson": "Alice Wanjiru",
    "address": "Mwea Town, Kirinyaga",
    "ward": "MWEA"
  },
  "message": null
}
```

---

### PATCH `/api/v1/buyer/me`

Update the authenticated buyer's profile.

**Request body:**

```json
{
  "displayName": "St. Mary's Primary School",
  "contactPerson": "Alice Wanjiru",
  "address": "Main Street, Mwea",
  "ward": "MWEA"
}
```

| Field           | Type   | Required |
|-----------------|--------|----------|
| `displayName`   | string | yes      |
| `contactPerson` | string | no       |
| `address`       | string | no       |
| `ward`          | string | no       |

**Response `200 OK`:** Envelope wrapping updated `BuyerProfileResponse`.

---

## 3. Products (Public)

No authentication required.

### GET `/api/v1/products`

List all active briquette products.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": [
    {
      "id": "a1b2c3d4-...",
      "sku": "BRQ-5KG",
      "name": "5 kg Briquette Bag",
      "description": "Compressed agricultural waste briquettes, 5 kg pack",
      "weightKg": 5.00,
      "unitPrice": 350.00,
      "imageUrl": "https://cdn.zaocycle.app/products/brq-5kg.jpg",
      "sortOrder": 1
    }
  ],
  "message": null
}
```

---

### GET `/api/v1/products/{id}`

Get a single product by UUID.

**Path param:** `id` — UUID of the product.

**Response `200 OK`:** Envelope wrapping single `ProductResponse` (same fields as list item).

**Response `404 Not Found`:** Product does not exist.

---

## 4. Buyer Orders

Requires role: **BUYER**

### POST `/api/v1/buyer/orders`

Place a new briquette order. Triggers M-Pesa STK Push for payment.

**Request body:**

```json
{
  "productId": "a1b2c3d4-...",
  "quantity": 3,
  "deliveryAddress": "Main Street, Mwea",
  "deliveryPhone": "+254712345678",
  "requestedDelivery": "2025-06-15",
  "notes": "Leave at the gate"
}
```

| Field               | Type    | Required | Notes                        |
|---------------------|---------|----------|------------------------------|
| `productId`         | UUID    | yes      | Must be an active product    |
| `quantity`          | integer | yes      | Min 1                        |
| `deliveryAddress`   | string  | yes      |                              |
| `deliveryPhone`     | string  | yes      | Phone to receive M-Pesa push |
| `requestedDelivery` | date    | no       | ISO 8601 date: `YYYY-MM-DD`  |
| `notes`             | string  | no       | Delivery instructions        |

**Response `201 Created`:**

```json
{
  "success": true,
  "data": {
    "id": "f47ac10b-...",
    "buyerId": "3fa85f64-...",
    "productId": "a1b2c3d4-...",
    "quantity": 3,
    "unitPrice": 350.00,
    "totalKg": 15.00,
    "totalAmount": 1050.00,
    "deliveryAddress": "Main Street, Mwea",
    "deliveryPhone": "+254712345678",
    "requestedDelivery": "2025-06-15",
    "deliveredAt": null,
    "status": "PENDING_PAYMENT",
    "mpesaTransactionId": null,
    "notes": "Leave at the gate",
    "createdAt": "2025-05-19T10:00:00Z",
    "updatedAt": "2025-05-19T10:00:00Z"
  },
  "message": null
}
```

---

### GET `/api/v1/buyer/orders`

List the authenticated buyer's orders, paginated.

**Query params:**
| Param | Type | Default | Notes |
|---|---|---|---|
| `page` | int | 0 | Zero-based page index |
| `size` | int | 20 | Page size |
| `sort` | string | — | e.g. `createdAt,desc` |

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "content": [
      /* array of OrderResponse */
    ],
    "totalElements": 5,
    "totalPages": 1,
    "size": 20,
    "number": 0
  },
  "message": null
}
```

---

### GET `/api/v1/buyer/orders/{id}`

Get a single order. The buyer can only access their own orders.

**Response `200 OK`:** Envelope wrapping `OrderResponse`.  
**Response `404 Not Found`:** Order not found or not owned by this buyer.

---

### DELETE `/api/v1/buyer/orders/{id}`

Cancel an order (only if `PENDING_PAYMENT`).

**Response `200 OK`:** Envelope wrapping updated `OrderResponse` with `status: "CANCELLED"`.

---

## 5. Farmers (Public)

No authentication required.

### GET `/api/v1/farmers/by-phone`

Look up a farmer by their phone number (used by the USSD login flow).

**Query param:** `phone` — E.164 phone number.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "id": "c0d1e2f3-...",
    "phone": "+254711000001",
    "fullName": "John Kamau",
    "ward": "Mwea",
    "registrationComplete": true
  },
  "message": null
}
```

**Response `404 Not Found`:** Farmer not found.

---

## 6. Pesticide Applications (Public)

No authentication required.

### GET `/api/v1/pesticide-applications/farmer/{farmerId}`

List all pesticide application records for a farmer.

**Path param:** `farmerId` — UUID of the farmer.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": [
    {
      "id": "d1e2f3a4-...",
      "farmerId": "c0d1e2f3-...",
      "chemicalId": "e2f3a4b5-...",
      "crop": "Rice",
      "quantityMl": 500.0,
      "safeHarvestDate": "2025-06-01",
      "status": "SAFE"
    }
  ],
  "message": null
}
```

---

## 7. Chemicals (Public)

No authentication required.

### GET `/api/v1/chemicals`

List all active chemicals in the system.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": [
    {
      "id": "e2f3a4b5-...",
      "name": "Mancozeb 80%",
      "activeIngredient": "Mancozeb",
      "category": "FUNGICIDE",
      "halfLifeDays": 7,
      "phiDays": 14,
      "commonCrops": "Rice, Maize, Beans"
    }
  ],
  "message": null
}
```

---

### GET `/api/v1/chemicals/{id}`

Get a single chemical by UUID.

**Response `200 OK`:** Envelope wrapping single `ChemicalResponse`.  
**Response `404 Not Found`:** Chemical not found.

---

## 8. Certificate Verification (Public)

No authentication required. Used by QR code scanners and buyers.

### GET `/api/v1/certificates/verify/{token}`

Verify a produce safety certificate by its unique token (embedded in QR code).
Each call increments the `verifiedCount`.

**Path param:** `token` — alphanumeric verification token from QR code.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "token": "ZAO-A1B2C3D4",
    "status": "ACTIVE",
    "issuedAt": "2025-05-15T08:00:00Z",
    "expiresAt": "2025-05-29T08:00:00Z",
    "verifiedCount": 3,
    "crop": "Rice",
    "chemicalName": "Mancozeb 80%",
    "farmerName": "John Kamau",
    "ward": "Mwea"
  },
  "message": null
}
```

**Response `404 Not Found`:** Certificate token does not exist.

---

## 9. Profile Image

Requires: any authenticated user.

### POST `/api/v1/profile/image`

Upload a profile photo. Multipart form upload.

**Content-Type:** `multipart/form-data`

**Form field:** `file` — image file (JPEG/PNG recommended).

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "imageUrl": "https://sampleokoasem.sfo3.digitaloceanspaces.com/profiles/uuid.jpg"
  },
  "message": null
}
```

---

### GET `/api/v1/profile/image`

Get the authenticated user's current profile image URL.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "imageUrl": "https://sampleokoasem.sfo3.digitaloceanspaces.com/profiles/uuid.jpg"
  },
  "message": null
}
```

---

## 10. Farmer — Pickups & Earnings

Requires role: **FARMER**

### GET `/api/v1/farmer/pickups`

Get all waste pickups for the authenticated farmer.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": [
    {
      "id": "f1a2b3c4-...",
      "farmerId": "c0d1e2f3-...",
      "riderId": "b9a8c7d6-...",
      "applicationId": "d1e2f3a4-...",
      "requestedAt": "2025-05-10T07:30:00Z",
      "scheduledFor": "2025-05-11",
      "status": "PAID",
      "weightKg": 12.50,
      "photoUrl": "https://cdn.zaocycle.app/pickups/photo.jpg",
      "notes": "Two bags collected",
      "payoutAmount": 62.50,
      "collectedAt": "2025-05-11T09:00:00Z",
      "paidAt": "2025-05-11T14:30:00Z",
      "createdAt": "2025-05-10T07:30:00Z"
    }
  ],
  "message": null
}
```

---

### GET `/api/v1/farmer/earnings`

Get the authenticated farmer's earnings summary.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "total": 312.50,
    "thisMonth": 125.00,
    "pickupCount": 25,
    "recentPayouts": [
      {
        "amount": 62.50,
        "date": "2025-05-11"
      },
      {
        "amount": 37.50,
        "date": "2025-05-04"
      }
    ]
  },
  "message": null
}
```

---

## 11. Rider — Pickups

Requires role: **RIDER**

### GET `/api/v1/rider/pickups/today`

Get all pickups scheduled for the authenticated rider today.

**Response `200 OK`:** Envelope wrapping array of `RiderPickupDetailResponse`.

---

### GET `/api/v1/rider/pickups/history`

Get the authenticated rider's full pickup history.

**Response `200 OK`:** Envelope wrapping array of `RiderPickupDetailResponse`.

---

### GET `/api/v1/rider/pickups/{id}`

Get a specific pickup by ID.

**Response `200 OK`:** Envelope wrapping `RiderPickupDetailResponse`.

---

### POST `/api/v1/rider/pickups/{id}/collect`

Mark a pickup as collected. Accepts weight and optional proof photo.

**Content-Type:** `multipart/form-data`

**Form params:**
| Param | Type | Required | Notes |
|---|---|---|---|
| `weightKg` | decimal | yes | Weight of waste collected |
| `photo` | file | no | Photo of collected waste |
| `notes` | string | no | Collection notes |

**Example curl:**

```bash
curl -X POST /api/v1/rider/pickups/{id}/collect \
  -H "Authorization: Bearer <token>" \
  -F "weightKg=12.5" \
  -F "photo=@waste.jpg" \
  -F "notes=Two bags"
```

**Response `200 OK`:** Envelope wrapping updated `RiderPickupDetailResponse` with `status: "COLLECTED"`.

---

## 12. Dashboard — Pickups (Staff)

Requires role: **COOP_MANAGER** or **ADMIN**

### GET `/api/v1/dashboard/pickups`

Search pickups with optional filters, paginated.

**Query params:**
| Param | Type | Notes |
|---|---|---|
| `status` | enum | `REQUESTED`, `ASSIGNED`, `COLLECTED`, `PAID`, `CANCELLED`, `FAILED` |
| `riderId` | UUID | Filter by assigned rider |
| `farmerId` | UUID | Filter by farmer |
| `fromDate` | date | ISO 8601 `YYYY-MM-DD` |
| `toDate` | date | ISO 8601 `YYYY-MM-DD` |
| `page` | int | Default 0 |
| `size` | int | Default 20 |
| `sort` | string | Default `scheduledFor` |

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "content": [ /* array of WastePickupResponse */ ],
    "totalElements": 42,
    "totalPages": 3,
    "size": 20,
    "number": 0
  },
  "message": null
}
```

---

### POST `/api/v1/dashboard/pickups/{id}/assign`

Assign a rider to a pickup.

**Query param:** `riderId` — UUID of the rider to assign.

**Response `200 OK`:** Envelope wrapping updated `WastePickupResponse` with `status: "ASSIGNED"`.

---

### POST `/api/v1/dashboard/pickups/{id}/cancel`

Cancel a pickup.

**Response `200 OK`:** Envelope wrapping updated `WastePickupResponse` with `status: "CANCELLED"`.

---

## 13. Dashboard — Inventory: Intake (Staff)

Requires role: **COOP_MANAGER** or **ADMIN**

### POST `/api/v1/dashboard/inventory/intake`

Record a waste intake batch (collection of multiple pickups into the warehouse).

**Request body:**

```json
{
  "intakeDate": "2025-05-19",
  "totalKg": 145.50,
  "pickupIds": [
    "f1a2b3c4-...",
    "e2d3c4b5-..."
  ],
  "notes": "Morning batch, all from Mwea ward"
}
```

| Field        | Type          | Required |
|--------------|---------------|----------|
| `intakeDate` | date          | yes      |
| `totalKg`    | decimal       | yes, > 0 |
| `pickupIds`  | array of UUID | yes      |
| `notes`      | string        | no       |

**Response `201 Created`:**

```json
{
  "success": true,
  "data": {
    "id": "a1b2c3d4-...",
    "intakeDate": "2025-05-19",
    "totalKg": 145.50,
    "pickupIds": [
      "f1a2b3c4-...",
      "e2d3c4b5-..."
    ],
    "notes": "Morning batch",
    "recordedBy": "staff-uuid-...",
    "createdAt": "2025-05-19T08:00:00Z"
  },
  "message": null
}
```

---

### GET `/api/v1/dashboard/inventory/intake`

List all recorded intake batches.

**Response `200 OK`:** Envelope wrapping array of `WasteIntakeBatch`.

---

## 14. Dashboard — Inventory: Batches & Stock (Staff)

Requires role: **COOP_MANAGER** or **ADMIN**

### POST `/api/v1/dashboard/inventory/batches`

Record a new briquette production batch.

**Request body:**

```json
{
  "batchNumber": "BRQ-2025-05-001",
  "kgProduced": 120.00,
  "producedAt": "2025-05-19T06:00:00Z",
  "sourceIntakeId": "a1b2c3d4-..."
}
```

| Field            | Type     | Required       |
|------------------|----------|----------------|
| `batchNumber`    | string   | yes            |
| `kgProduced`     | decimal  | yes, > 0       |
| `producedAt`     | datetime | yes (ISO 8601) |
| `sourceIntakeId` | UUID     | no             |

**Response `201 Created`:**

```json
{
  "success": true,
  "data": {
    "id": "b2c3d4e5-...",
    "batchNumber": "BRQ-2025-05-001",
    "kgProduced": 120.00,
    "kgRemaining": 120.00,
    "producedAt": "2025-05-19T06:00:00Z",
    "sourceIntakeId": "a1b2c3d4-...",
    "createdAt": "2025-05-19T06:05:00Z"
  },
  "message": null
}
```

---

### GET `/api/v1/dashboard/inventory/batches`

List all production batches.

**Response `200 OK`:** Envelope wrapping array of `BriquetteBatch`.

---

### GET `/api/v1/dashboard/inventory/stock`

Get total available briquette stock in kg.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": 240.50,
  "message": null
}
```

---

## 15. Dashboard — Orders (Staff)

Requires role: **COOP_MANAGER** or **ADMIN**

### GET `/api/v1/dashboard/orders`

List all orders, optionally filtered by status.

**Query params:**
| Param | Type | Notes |
|---|---|---|
| `status` | enum | `PENDING_PAYMENT`, `PAID`, `READY_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`, `REFUNDED` |
| `page` | int | Default 0 |
| `size` | int | Default 20 |

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "content": [ /* array of OrderResponse */ ],
    "totalElements": 15,
    "totalPages": 1,
    "size": 20,
    "number": 0
  },
  "message": null
}
```

---

### POST `/api/v1/dashboard/orders/{id}/ready`

Mark an order as ready for delivery.

**Response `200 OK`:** Envelope wrapping updated `OrderResponse` with `status: "READY_FOR_DELIVERY"`.

---

### POST `/api/v1/dashboard/orders/{id}/deliver`

Mark an order as delivered.

**Response `200 OK`:** Envelope wrapping updated `OrderResponse` with `status: "DELIVERED"`.

---

## 16. Dashboard — Certificates (Staff)

Requires role: **COOP_MANAGER** or **ADMIN**

### GET `/api/v1/dashboard/certificates/{id}`

Get a full certificate record by its UUID.

**Response `200 OK`:**

```json
{
  "success": true,
  "data": {
    "id": "c3d4e5f6-...",
    "applicationId": "d1e2f3a4-...",
    "token": "ZAO-A1B2C3D4",
    "qrImageUrl": "https://cdn.zaocycle.app/certs/qr-uuid.png",
    "status": "ACTIVE",
    "issuedAt": "2025-05-15T08:00:00Z",
    "expiresAt": "2025-05-29T08:00:00Z",
    "verifiedCount": 3
  },
  "message": null
}
```

---

### POST `/api/v1/dashboard/certificates/{id}/revoke`

Revoke an active certificate.

**Response `200 OK`:** Envelope wrapping updated `CertificateResponse` with `status: "REVOKED"`.

---

## 17. Admin — Staff Management

Requires role: **ADMIN** (unless noted)

### POST `/api/v1/admin/staff`

Create a new staff user.

**Request body:**

```json
{
  "email": "manager@zaocycle.co.ke",
  "password": "securepass",
  "fullName": "Mary Njeri",
  "role": "COOP_MANAGER"
}
```

| Field      | Type   | Required | Notes                     |
|------------|--------|----------|---------------------------|
| `email`    | string | yes      | Valid email               |
| `password` | string | yes      | Min 6 chars               |
| `fullName` | string | yes      |                           |
| `role`     | enum   | yes      | `COOP_MANAGER` or `ADMIN` |

**Response `201 Created`:**

```json
{
  "success": true,
  "data": {
    "id": "e4f5a6b7-...",
    "email": "manager@zaocycle.co.ke",
    "fullName": "Mary Njeri",
    "role": "COOP_MANAGER",
    "active": true,
    "profileImageUrl": null,
    "createdAt": "2025-05-19T09:00:00Z"
  },
  "message": null
}
```

---

### GET `/api/v1/admin/staff`

List all staff users.

**Response `200 OK`:** Envelope wrapping array of `StaffResponse`.

---

### GET `/api/v1/admin/staff/{id}`

Get a single staff user. Accessible by **ADMIN** or **COOP_MANAGER**.

**Response `200 OK`:** Envelope wrapping `StaffResponse`.

---

### DELETE `/api/v1/admin/staff/{id}`

Deactivate a staff user (soft delete — `active: false`).

**Response `200 OK`:** Envelope wrapping updated `StaffResponse` with `active: false`.

---

### POST `/api/v1/admin/staff/{id}/activate`

Reactivate a previously deactivated staff user.

**Response `200 OK`:** Envelope wrapping updated `StaffResponse` with `active: true`.

---

## 18. Admin — Rider Management

Requires role: **ADMIN** (unless noted)

### POST `/api/v1/admin/riders`

Register a new rider.

**Request body:**

```json
{
  "phone": "+254722000001",
  "fullName": "Peter Mwangi",
  "ward": "MWEA",
  "password": "riderpass"
}
```

| Field      | Type   | Required | Notes                                                   |
|------------|--------|----------|---------------------------------------------------------|
| `phone`    | string | yes      | E.164 format                                            |
| `fullName` | string | yes      |                                                         |
| `ward`     | string | yes      | One of: `MWEA`, `GICHUGU`, `KIRINYAGA_CENTRAL`, `NDIA` |
| `password` | string | yes      | Min 6 chars                                             |

**Response `201 Created`:**

```json
{
  "success": true,
  "data": {
    "id": "f5a6b7c8-...",
    "phone": "+254722000001",
    "fullName": "Peter Mwangi",
    "ward": "MWEA",
    "active": true
  },
  "message": null
}
```

---

### GET `/api/v1/admin/riders/{id}`

Get a rider by ID. Accessible by **ADMIN** or **COOP_MANAGER**.

**Response `200 OK`:** Envelope wrapping `RiderResponse`.  
**Response `404 Not Found`**

---

### DELETE `/api/v1/admin/riders/{id}`

Deactivate a rider.

**Response `200 OK`:** Envelope wrapping updated `RiderResponse` with `active: false`.

---

## 19. Admin — Scheduler

Requires role: **ADMIN**. Dev/testing utility.

### POST `/api/v1/admin/scheduler/fast-forward/{applicationId}`

Simulate harvest maturity for a pesticide application (fire the maturity event after N seconds).

**Path param:** `applicationId` — UUID of the pesticide application.  
**Query param:** `seconds` — default `30`. Seconds from now to trigger the maturity event.

**Response `200 OK`:** Empty body.

---

## 20. Webhooks & Callbacks

These endpoints are called by third-party services (M-Pesa Daraja, Africa's Talking). **No authentication required.**
Responses from these endpoints use the format expected by the external service — **not** the standard `ApiResponse` envelope.

### POST `/api/v1/payments/mpesa/b2c/result`

M-Pesa B2C result callback. Called by Safaricom when a farmer payout completes.

**Response `200 OK`:**

```json
{
  "ResponseCode": "0",
  "ResponseDesc": "Accepted"
}
```

---

### POST `/api/v1/payments/mpesa/b2c/timeout`

M-Pesa B2C timeout callback.

**Response `200 OK`:**

```json
{
  "ResponseCode": "0",
  "ResponseDesc": "Accepted"
}
```

---

### POST `/api/v1/payments/mpesa/stkpush/callback`

M-Pesa STK Push callback. Called by Safaricom after a buyer completes/fails payment.

**Response `200 OK`:**

```json
{
  "ResultCode": 0,
  "ResultDesc": "Accepted"
}
```

---

### POST `/api/v1/sms/inbound`

Africa's Talking inbound SMS webhook. Handles farmer SMS commands.

**Content-Type:** `application/x-www-form-urlencoded`

**Form params:** `from`, `text`

**Response `200 OK`:** Empty.

---

## 21. USSD Gateway (Telecom)

Called by Africa's Talking USSD platform. Not intended for web frontend.
Responses use plain text — **not** the standard `ApiResponse` envelope.

### POST `/api/v1/ussd/callback`

**Content-Type:** `application/x-www-form-urlencoded`  
**Produces:** `text/plain`

Handles the interactive USSD session for farmer registration and PIN operations.

---

## 22. Error Responses

All errors return a consistent JSON body (not wrapped in `ApiResponse`):

```json
{
  "message": "Human-readable error description"
}
```

| HTTP Status                 | Trigger                                             |
|-----------------------------|-----------------------------------------------------|
| `400 Bad Request`           | Missing/invalid request fields (validation failure) |
| `401 Unauthorized`          | Bad credentials, expired/invalid JWT                |
| `403 Forbidden`             | Authenticated but insufficient role                 |
| `404 Not Found`             | Resource not found                                  |
| `422 Unprocessable Entity`  | Business rule violation (domain exception)          |
| `500 Internal Server Error` | Unexpected server error                             |

**Validation error example:**

```json
{
  "message": "email: must be a well-formed email address, password: size must be between 6 and 2147483647"
}
```

---

## 23. Enumerations Reference

### Role

| Value          | Description                          |
|----------------|--------------------------------------|
| `FARMER`       | Registered farmer using USSD         |
| `RIDER`        | Collection rider                     |
| `COOP_MANAGER` | Cooperative staff — dashboard access |
| `ADMIN`        | Full system access                   |
| `BUYER`        | Briquette buyer — web/app            |

### BuyerType

| Value         | Description                |
|---------------|----------------------------|
| `SCHOOL`      | School institution         |
| `INDIVIDUAL`  | Private individual         |
| `INSTITUTION` | Government/NGO institution |
| `BUSINESS`    | Commercial business        |

### Ward

| Value               | Display Name      |
|---------------------|-------------------|
| `MWEA`              | Mwea              |
| `GICHUGU`           | Gichugu           |
| `KIRINYAGA_CENTRAL` | Kirinyaga Central |
| `NDIA`              | Ndia              |

### PickupStatus

| Value       | Meaning                          |
|-------------|----------------------------------|
| `REQUESTED` | Farmer requested pickup via USSD |
| `ASSIGNED`  | Rider assigned by coop manager   |
| `COLLECTED` | Rider collected the waste        |
| `PAID`      | Farmer payout sent via M-Pesa    |
| `CANCELLED` | Pickup cancelled                 |
| `FAILED`    | Pickup or payment failed         |

### ApplicationStatus (Pesticide)

| Value         | Meaning                                             |
|---------------|-----------------------------------------------------|
| `PENDING`     | Application recorded, within waiting period         |
| `SAFE`        | Safe harvest date reached — certificate auto-issued |
| `EXPIRED`     | Certificate validity window has passed              |
| `INVALIDATED` | Manually invalidated                                |

### CertificateStatus

| Value     | Meaning                         |
|-----------|---------------------------------|
| `ACTIVE`  | Valid and verifiable            |
| `EXPIRED` | Past the 14-day validity window |
| `REVOKED` | Manually revoked by staff       |

### OrderStatus

| Value                | Meaning                             |
|----------------------|-------------------------------------|
| `PENDING_PAYMENT`    | Awaiting M-Pesa STK push completion |
| `PAID`               | Payment confirmed                   |
| `READY_FOR_DELIVERY` | Staff packed and ready              |
| `DELIVERED`          | Order delivered                     |
| `CANCELLED`          | Cancelled by buyer or staff         |
| `REFUNDED`           | Refund issued                       |

### ChemicalCategory

`FUNGICIDE` | `INSECTICIDE` | `HERBICIDE` | `OTHER`
