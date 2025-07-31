/**
 * Healthcare-optimized typography configuration
 * Focuses on readability for medical information display
 */
export const healthcareTypography = {
  fontFamily: [
    'Roboto',
    '-apple-system',
    'BlinkMacSystemFont',
    '"Segoe UI"',
    '"Helvetica Neue"',
    'Arial',
    'sans-serif',
  ].join(','),
  
  // Optimized font sizes for healthcare readability
  fontSize: 16, // Increased base font size for better readability
  
  // H1: Page titles, main headings
  h1: {
    fontSize: '2.5rem',
    fontWeight: 600,
    lineHeight: 1.2,
    letterSpacing: '-0.01562em',
    marginBottom: '1rem',
  },
  
  // H2: Section headings
  h2: {
    fontSize: '2rem',
    fontWeight: 600,
    lineHeight: 1.25,
    letterSpacing: '-0.00833em',
    marginBottom: '0.875rem',
  },
  
  // H3: Subsection headings
  h3: {
    fontSize: '1.75rem',
    fontWeight: 500,
    lineHeight: 1.3,
    letterSpacing: '0em',
    marginBottom: '0.75rem',
  },
  
  // H4: Card titles, form section headers
  h4: {
    fontSize: '1.5rem',
    fontWeight: 500,
    lineHeight: 1.35,
    letterSpacing: '0.00735em',
    marginBottom: '0.625rem',
  },
  
  // H5: Component titles, data labels
  h5: {
    fontSize: '1.25rem',
    fontWeight: 500,
    lineHeight: 1.4,
    letterSpacing: '0em',
    marginBottom: '0.5rem',
  },
  
  // H6: Small section headers
  h6: {
    fontSize: '1.125rem',
    fontWeight: 500,
    lineHeight: 1.45,
    letterSpacing: '0.0075em',
    marginBottom: '0.5rem',
  },
  
  // Body1: Main content text
  body1: {
    fontSize: '1rem',
    fontWeight: 400,
    lineHeight: 1.6, // Increased for better readability
    letterSpacing: '0.00938em',
  },
  
  // Body2: Secondary content text
  body2: {
    fontSize: '0.875rem',
    fontWeight: 400,
    lineHeight: 1.57,
    letterSpacing: '0.00714em',
  },
  
  // Caption: Small descriptive text, disclaimers
  caption: {
    fontSize: '0.75rem',
    fontWeight: 400,
    lineHeight: 1.66,
    letterSpacing: '0.03333em',
    color: 'text.secondary',
  },
  
  // Button: Button text
  button: {
    fontSize: '0.875rem',
    fontWeight: 500,
    lineHeight: 1.75,
    letterSpacing: '0.02857em',
    textTransform: 'none' as const, // Remove uppercase for better accessibility
  },
  
  // Overline: Labels, categories
  overline: {
    fontSize: '0.75rem',
    fontWeight: 500,
    lineHeight: 2.66,
    letterSpacing: '0.08333em',
    textTransform: 'uppercase' as const,
  },
  
  // Subtitle1: Large secondary text
  subtitle1: {
    fontSize: '1rem',
    fontWeight: 400,
    lineHeight: 1.75,
    letterSpacing: '0.00938em',
  },
  
  // Subtitle2: Medium secondary text
  subtitle2: {
    fontSize: '0.875rem',
    fontWeight: 500,
    lineHeight: 1.57,
    letterSpacing: '0.00714em',
  },
};

// Healthcare-specific typography variants
export const healthcareTypographyVariants = {
  // Medical data display
  medicalValue: {
    fontSize: '1.125rem',
    fontWeight: 600,
    lineHeight: 1.4,
    fontFamily: 'monospace',
    letterSpacing: '0.02em',
  },
  
  // Lab result values
  labResult: {
    fontSize: '1.25rem',
    fontWeight: 700,
    lineHeight: 1.3,
    fontFamily: 'monospace',
  },
  
  // Patient identifiers
  patientId: {
    fontSize: '0.875rem',
    fontWeight: 500,
    lineHeight: 1.5,
    fontFamily: 'monospace',
    letterSpacing: '0.05em',
  },
  
  // Dosage information
  dosage: {
    fontSize: '1rem',
    fontWeight: 600,
    lineHeight: 1.4,
    letterSpacing: '0.01em',
  },
  
  // Warning text
  warning: {
    fontSize: '0.875rem',
    fontWeight: 500,
    lineHeight: 1.6,
    color: 'warning.main',
  },
  
  // Error messages
  error: {
    fontSize: '0.875rem',
    fontWeight: 500,
    lineHeight: 1.6,
    color: 'error.main',
  },
  
  // Success messages
  success: {
    fontSize: '0.875rem',
    fontWeight: 500,
    lineHeight: 1.6,
    color: 'success.main',
  },
} as const;

// Responsive typography for mobile optimization
export const responsiveTypography = {
  '@media (max-width: 600px)': {
    h1: {
      fontSize: '2rem',
    },
    h2: {
      fontSize: '1.75rem',
    },
    h3: {
      fontSize: '1.5rem',
    },
    h4: {
      fontSize: '1.25rem',
    },
    body1: {
      fontSize: '0.875rem',
    },
  },
  '@media (max-width: 480px)': {
    h1: {
      fontSize: '1.75rem',
    },
    h2: {
      fontSize: '1.5rem',
    },
    h3: {
      fontSize: '1.25rem',
    },
  },
} as const;

export type HealthcareTypographyVariants = typeof healthcareTypographyVariants;