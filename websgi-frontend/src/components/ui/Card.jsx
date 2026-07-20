export default function Card({ className = '', hover = false, children, ...props }) {
  return (
    <div
      className={`rounded-2xl border border-cyan-glow/20 bg-navy-light/70 backdrop-blur-sm p-6 ${
        hover ? 'transition-all duration-300 hover:border-cyan-glow/60 hover:shadow-glow-sm hover:-translate-y-1' : ''
      } ${className}`}
      {...props}
    >
      {children}
    </div>
  );
}
