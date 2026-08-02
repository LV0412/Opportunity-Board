export function isEmail(value: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim());
}

export function normalizeEmail(value: string) {
  return value.trim().toLowerCase();
}

export function emailTypoMessage(value: string) {
  return /\.vn\.vn$/.test(normalizeEmail(value))
    ? "Email đang bị lặp đuôi .vn. Vui lòng kiểm tra lại địa chỉ email."
    : "";
}

export function isUrl(value: string) {
  return /^https?:\/\/.+/i.test(value.trim());
}

export function requiredMessage(label: string, value: string) {
  return value.trim() ? "" : `${label} là bắt buộc.`;
}

export function minLengthMessage(label: string, value: string, minLength: number) {
  return value.trim().length >= minLength ? "" : `${label} phải có ít nhất ${minLength} ký tự.`;
}
