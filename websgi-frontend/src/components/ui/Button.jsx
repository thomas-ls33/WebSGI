import { forwardRef } from 'react';

const variants = {
  primary:
    'bg-cyan-glow text-navy font-semibold hover:shadow-glow hover:-translate-y-0.5 border border-cyan-glow',
  outline:
    'bg-transparent text-cyan-glow border border-cyan-glow/60 hover:border-cyan-glow hover:shadow-glow-sm hover:-translate-y-0.5',
  ghost:
    'bg-transparent text-ink-secondary hover:text-ink-primary border border-transparent',
};

const sizes = {
  sm: 'px-4 py-2 text-sm',
  md: 'px-6 py-3 text-sm',
  lg: 'px-8 py-4 text-base',
};

const Button = forwardRef(function Button(
  { as: Component = 'button', variant = 'primary', size = 'md', className = '', children, ...props },
  ref
) {
  return (
    <Component
      ref={ref}
      className={`inline-flex items-center justify-center gap-2 rounded-xl font-body tracking-wide transition-all duration-200 ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    >
      {children}
    </Component>
  );
});

export default Button;
