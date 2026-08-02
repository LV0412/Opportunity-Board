# Login flow fix

## Overview

Keep the authentication API contract while preventing malformed email submissions and giving pending-verification users an actionable message.

## Steps

1. Normalize email and detect a repeated `.vn` suffix.
2. Submit the normalized email without changing the password.
3. Explain the pending-verification response in Vietnamese.
4. Build and review the request path.

## Completion criteria

- `.vn.vn` is blocked before the request.
- Password is passed unchanged.
- Pending accounts receive a verification instruction.
- `npm run build` succeeds.
