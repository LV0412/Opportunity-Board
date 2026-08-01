import { useMemo, useState } from "react";

export function useAsyncState(initialLoading = true) {
  const [loading, setLoading] = useState(initialLoading);
  const [error, setError] = useState("");

  return useMemo(() => ({
    loading,
    error,
    start() {
      setLoading(true);
      setError("");
    },
    fail(message: string) {
      setError(message);
      setLoading(false);
    },
    finish() {
      setLoading(false);
    },
    clearError() {
      setError("");
    },
  }), [error, loading]);
}
