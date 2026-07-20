const baseInput =
  'w-full rounded-lg bg-navy border border-cyan-soft/30 px-4 py-3 text-ink-primary placeholder:text-ink-secondary/50 outline-none transition-colors focus:border-cyan-glow focus:shadow-glow-sm';

export function Label({ children, htmlFor, required }) {
  return (
    <label htmlFor={htmlFor} className="mb-2 block text-sm font-medium text-ink-secondary">
      {children}
      {required && <span className="ml-1 text-cyan-glow">*</span>}
    </label>
  );
}

export function Input(props) {
  return <input className={baseInput} {...props} />;
}

export function Textarea(props) {
  return <textarea className={`${baseInput} min-h-32 resize-y`} {...props} />;
}

export function Select({ children, ...props }) {
  return (
    <select className={`${baseInput} appearance-none`} {...props}>
      {children}
    </select>
  );
}

export function FieldGroup({ label, htmlFor, required, children }) {
  return (
    <div>
      <Label htmlFor={htmlFor} required={required}>
        {label}
      </Label>
      {children}
    </div>
  );
}
