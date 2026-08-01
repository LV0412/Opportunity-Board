type LoadingStateProps = {
  lines?: number;
  className?: string;
};

export function LoadingState({ lines = 3, className = "" }: LoadingStateProps) {
  return (
    <div className={`space-y-3 ${className}`.trim()}>
      {Array.from({ length: lines }).map((_, index) => (
        <div key={index} className="h-20 animate-pulse rounded-md border border-border bg-white" />
      ))}
    </div>
  );
}
