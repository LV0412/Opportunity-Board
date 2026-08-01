const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

type ApiClientOptions = RequestInit & {
  skipAuth?: boolean;
};

type ApiErrorBody = {
  message?: string;
  error?: string;
  detail?: string;
  fieldErrors?: Array<{ field: string; message: string }>;
};

const AUTH_TOKEN_KEY = "opportunity_board_token";

export function getStoredToken() {
  return localStorage.getItem(AUTH_TOKEN_KEY);
}

export function setStoredToken(token: string) {
  localStorage.setItem(AUTH_TOKEN_KEY, token);
}

export function clearStoredToken() {
  localStorage.removeItem(AUTH_TOKEN_KEY);
}

export async function apiClient<T>(path: string, init?: ApiClientOptions): Promise<T> {
  const { skipAuth, ...requestInit } = init ?? {};
  const token = skipAuth ? null : getStoredToken();
  const isFormData = requestInit.body instanceof FormData;
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      ...(isFormData ? {} : { "Content-Type": "application/json" }),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...requestInit.headers,
    },
    ...requestInit,
  });

  if (!response.ok) {
    const message = await readErrorMessage(response);
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const body = await response.json();
  return (body?.data ?? body) as T;
}

async function readErrorMessage(response: Response) {
  try {
    const body = await response.json() as ApiErrorBody;
    const fieldErrors = body.fieldErrors?.map((item) => item.message).filter(Boolean) ?? [];
    if (fieldErrors.length) {
      return fieldErrors.join(" ");
    }
    return body.message ?? body.error ?? body.detail;
  } catch {
    return "";
  }
}
