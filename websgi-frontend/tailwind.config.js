export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        navy: {
          DEFAULT: '#002247',
          light: '#012C4F',
        },
        cyan: {
          glow: '#73DAEA',
          soft: '#589DB7',
        },
        ink: {
          primary: '#ECF3F5',
          secondary: '#A9C2D4',
        },
      },
      fontFamily: {
        heading: ['Orbitron', 'sans-serif'],
        body: ['Inter', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
      backgroundImage: {
        'navy-gradient': 'linear-gradient(160deg, #002247 0%, #012C4F 100%)',
        'grid-pattern': 'linear-gradient(rgba(115,218,234,0.055) 1px, transparent 1px), linear-gradient(90deg, rgba(115,218,234,0.055) 1px, transparent 1px)',
      },
      boxShadow: {
        glow: '0 0 24px rgba(115,218,234,0.35)',
        'glow-sm': '0 0 12px rgba(115,218,234,0.25)',
        'glow-lg': '0 0 48px rgba(115,218,234,0.28)',
      },
      animation: {
        blink: 'blink 1.1s steps(2, start) infinite',
      },
      keyframes: {
        blink: {
          'to': { visibility: 'hidden' },
        },
      },
    },
  },
  plugins: [],
}
