export default function Card({ as: Tag = 'section', className = '', children, ...rest }) {
  return (
    <Tag
      className={`bg-white border border-gray-200 rounded-2xl p-4 ${className}`}
      {...rest}
    >
      {children}
    </Tag>
  )
}
