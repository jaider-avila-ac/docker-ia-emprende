export default function DataTable({ columns, children }) {
  return (
    <div className="overflow-x-auto rounded-lg border border-gray-200">
      <table className="w-full text-sm">
        <thead className="bg-gray-50 text-gray-600">
          <tr>
            {columns.map((col) => (
              <th key={col} className="text-left px-3 py-2">
                {col}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-200">{children}</tbody>
      </table>
    </div>
  )
}

export function Td({ className = '', children }) {
  return <td className={`px-3 py-2 ${className}`}>{children}</td>
}
