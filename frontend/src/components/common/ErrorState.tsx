type ErrorStateProps = {
  message: string;
  className?: string;
};

export function ErrorState({ message, className = "" }: ErrorStateProps) {
  return <p className={`rounded-md bg-red-50 px-4 py-3 text-sm text-red-700 ${className}`.trim()}>{message}</p>;
}
