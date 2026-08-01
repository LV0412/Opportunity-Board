export function isEmail(value: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim());
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
