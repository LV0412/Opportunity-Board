# Frontend redesign plan

## Overview

Refresh the existing React frontend using the supplied pitch-deck visual language while preserving routes, API calls, authentication, and role workflows. The chosen approach is a shared design-system refresh plus focused updates to the public experience and reusable surfaces; a full dashboard rewrite is out of scope.

After the latest pull, preserve the new organization-verification and navigation flows. Reapply the visual system through shared shells and component hooks instead of restoring stale page implementations.

## Steps

1. Replace the base tokens and global styles with the warm paper, teal, coral, glass, shadow, focus, and reduced-motion system from the reference UI.
2. Rebuild the home page with a responsive glass navigation, editorial hero, animated product preview, trust highlights, and approved-opportunity section.
3. Upgrade shared Button, Card, OpportunityCard, and DashboardLayout surfaces so authenticated pages inherit the new visual language.
4. Build the production bundle and review the diff for TypeScript, responsive, accessibility, and behavior regressions.
5. Redesign the shared role shell, student dashboard, and notification experience so authenticated screens follow the same visual language instead of only inheriting new colors.
6. Audit all user-facing frontend strings and replace Vietnamese labels that were committed without diacritics.
7. Move Explore (for signed-in students), Saved, Applications, and Profile into the shared dashboard shell; redesign their filters, list states, tracker, and form hierarchy around each page's primary task.
8. Reconcile the pulled UI: restyle the current `PublicHeader`, `Footer`, `Sidebar`, and `DashboardLayout`, retaining every newly pulled route and action.

## Completion criteria

- Existing routes and data-fetching behavior remain unchanged.
- Public pages and shared dashboard surfaces use a coherent modern design on mobile and desktop.
- Interactive elements have visible focus states and motion respects `prefers-reduced-motion`.
- `npm run build` succeeds.
- A source scan finds no known unaccented Vietnamese fallback labels in `frontend/src`.
- Student navigation remains persistent across Explore, Saved, Applications, and Profile, while Explore remains usable publicly.
