# Healthcare Design System

A comprehensive, WCAG 2.1 AA compliant design system for healthcare applications built with Material-UI, React, and TypeScript.

## ✨ Features

- **🏥 Healthcare-focused**: Designed specifically for medical and healthcare applications
- **♿ Accessibility-first**: WCAG 2.1 AA compliant with comprehensive accessibility features
- **🎨 Professional themes**: Light, dark, and high-contrast theme variants
- **📱 Responsive**: Mobile-first design with optimal touch targets
- **🔧 TypeScript**: Fully typed for better development experience
- **📚 Storybook**: Comprehensive component documentation and testing
- **🧪 Tested**: Built-in accessibility testing with axe-core

## 🚀 Quick Start

### Installation

```bash
npm install @mui/material @emotion/react @emotion/styled
npm install @mui/icons-material @fontsource/roboto
```

### Basic Usage

```tsx
import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import { lightTheme } from './theme';
import { HealthAlert, PatientCard } from './components';

function App() {
  return (
    <ThemeProvider theme={lightTheme}>
      <CssBaseline />
      <HealthAlert severity="success" title="Lab Results Available">
        Your recent lab results are ready for review.
      </HealthAlert>
    </ThemeProvider>
  );
}
```

## 🎨 Theme System

### Available Themes

- **Light Theme**: Default healthcare-optimized light theme
- **Dark Theme**: Accessible dark mode with proper contrast ratios
- **High Contrast**: Maximum contrast for visually impaired users

### Color Palette

The color system is designed specifically for healthcare applications:

- **Primary Green (#4CAF50)**: Health and wellness indicators
- **Secondary Blue (#2196F3)**: Trust and reliability
- **Error Red (#F44336)**: Critical alerts and warnings
- **Warning Orange (#FF9800)**: Caution and attention needed
- **Success Green (#4CAF50)**: Positive outcomes and normal values

### Typography

Healthcare-optimized typography with improved readability:

- **Increased line height**: Better readability for medical content
- **Larger base font size**: 16px for improved accessibility
- **Medical data variants**: Monospace fonts for lab values and IDs
- **Responsive sizing**: Automatically adjusts for mobile devices

## 🧩 Components

### HealthAlert

Medical-specific alert component with healthcare severity levels.

```tsx
<HealthAlert severity="medical" title="Medication Interaction" dismissible>
  A potential interaction has been detected between your medications.
</HealthAlert>
```

**Features:**
- Medical severity level (`medical` in addition to standard levels)
- Dismissible and collapsible variants
- Proper ARIA attributes and screen reader support
- Keyboard navigation

### PatientCard

Secure patient information display with data masking.

```tsx
<PatientCard
  patient={patientData}
  onEdit={handleEdit}
  onView={handleView}
  compact={false}
/>
```

**Features:**
- Automatic data masking for PII (email, phone)
- Age calculation from date of birth
- Status indicators with color coding
- Accessible action buttons
- Responsive layout

### LabResultCard

Medical test result display with range visualization.

```tsx
<LabResultCard
  result={labResult}
  onViewDetails={handleDetails}
  showTrend={true}
/>
```

**Features:**
- Reference range visualization
- Trend indicators (up/down/stable)
- Status-based color coding
- Progress bar for values within range
- Flagged result highlighting

## ♿ Accessibility Features

### WCAG 2.1 AA Compliance

- **Color Contrast**: All color combinations meet 4.5:1 minimum ratio
- **Focus Management**: Visible focus indicators with proper order
- **Keyboard Navigation**: Full keyboard accessibility
- **Screen Reader Support**: Comprehensive ARIA attributes
- **Touch Targets**: Minimum 44px for mobile accessibility

### Testing

Built-in accessibility testing with:
- ESLint jsx-a11y rules
- Storybook a11y addon with axe-core
- Automated color contrast validation

### Usage Guidelines

1. **Always provide alt text** for images and icons
2. **Use semantic HTML** with proper heading hierarchy
3. **Include ARIA labels** for complex interactions
4. **Test with keyboard navigation** before deployment
5. **Validate color contrast** in all theme variants

## 📚 Documentation

### Storybook

Run Storybook to explore components interactively:

```bash
npm run storybook
```

Features:
- Interactive component playground
- Accessibility testing panel
- Theme switching
- Props documentation
- Usage examples

### Development

```bash
# Start development server
npm run dev

# Build for production
npm run build

# Run linting
npm run lint

# Start Storybook
npm run storybook
```

## 🔧 Customization

### Extending Themes

```tsx
import { createTheme } from '@mui/material/styles';
import { lightTheme } from './theme';

const customTheme = createTheme({
  ...lightTheme,
  palette: {
    ...lightTheme.palette,
    primary: {
      main: '#your-custom-color',
    },
  },
});
```

### Adding Components

Follow the atomic design structure:

```
src/components/
├── atoms/          # Basic building blocks
├── molecules/      # Simple combinations
├── organisms/      # Complex components
├── templates/      # Page layouts
└── pages/          # Complete pages
```

## 🏥 Healthcare-Specific Guidelines

### Data Display

- **Patient Information**: Always mask sensitive data appropriately
- **Lab Results**: Use consistent formatting for medical values
- **Medications**: Include proper dosage and interaction warnings
- **Alerts**: Use appropriate severity levels for medical context

### Compliance Considerations

- **HIPAA**: Components designed with data protection in mind
- **Medical Standards**: Color coding follows healthcare conventions
- **Internationalization**: Prepared for multi-language medical terms
- **Audit Trails**: Component actions support logging requirements

### Security

- **Data Masking**: Built-in PII protection
- **Input Validation**: Sanitized data handling
- **Secure Defaults**: Privacy-first component behavior
- **Access Control**: Role-based component visibility support

## 📈 Performance

- **Tree Shaking**: Import only needed components
- **Code Splitting**: Lazy loading support
- **Bundle Optimization**: Minimized runtime overhead
- **Responsive Images**: Automatic optimization for different screen sizes

## 🤝 Contributing

1. Follow the established component patterns
2. Include comprehensive accessibility features
3. Add Storybook stories for new components
4. Ensure WCAG 2.1 AA compliance
5. Update documentation and examples
