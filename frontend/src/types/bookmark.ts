import type { Opportunity } from "./opportunity";

export type BookmarkItem = {
  id: string;
  opportunity: Opportunity;
  savedAt: string;
};
