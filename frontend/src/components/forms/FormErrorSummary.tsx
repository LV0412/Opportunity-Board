type FormErrorSummaryProps = {
  errors: string[];
};

export function FormErrorSummary({ errors }: FormErrorSummaryProps) {
  if (!errors.length) {
    return null;
  }

  return (
    <div className="rounded-md bg-red-50 px-4 py-3 text-sm text-red-700">
      <p className="font-semibold">Vui lòng kiểm tra lại thông tin:</p>
      <ul className="mt-2 list-disc pl-5">
        {errors.map((error) => (
          <li key={error}>{error}</li>
        ))}
      </ul>
    </div>
  );
}
