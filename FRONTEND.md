# ZaoCycle — Frontend Developer Guide

This document guides the full development lifecycle for the ZaoCycle web frontend. It covers architecture, page
structure, component design, API integration patterns, state management, and UX conventions.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technology Stack](#2-technology-stack)
3. [User Roles & Portals](#3-user-roles--portals)
4. [Application Architecture](#4-application-architecture)
5. [Routing Map](#5-routing-map)
6. [Authentication Flow](#6-authentication-flow)
7. [Page-by-Page Breakdown](#7-page-by-page-breakdown)
    - [Public Pages](#71-public-pages)
    - [Buyer Portal](#72-buyer-portal)
    - [Staff Dashboard](#73-staff-dashboard)
    - [Admin Panel](#74-admin-panel)
8. [API Integration Patterns](#8-api-integration-patterns)
9. [State Management](#9-state-management)
10. [Component Library & Design System](#10-component-library--design-system)
11. [Form Patterns & Validation](#11-form-patterns--validation)
12. [Error Handling & Feedback](#12-error-handling--feedback)
13. [Pagination & Infinite Scroll](#13-pagination--infinite-scroll)
14. [File Uploads](#14-file-uploads)
15. [QR Code Verification Page](#15-qr-code-verification-page)
16. [Security Considerations](#16-security-considerations)
17. [Environment Configuration](#17-environment-configuration)
18. [Development Workflow](#18-development-workflow)

---

## 1. Project Overview

ZaoCycle is an agricultural waste management platform operating in Kirinyaga County, Kenya. It connects:

- **Farmers** — register via USSD, track pesticide applications and waste pickups, receive M-Pesa payouts.
- **Riders** — mobile collection agents who collect waste from farmers.
- **Buyers** — schools, institutions, and businesses that purchase briquettes made from the collected waste.
- **Cooperative Staff** — manage the full operational loop via a web dashboard.

The web frontend serves **buyers** and **cooperative staff/admin**. Farmers and riders primarily use USSD/SMS, but the
frontend can display their data as read-only context.

---

## 2. Technology Stack

| Layer         | Choice                                           | Notes                                                              |
|---------------|--------------------------------------------------|--------------------------------------------------------------------|
| Framework     | **Next.js 14+ (App Router)** with **TypeScript** | File-based routing, Server Components, built-in image optimisation |
| Routing       | **Next.js App Router**                           | Built-in — no React Router needed                                  |
| Auth guard    | **Next.js Middleware** (`middleware.ts`)         | JWT check at the edge before page render                           |
| Data fetching | **TanStack Query (React Query) v5**              | Client-side cache, background refetch in Client Components         |
| HTTP client   | **Axios**                                        | Interceptors for auth tokens (Client Components only)              |
| State         | **Zustand**                                      | Auth state in Client Components; server state via TanStack Query   |
| Forms         | **React Hook Form + Zod**                        | Schema validation aligned with backend constraints                 |
| UI components | **shadcn/ui + Tailwind CSS**                     | Accessible, composable, unstyled base                              |
| Icons         | **Lucide React**                                 | Consistent icon set                                                |
| Date handling | **date-fns**                                     | ISO date formatting                                                |
| Images        | **next/image**                                   | Automatic optimisation and lazy loading                            |
| QR rendering  | **qrcode.react**                                 | For displaying generated QR codes                                  |
| Notifications | **Sonner**                                       | Toast notifications                                                |

---

## 3. User Roles & Portals

| Role (backend) | Frontend portal                      | Login method                |
|----------------|--------------------------------------|-----------------------------|
| `BUYER`        | `/` (public marketing) + `/portal/*` | Email + password            |
| `COOP_MANAGER` | `/dashboard/*`                       | Email + password            |
| `ADMIN`        | `/dashboard/*` + `/admin/*`          | Email + password            |
| `FARMER`       | Read-only context within dashboard   | Not a web login — USSD only |
| `RIDER`        | Read-only context within dashboard   | Not a web login             |

The frontend has three distinct authenticated areas:

1. **Buyer Portal** — self-service ordering, order history, profile.
2. **Staff Dashboard** — operations: pickups, orders, inventory, certificates.
3. **Admin Panel** — user management (staff + riders), scheduler tools.

---

## 4. Application Architecture

Next.js App Router uses the `app/` directory. Route groups `(group)` create shared layouts without adding URL segments.

```
zaocycle-web/
├── app/                            # Next.js App Router root
│   ├── layout.tsx                  # Root layout (fonts, providers)
│   ├── page.tsx                    # Landing page /
│   ├── products/
│   │   └── page.tsx                # /products  (Server Component)
│   ├── verify/
│   │   └── [token]/
│   │       └── page.tsx            # /verify/:token  (Server Component)
│   ├── login/
│   │   └── page.tsx                # /login
│   ├── register/
│   │   └── page.tsx                # /register
│   │
│   ├── (portal)/                   # Route group — Buyer portal layout
│   │   ├── layout.tsx              # Portal shell (sidebar, top bar)
│   │   └── portal/
│   │       ├── home/page.tsx
│   │       ├── products/page.tsx
│   │       ├── orders/
│   │       │   ├── page.tsx
│   │       │   └── [id]/page.tsx
│   │       └── profile/page.tsx
│   │
│   ├── (dashboard)/                # Route group — Staff dashboard layout
│   │   ├── layout.tsx
│   │   └── dashboard/
│   │       ├── overview/page.tsx
│   │       ├── pickups/
│   │       │   ├── page.tsx
│   │       │   └── [id]/page.tsx
│   │       ├── orders/
│   │       │   ├── page.tsx
│   │       │   └── [id]/page.tsx
│   │       ├── inventory/page.tsx
│   │       └── certificates/
│   │           ├── page.tsx
│   │           └── [id]/page.tsx
│   │
│   └── (admin)/                    # Route group — Admin panel layout
│       ├── layout.tsx
│       └── admin/
│           ├── staff/
│           │   ├── page.tsx
│           │   ├── new/page.tsx
│           │   └── [id]/page.tsx
│           └── riders/
│               ├── page.tsx
│               ├── new/page.tsx
│               └── [id]/page.tsx
│
├── middleware.ts                   # Edge auth guard (JWT role check)
│
├── lib/
│   ├── api/                        # API client layer (Axios, used in Client Components)
│   │   ├── client.ts               # Axios instance + interceptors
│   │   ├── auth.ts
│   │   ├── buyer.ts
│   │   ├── products.ts
│   │   ├── orders.ts
│   │   ├── pickups.ts
│   │   ├── inventory.ts
│   │   ├── certificates.ts
│   │   ├── chemicals.ts
│   │   ├── staff.ts
│   │   └── riders.ts
│   ├── formatters.ts               # KES, kg, date formatters
│   └── validators.ts               # Shared Zod schemas
│
├── hooks/                          # Client Component hooks (TanStack Query)
│   ├── useAuth.ts
│   ├── useProducts.ts
│   ├── useOrders.ts
│   └── ...
│
├── store/
│   └── authStore.ts                # Zustand — tokens, user, login/logout
│
└── components/
    ├── ui/                         # shadcn/ui base components
    ├── layout/                     # AppShell, Sidebar, Navbar, Footer
    ├── auth/                       # LoginForm, RoleGuard
    ├── buyer/                      # OrderCard, ProductCard, BuyerProfileForm
    ├── dashboard/                  # PickupTable, PickupFilters, OrderStatusBadge
    ├── admin/                      # StaffForm, RiderForm
    └── shared/                     # PageHeader, EmptyState, LoadingSkeleton
```

### Server vs Client Components

| Use a **Server Component** when                 | Use a **Client Component** (`'use client'`) when |
|-------------------------------------------------|--------------------------------------------------|
| Fetching public data (products, verify page)    | Reading from Zustand (auth state)                |
| Page-level data that doesn't need interactivity | Forms, modals, event handlers                    |
| Reducing JS bundle size                         | TanStack Query hooks                             |
| Reading from cookies on the server              | Pagination controls, filters                     |

Public pages (`/products`, `/verify/[token]`) can be Server Components that `fetch` directly from the backend, skipping
the Axios client entirely.

---

## 5. Routing Map

Next.js App Router file → URL mapping:

```
app/page.tsx                              → /
app/products/page.tsx                     → /products
app/verify/[token]/page.tsx               → /verify/:token
app/login/page.tsx                        → /login
app/register/page.tsx                     → /register

app/(portal)/portal/home/page.tsx         → /portal/home
app/(portal)/portal/products/page.tsx     → /portal/products
app/(portal)/portal/orders/page.tsx       → /portal/orders
app/(portal)/portal/orders/[id]/page.tsx  → /portal/orders/:id
app/(portal)/portal/profile/page.tsx      → /portal/profile

app/(dashboard)/dashboard/overview/page.tsx          → /dashboard/overview
app/(dashboard)/dashboard/pickups/page.tsx            → /dashboard/pickups
app/(dashboard)/dashboard/pickups/[id]/page.tsx       → /dashboard/pickups/:id
app/(dashboard)/dashboard/orders/page.tsx             → /dashboard/orders
app/(dashboard)/dashboard/orders/[id]/page.tsx        → /dashboard/orders/:id
app/(dashboard)/dashboard/inventory/page.tsx          → /dashboard/inventory
app/(dashboard)/dashboard/certificates/page.tsx       → /dashboard/certificates
app/(dashboard)/dashboard/certificates/[id]/page.tsx  → /dashboard/certificates/:id

app/(admin)/admin/staff/page.tsx          → /admin/staff
app/(admin)/admin/staff/new/page.tsx      → /admin/staff/new
app/(admin)/admin/staff/[id]/page.tsx     → /admin/staff/:id
app/(admin)/admin/riders/page.tsx         → /admin/riders
app/(admin)/admin/riders/new/page.tsx     → /admin/riders/new
app/(admin)/admin/riders/[id]/page.tsx    → /admin/riders/:id
```

Route group layouts (`(portal)`, `(dashboard)`, `(admin)`) each have a `layout.tsx` that renders the appropriate
sidebar/shell for that section.

---

## 6. Authentication Flow

### Token Storage

| Token                         | Where to store                                                                                                     |
|-------------------------------|--------------------------------------------------------------------------------------------------------------------|
| Access token (15 min)         | **Zustand in-memory only** — never persisted                                                                       |
| Refresh token (30 days)       | **`localStorage`** via Zustand `persist` (acceptable for SPAs; use httpOnly cookie if backend adds cookie support) |
| User summary (role, id, name) | **`localStorage`** via Zustand `persist`                                                                           |

### Zustand Auth Store

```ts
// store/authStore.ts
'use client';
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: { id: string; role: string; displayName: string } | null;
  login: (tokenResponse: TokenResponse) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      login: (t) => set({ accessToken: t.accessToken, refreshToken: t.refreshToken, user: t.user }),
      logout: () => set({ accessToken: null, refreshToken: null, user: null }),
    }),
    {
      name: 'zao-auth',
      // Only persist refresh token and user; access token is always in-memory
      partialize: (s) => ({ refreshToken: s.refreshToken, user: s.user }),
    }
  )
);
```

### Axios Interceptors

The Axios client is only used inside Client Components and hooks. Do not import it in Server Components.

```ts
// lib/api/client.ts
import axios from 'axios';
import { useAuthStore } from '@/store/authStore';

export const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

apiClient.interceptors.response.use(
  (res) => res,
  async (error) => {
    if (error.response?.status === 401 && !error.config._retry) {
      error.config._retry = true;
      const { refreshToken, login, logout } = useAuthStore.getState();
      if (!refreshToken) { logout(); return Promise.reject(error); }
      try {
        const { data } = await axios.post(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/auth/refresh`,
          { refreshToken }
        );
        login(data);
        error.config.headers.Authorization = `Bearer ${data.accessToken}`;
        return apiClient(error.config);
      } catch {
        logout();
        window.location.href = '/login';
      }
    }
    const message = error.response?.data?.message ?? 'An unexpected error occurred';
    return Promise.reject(new Error(message));
  }
);
```

### Next.js Middleware — Route Protection

`middleware.ts` at the project root runs at the Edge before any page renders. It reads the stored user from a cookie (or
redirects to login if missing). The simplest approach is to store a non-sensitive `role` cookie alongside the token:

```ts
// middleware.ts
import { NextRequest, NextResponse } from 'next/server';

const PORTAL_ROLES   = ['BUYER'];
const DASHBOARD_ROLES = ['COOP_MANAGER', 'ADMIN'];
const ADMIN_ROLES    = ['ADMIN'];

export function middleware(req: NextRequest) {
  const role = req.cookies.get('zao-role')?.value;
  const { pathname } = req.nextUrl;

  const loginUrl = req.nextUrl.clone();
  loginUrl.pathname = '/login';

  if (pathname.startsWith('/portal') && !PORTAL_ROLES.includes(role ?? ''))
    return NextResponse.redirect(loginUrl);

  if (pathname.startsWith('/dashboard') && !DASHBOARD_ROLES.includes(role ?? ''))
    return NextResponse.redirect(loginUrl);

  if (pathname.startsWith('/admin') && !ADMIN_ROLES.includes(role ?? ''))
    return NextResponse.redirect(loginUrl);

  return NextResponse.next();
}

export const config = {
  matcher: ['/portal/:path*', '/dashboard/:path*', '/admin/:path*'],
};
```

On login success, write the role cookie alongside storing tokens in Zustand:

```ts
document.cookie = `zao-role=${tokens.user.role}; path=/; SameSite=Strict`;
```

On logout, clear it:

```ts
document.cookie = 'zao-role=; Max-Age=0; path=/';
```

### Role-Based Redirects After Login

```ts
// In the login page Client Component
const ROLE_REDIRECT: Record<string, string> = {
  BUYER:        '/portal/home',
  COOP_MANAGER: '/dashboard/overview',
  ADMIN:        '/dashboard/overview',
};

const router = useRouter();
// after login API call:
router.push(ROLE_REDIRECT[tokens.user.role] ?? '/');
```

### App Initialisation — Restore Access Token

On first render the access token is gone (in-memory only). Silently refresh it using the persisted refresh token:

```tsx
// components/auth/AuthHydrator.tsx
'use client';
import { useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { authApi } from '@/lib/api/auth';

export function AuthHydrator({ children }: { children: React.ReactNode }) {
  const { refreshToken, user, login, logout } = useAuthStore();

  useEffect(() => {
    if (refreshToken && !useAuthStore.getState().accessToken) {
      authApi.refresh(refreshToken).then(login).catch(logout);
    }
  }, []);

  return <>{children}</>;
}
```

Mount `<AuthHydrator>` inside the root `app/layout.tsx`.

---

## 7. Page-by-Page Breakdown

### 7.1 Public Pages

#### Landing Page `/`

**Goal:** Convert visitors into buyers or direct staff to login.

- Hero section with ZaoCycle value proposition (waste-to-briquettes story)
- "How it works" steps: Farmer → Rider → Coop → Buyer
- Product preview section — call `GET /api/v1/products`, display top 3 products
- "Verify your certificate" search box linking to `/verify/:token`
- CTA buttons: "Buy Briquettes" → `/register`, "Staff Login" → `/login`

#### Products Page `/products`

- Grid of product cards using `GET /api/v1/products`
- Each card: image, name, weight, price (KES), "Order Now" CTA
- "Order Now" redirects to `/login` if unauthenticated, or `/portal/products` if BUYER

#### Certificate Verification Page `/verify/[token]`

See [Section 15](#15-qr-code-verification-page) for full specification.

This page is a **Server Component** — fetch the certificate data server-side so the result is immediately rendered (fast
for mobile QR scans, good for SEO).

#### Login Page `/login`

- Mark the form component `'use client'` — it uses state and event handlers
- Single form for all web users (buyers, staff, admin)
- Email + password with a role toggle: "I am a buyer / I am staff"
- Buyers → `POST /api/v1/auth/buyer/login`, staff/admin → `POST /api/v1/auth/staff/login`
- On success: set role cookie, update Zustand, call `router.push(ROLE_REDIRECT[role])`
- Use `useRouter` from `next/navigation` (not `react-router-dom`)

#### Buyer Registration Page `/register`

- `'use client'` component
- `POST /api/v1/auth/buyer/register`
- Fields: email, password, phone, buyer type (dropdown), display name, contact person (optional), address (optional),
  ward (dropdown)
- On success: set role cookie, update Zustand, `router.push('/portal/home')`

---

### 7.2 Buyer Portal

All pages under `/portal` require role `BUYER`.

#### Buyer Home `/portal/home`

- Welcome banner with `user.displayName`
- Quick stats: total orders, pending orders
- "Browse Products" CTA
- Recent orders list (last 3) with status badges

#### Browse Products `/portal/products`

- Product grid from `GET /api/v1/products`
- Each card has quantity selector and "Add to Cart" or "Order Now" button
- **Simple ordering:** ZaoCycle uses single-item orders. "Order Now" opens an order form modal with quantity, delivery
  address, delivery phone, requested delivery date, and notes.
- Call `POST /api/v1/buyer/orders` on submit

#### Order History `/portal/orders`

- Paginated list from `GET /api/v1/buyer/orders`
- Filter by status (client-side or `?status=` if added)
- Each row: order date, product name, quantity, total amount (KES), status badge
- Click → `/portal/orders/:id`

#### Order Detail `/portal/orders/:id`

- Full order info from `GET /api/v1/buyer/orders/:id`
- Show M-Pesa payment status prominently
- "Cancel Order" button (visible only when `status === "PENDING_PAYMENT"`)
- Calls `DELETE /api/v1/buyer/orders/:id`

#### Buyer Profile `/portal/profile`

- Display current profile from `GET /api/v1/buyer/me`
- Edit form using `PATCH /api/v1/buyer/me`
- Profile image upload: `POST /api/v1/profile/image`
- Show current image or avatar placeholder

---

### 7.3 Staff Dashboard

All pages under `/dashboard` require role `COOP_MANAGER` or `ADMIN`.

#### Dashboard Overview `/dashboard/overview`

- Summary cards:
    - Pickups today (count by status)
    - Pending orders
    - Available stock (kg) from `GET /api/v1/dashboard/inventory/stock`
    - Active certificates count
- Recent pickup activity table

#### Pickup Management `/dashboard/pickups`

- Filterable, paginated table: `GET /api/v1/dashboard/pickups`
- Filters: status, rider, date range
- Status filter chips at the top: All | Requested | Assigned | Collected | Paid | Cancelled
- Actions per row:
    - **Assign Rider** (when `REQUESTED`): dropdown of active riders → `POST /dashboard/pickups/:id/assign?riderId=`
    - **Cancel** (when `REQUESTED` or `ASSIGNED`): `POST /dashboard/pickups/:id/cancel`
- Clicking a row → pickup detail

#### Pickup Detail `/dashboard/pickups/:id`

- Full pickup info
- Show photo if collected
- Weight, payout amount, timestamps

#### Order Management `/dashboard/orders`

- Paginated order table: `GET /api/v1/dashboard/orders`
- Filter by status
- Actions:
    - **Mark Ready** (when `PAID`): `POST /dashboard/orders/:id/ready`
    - **Mark Delivered** (when `READY_FOR_DELIVERY`): `POST /dashboard/orders/:id/deliver`

#### Inventory `/dashboard/inventory`

Tabs: **Intake** | **Batches** | **Stock**

**Intake tab:**

- List of intake batches from `GET /api/v1/dashboard/inventory/intake`
- "Record Intake" button opens form modal → `POST /api/v1/dashboard/inventory/intake`
- Form: intake date, total kg, pickup IDs (multi-select from recent pickups), notes

**Batches tab:**

- List from `GET /api/v1/dashboard/inventory/batches`
- "Create Batch" button → `POST /api/v1/dashboard/inventory/batches`
- Show kgProduced vs kgRemaining as a progress bar

**Stock tab:**

- Large numeric display: available stock from `GET /api/v1/dashboard/inventory/stock`
- Stock level trend (if historical data becomes available)

#### Certificates `/dashboard/certificates`

- Table: certificate list with status, issued date, expiry, verify count
- Data source: query certificates associated with recent applications
- Revoke button → `POST /api/v1/dashboard/certificates/:id/revoke`
- View QR code button: display `qrImageUrl` in a modal

#### Certificate Detail `/dashboard/certificates/:id`

- `GET /api/v1/dashboard/certificates/:id`
- Show QR code image
- Status badge: ACTIVE (green), EXPIRED (amber), REVOKED (red)
- Linked pesticide application details

---

### 7.4 Admin Panel

All pages under `/admin` require role `ADMIN`.

#### Staff List `/admin/staff`

- Table from `GET /api/v1/admin/staff`
- Columns: name, email, role, status, joined date
- "Create Staff" button → `/admin/staff/new`
- Actions: Deactivate (`DELETE /admin/staff/:id`) / Reactivate (`POST /admin/staff/:id/activate`)

#### Create Staff `/admin/staff/new`

- Form: email, password, full name, role (COOP_MANAGER or ADMIN)
- `POST /api/v1/admin/staff`
- On success: redirect to `/admin/staff`

#### Rider List `/admin/riders`

- Table from `GET /api/v1/admin/riders` (use individual gets if no list endpoint yet)
- Columns: name, phone, ward, status
- "Register Rider" button → `/admin/riders/new`
- Deactivate: `DELETE /api/v1/admin/riders/:id`

#### Register Rider `/admin/riders/new`

- Form: phone, full name, ward (dropdown: Mwea, Gichugu, Kirinyaga Central, Ndia), password
- `POST /api/v1/admin/riders`

---

## 8. API Integration Patterns

### Server Component Fetching (public data)

For public pages that don't need auth, use `fetch` directly in a Server Component — no Axios, no client bundle overhead:

```ts
// app/products/page.tsx  (Server Component — no 'use client')
export default async function ProductsPage() {
  const res = await fetch(`${process.env.API_BASE_URL}/products`, {
    next: { revalidate: 60 }, // ISR: revalidate every 60 s
  });
  const products: ProductResponse[] = await res.json();
  return <ProductGrid products={products} />;
}
```

Note: `API_BASE_URL` (no `NEXT_PUBLIC_` prefix) is server-only. Use it in Server Components and Route Handlers. Use
`NEXT_PUBLIC_API_BASE_URL` in Client Components.

### API Module Pattern (Client Components)

Each domain has its own file in `lib/api/`:

```ts
// lib/api/orders.ts
import { apiClient } from './client';
import type { OrderResponse, PlaceOrderRequest } from '@/types';

export const ordersApi = {
  placeOrder: (data: PlaceOrderRequest) =>
    apiClient.post<OrderResponse>('/buyer/orders', data).then(r => r.data),

  listMyOrders: (params: { page?: number; size?: number }) =>
    apiClient.get<Page<OrderResponse>>('/buyer/orders', { params }).then(r => r.data),

  getOrder: (id: string) =>
    apiClient.get<OrderResponse>(`/buyer/orders/${id}`).then(r => r.data),

  cancelOrder: (id: string) =>
    apiClient.delete<OrderResponse>(`/buyer/orders/${id}`).then(r => r.data),
};
```

### TanStack Query Hooks

All hooks must be used inside `'use client'` components. TanStack Query does not work in Server Components.

```ts
// hooks/useOrders.ts
'use client';

export function useMyOrders(page = 0) {
    return useQuery({
        queryKey: ['buyer', 'orders', page],
        queryFn: () => ordersApi.listMyOrders({page}),
    });
}

export function usePlaceOrder() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ordersApi.placeOrder,
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['buyer', 'orders']});
            toast.success('Order placed! Check your phone for M-Pesa prompt.');
        },
    });
}
```

### `ApiResponse<T>` Wrapper

Several endpoints (chemicals, farmers, pesticide applications) return responses wrapped in:

```json
{
  "success": true,
  "data": ...,
  "message": null
}
```

Create a helper:

```ts
export const unwrap = <T>(res: ApiResponse<T>): T => res.data;
```

---

## 9. State Management

### Auth State (Zustand)

The Zustand auth store is defined in Section 6. Key points for Next.js:

- Add `'use client'` at the top of any file that imports `useAuthStore`.
- The store uses `zustand/middleware`'s `persist` with `localStorage`, which is only available in the browser. Zustand
  handles this safely — it skips persistence during SSR.
- **Never import the auth store in a Server Component.** Server Components cannot access browser APIs.
- Wrap the root layout with a `QueryClientProvider` and `AuthHydrator` inside a `'use client'` providers component:

```tsx
// components/Providers.tsx
'use client';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useState } from 'react';
import { AuthHydrator } from '@/components/auth/AuthHydrator';
import { Toaster } from 'sonner';

export function Providers({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(() => new QueryClient());
  return (
    <QueryClientProvider client={queryClient}>
      <AuthHydrator>
        {children}
        <Toaster richColors />
      </AuthHydrator>
    </QueryClientProvider>
  );
}
```

```tsx
// app/layout.tsx
import { Providers } from '@/components/Providers';
export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
```

Only persist the refresh token and user summary. Fetch a fresh access token on app init via `AuthHydrator` (Section 6).

### Server State (TanStack Query)

All API data lives in TanStack Query. Do not duplicate server state into Zustand. Use `queryClient.invalidateQueries()`
after mutations.

**Query key conventions:**

```ts
['buyer', 'orders']                    // list
    ['buyer', 'orders', orderId]           // single item
    ['dashboard', 'pickups', filters]      // filtered list
    ['dashboard', 'stock']                 // scalar
    ['admin', 'staff']
    ['admin', 'riders']
```

---

## 10. Component Library & Design System

### Colour Palette

| Token                   | Use                                  |
|-------------------------|--------------------------------------|
| `green-600`             | Primary brand (agricultural, growth) |
| `amber-500`             | Warning, pending status              |
| `red-600`               | Error, cancelled, revoked            |
| `blue-600`              | Info, links                          |
| `gray-50` to `gray-900` | Neutral backgrounds and text         |

### Status Badges

```tsx
const STATUS_COLOURS: Record<string, string> = {
    REQUESTED: 'bg-blue-100 text-blue-800',
    ASSIGNED: 'bg-amber-100 text-amber-800',
    COLLECTED: 'bg-green-100 text-green-800',
    PAID: 'bg-green-700 text-white',
    CANCELLED: 'bg-gray-100 text-gray-600',
    FAILED: 'bg-red-100 text-red-800',
    PENDING_PAYMENT: 'bg-amber-100 text-amber-800',
    READY_FOR_DELIVERY: 'bg-blue-100 text-blue-800',
    DELIVERED: 'bg-green-700 text-white',
    ACTIVE: 'bg-green-100 text-green-800',
    EXPIRED: 'bg-amber-100 text-amber-800',
    REVOKED: 'bg-red-100 text-red-800',
};
```

### Currency Formatting

All monetary values are in **KES (Kenyan Shillings)**:

```ts
export const formatKES = (amount: number) =>
    `KES ${amount.toLocaleString('en-KE', {minimumFractionDigits: 2})}`;
```

### Weight Formatting

```ts
export const formatKg = (kg: number) => `${kg.toFixed(2)} kg`;
```

### Date Formatting

```ts
import {format, parseISO} from 'date-fns';

export const formatDate = (iso: string) => format(parseISO(iso), 'dd MMM yyyy');
export const formatDateTime = (iso: string) => format(parseISO(iso), 'dd MMM yyyy, HH:mm');
```

### Core Layout Components

**AppShell** — wraps authenticated pages with a sidebar and top bar.

**Sidebar nav items by role:**

| BUYER     | COOP_MANAGER / ADMIN |
|-----------|----------------------|
| Home      | Overview             |
| Products  | Pickups              |
| My Orders | Orders               |
| Profile   | Inventory            |
| —         | Certificates         |
| —         | Staff (ADMIN only)   |
| —         | Riders (ADMIN only)  |

---

## 11. Form Patterns & Validation

Use **React Hook Form** with **Zod** schemas that mirror backend validation:

```ts
// lib/validators.ts
import {z} from 'zod';

export const registerBuyerSchema = z.object({
    email: z.string().email(),
    password: z.string().min(6, 'Password must be at least 6 characters'),
    phone: z.string().min(10, 'Enter a valid phone number'),
    buyerType: z.enum(['SCHOOL', 'INDIVIDUAL', 'INSTITUTION', 'BUSINESS']),
    displayName: z.string().min(1, 'Display name is required'),
    contactPerson: z.string().optional(),
    address: z.string().optional(),
    ward: z.string().optional(),
});

export const placeOrderSchema = z.object({
    productId: z.string().uuid(),
    quantity: z.number().int().min(1),
    deliveryAddress: z.string().min(1),
    deliveryPhone: z.string().min(10),
    requestedDelivery: z.string().optional(),
    notes: z.string().optional(),
});
```

**Pattern for forms:**

```tsx
const {register, handleSubmit, formState: {errors}} = useForm<RegisterBuyer>({
    resolver: zodResolver(registerBuyerSchema),
});
```

Always display field-level errors below inputs. The backend returns comma-separated field errors in `message` — parse
and display them if client validation is bypassed.

---

## 12. Error Handling & Feedback

### API Error Handling

```ts
// api/client.ts
axiosInstance.interceptors.response.use(
    res => res,
    error => {
        const message = error.response?.data?.message ?? 'An unexpected error occurred';
        // Let calling code handle toast or inline error
        return Promise.reject(new Error(message));
    }
);
```

### Toast Notifications

Use consistent feedback after mutations:

| Action            | Toast                                                          |
|-------------------|----------------------------------------------------------------|
| Order placed      | `success: "Order placed! Check your phone for M-Pesa prompt."` |
| Order cancelled   | `success: "Order cancelled."`                                  |
| Rider assigned    | `success: "Rider assigned successfully."`                      |
| Pickup cancelled  | `success: "Pickup cancelled."`                                 |
| Staff created     | `success: "Staff account created."`                            |
| Staff deactivated | `warning: "Staff account deactivated."`                        |
| Any 4xx error     | `error: <message from API>`                                    |
| Any 5xx error     | `error: "Server error. Please try again."`                     |

### Empty States

Show a friendly empty-state component when lists are empty. Use `useRouter` from `next/navigation` for navigation:

```tsx
'use client';
import {useRouter} from 'next/navigation';

<EmptyState
    icon={<Package/>}
    title="No orders yet"
    description="Browse products and place your first order."
    action={<Button onClick={() => router.push('/portal/products')}>Browse Products</Button>}
/>
```

---

## 13. Pagination & Infinite Scroll

All paginated endpoints use Spring's `Page<T>` structure:

```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0,
  "number": 0,
  "size": 20
}
```

Use **page-number pagination** for dashboard tables (staff controls) and **load more / infinite scroll** for buyer order
history on mobile.

```tsx
// Pagination controls for dashboard tables
<Pagination
    currentPage={page}
    totalPages={data?.totalPages ?? 0}
    onPageChange={setPage}
/>
```

---

## 14. File Uploads

Profile image upload uses multipart/form-data:

```ts
export const uploadProfileImage = (file: File) => {
  const form = new FormData();
  form.append('file', file);
  return apiClient.post<ProfileImageResponse>('/profile/image', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }).then(r => r.data);
};
```

Rider pickup photos are uploaded by the mobile rider app, not the web frontend. The web dashboard only displays
`photoUrl`.

**Image display:**

- Always provide a fallback avatar/placeholder when `profileImageUrl` or `imageUrl` is null.
- Use `object-cover` CSS for consistent thumbnail rendering.

---

## 15. QR Code Verification Page

URL: `/verify/[token]` → `app/verify/[token]/page.tsx`

This is a **public Server Component** — no login required. Fetch server-side so the result is fully rendered for mobile
browsers and scanners.

**Flow:**

1. Next.js passes the token via `params.token`.
2. Server Component calls `GET /api/v1/certificates/verify/:token` using `fetch`.
3. Page is fully rendered server-side — no loading spinner, instant display.

```tsx
// app/verify/[token]/page.tsx
export default async function VerifyPage({ params }: { params: { token: string } }) {
  const res = await fetch(
    `${process.env.API_BASE_URL}/certificates/verify/${params.token}`,
    { cache: 'no-store' } // always fresh — each scan increments verifiedCount
  );
  if (!res.ok) return <NotFoundState />;
  const cert: PublicCertificateResponse = await res.json();
  return <CertificateDisplay cert={cert} />;
}
```

**Layout:**

```
┌─────────────────────────────────────┐
│  ZaoCycle Logo                      │
│  ─────────────────────────────────  │
│  ✅ CERTIFICATE ACTIVE              │  ← large status badge
│                                     │
│  Farmer:    John Kamau              │
│  Ward:      Mwea                    │
│  Crop:      Rice                    │
│  Chemical:  Mancozeb 80%            │
│  Issued:    15 May 2025             │
│  Expires:   29 May 2025             │
│  Verified:  3 times                 │
│                                     │
│  [Browse Briquette Products →]      │
└─────────────────────────────────────┘
```

**Status-aware display:**

| Status    | Colour | Icon | Message                      |
|-----------|--------|------|------------------------------|
| `ACTIVE`  | Green  | ✅    | Certificate is valid         |
| `EXPIRED` | Amber  | ⚠️   | Certificate has expired      |
| `REVOKED` | Red    | ❌    | Certificate has been revoked |

**Error state:** If the token is not found (404), show:
> "This certificate could not be found. The QR code may be damaged or invalid."

---

## 16. Security Considerations

- **Never** expose the JWT secret or backend credentials in frontend code.
- Variables prefixed `NEXT_PUBLIC_` are embedded in the client bundle — only use this prefix for values safe to expose (
  e.g., API base URL). Keep server secrets (database URLs, internal keys) as plain `process.env.X` used only in Server
  Components.
- The Axios client and Zustand store are browser-only — never import them in Server Components or Route Handlers.
- Sanitise any user-generated content before rendering. Avoid `dangerouslySetInnerHTML`. Next.js JSX escapes strings by
  default.
- On logout: call `POST /auth/logout`, clear Zustand (`logout()`), clear the role cookie, call `queryClient.clear()`,
  then `router.push('/login')`.
- The Next.js Middleware role check is a UX guard. The Spring Boot backend enforces all real authorisation via
  `@PreAuthorize` — a determined user who bypasses the middleware will still get a 403 from every API call.
- Use `next/headers` `cookies()` in Server Components/Route Handlers if you later move to httpOnly cookies for the
  refresh token.

---

## 17. Environment Configuration

Create `.env.local` at the project root (never commit to git):

```bash
# Server-only (Server Components, Route Handlers, middleware)
API_BASE_URL=http://localhost:8080/api/v1

# Exposed to the browser (Client Components, Axios)
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api/v1

NEXT_PUBLIC_APP_NAME=ZaoCycle
```

Production (`.env.production.local` or set in deployment platform):

```bash
API_BASE_URL=https://api.zaocycle.app/api/v1
NEXT_PUBLIC_API_BASE_URL=https://api.zaocycle.app/api/v1
```

**Usage by context:**

| Context                          | Variable                   | Access                                 |
|----------------------------------|----------------------------|----------------------------------------|
| Server Component / Route Handler | `API_BASE_URL`             | `process.env.API_BASE_URL`             |
| Client Component / Axios         | `NEXT_PUBLIC_API_BASE_URL` | `process.env.NEXT_PUBLIC_API_BASE_URL` |

You can also configure the backend origin in `next.config.ts` for rewrites (proxying in dev):

```ts
// next.config.ts
const nextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/v1/:path*',
        destination: `${process.env.API_BASE_URL}/:path*`,
      },
    ];
  },
};
export default nextConfig;
```

With this rewrite, the Axios `baseURL` can simply be `/api/v1` — no cross-origin issue in development.

---

## 18. Development Workflow

### Phase 1 — Foundations

- [ ] Scaffold project:
  `npx create-next-app@latest zaocycle-web --typescript --tailwind --app --src-dir no --import-alias "@/*"`
- [ ] Install dependencies: `shadcn/ui`, `@tanstack/react-query`, `axios`, `zustand`, `react-hook-form`, `zod`,
  `@hookform/resolvers`, `date-fns`, `lucide-react`, `sonner`, `qrcode.react`
- [ ] Configure `next.config.ts` with API rewrite proxy
- [ ] Set up App Router route groups `(portal)`, `(dashboard)`, `(admin)` with layout files
- [ ] Implement `middleware.ts` for role-based route protection
- [ ] Build Zustand auth store and `Providers` wrapper
- [ ] Build `AuthHydrator` for silent token refresh on page load
- [ ] Implement Axios client with interceptors
- [ ] Build login and register pages (`'use client'` components)

### Phase 2 — Public & Buyer

- [ ] Landing page
- [ ] Products listing page
- [ ] Buyer registration flow
- [ ] Buyer portal: home, browse products, place order
- [ ] Order history and order detail pages
- [ ] Buyer profile and image upload
- [ ] Certificate verification page

### Phase 3 — Staff Dashboard

- [ ] Dashboard overview with summary stats
- [ ] Pickup management (filters, assign rider, cancel)
- [ ] Order management (mark ready, mark delivered)
- [ ] Inventory: intake recording, batch creation, stock view
- [ ] Certificate list and detail with revoke action

### Phase 4 — Admin Panel

- [ ] Staff management (create, list, deactivate, reactivate)
- [ ] Rider management (register, list, deactivate)

### Phase 5 — Polish & QA

- [ ] Responsive design audit (mobile, tablet, desktop)
- [ ] Empty states for all lists
- [ ] Loading skeletons on all data tables and cards
- [ ] Error boundary at route level
- [ ] Accessibility audit (keyboard navigation, ARIA labels, colour contrast)
- [ ] Lighthouse performance pass

### API Design Notes for Backend Coordination

These improvements would help the frontend if added to the backend:

| Need                                      | Current workaround                                                                        |
|-------------------------------------------|-------------------------------------------------------------------------------------------|
| List all riders (for assignment dropdown) | `GET /admin/riders` does not exist as a list — use stored rider data or add the endpoint  |
| Paginated certificate list                | No list endpoint — add `GET /dashboard/certificates?page=`                                |
| Dashboard stats (pickup counts by status) | Aggregate client-side from pickups list, or add a `GET /dashboard/stats` summary endpoint |
| Order status filter for buyer             | Backend `GET /buyer/orders` has no `status` filter — filter client-side for now           |

---

## Quick Reference: Endpoint → Page Mapping

| Endpoint                                  | Page                               |
|-------------------------------------------|------------------------------------|
| `POST /auth/buyer/register`               | `/register`                        |
| `POST /auth/buyer/login`                  | `/login`                           |
| `POST /auth/staff/login`                  | `/login`                           |
| `POST /auth/refresh`                      | App init / token expiry            |
| `POST /auth/logout`                       | Logout action                      |
| `GET /products`                           | `/products`, `/portal/products`    |
| `GET /products/:id`                       | Order modal                        |
| `POST /buyer/orders`                      | Order modal form                   |
| `GET /buyer/orders`                       | `/portal/orders`                   |
| `GET /buyer/orders/:id`                   | `/portal/orders/:id`               |
| `DELETE /buyer/orders/:id`                | Cancel button                      |
| `GET /buyer/me`                           | `/portal/profile`                  |
| `PATCH /buyer/me`                         | `/portal/profile` form             |
| `POST /profile/image`                     | Profile image upload               |
| `GET /certificates/verify/:token`         | `/verify/:token`                   |
| `GET /dashboard/pickups`                  | `/dashboard/pickups`               |
| `POST /dashboard/pickups/:id/assign`      | Assign rider action                |
| `POST /dashboard/pickups/:id/cancel`      | Cancel pickup action               |
| `GET /dashboard/orders`                   | `/dashboard/orders`                |
| `POST /dashboard/orders/:id/ready`        | Mark ready action                  |
| `POST /dashboard/orders/:id/deliver`      | Mark delivered action              |
| `GET /dashboard/inventory/intake`         | `/dashboard/inventory` Intake tab  |
| `POST /dashboard/inventory/intake`        | Record intake form                 |
| `GET /dashboard/inventory/batches`        | `/dashboard/inventory` Batches tab |
| `POST /dashboard/inventory/batches`       | Create batch form                  |
| `GET /dashboard/inventory/stock`          | `/dashboard/inventory` Stock tab   |
| `GET /dashboard/certificates/:id`         | `/dashboard/certificates/:id`      |
| `POST /dashboard/certificates/:id/revoke` | Revoke action                      |
| `GET /admin/staff`                        | `/admin/staff`                     |
| `POST /admin/staff`                       | `/admin/staff/new`                 |
| `DELETE /admin/staff/:id`                 | Deactivate action                  |
| `POST /admin/staff/:id/activate`          | Reactivate action                  |
| `POST /admin/riders`                      | `/admin/riders/new`                |
| `GET /admin/riders/:id`                   | `/admin/riders/:id`                |
| `DELETE /admin/riders/:id`                | Deactivate action                  |
