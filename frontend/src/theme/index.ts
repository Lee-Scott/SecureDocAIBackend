import { type ThemeOptions, createTheme } from '@mui/material/styles';
import { healthcareColors, highContrastColors } from './colors';
import { healthcareTypography } from './typography';

// Base theme options for healthcare platform
const baseThemeOptions: ThemeOptions = {
  typography: healthcareTypography,
  
  // Spacing system optimized for healthcare UI
  spacing: 8, // 8px base unit
  
  // Border radius for modern, accessible design
  shape: {
    borderRadius: 8,
  },
  
  // Component overrides for healthcare requirements
  components: {
    // Button overrides
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          textTransform: 'none',
          fontWeight: 500,
          minHeight: 44, // WCAG touch target minimum
          padding: '12px 24px',
          '&:focus-visible': {
            outline: '2px solid',
            outlineOffset: '2px',
          },
        },
        contained: {
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          '&:hover': {
            boxShadow: '0 4px 8px rgba(0,0,0,0.15)',
          },
        },
      },
      variants: [
        {
          props: { variant: 'contained', color: 'primary' },
          style: {
            background: 'linear-gradient(45deg, #4CAF50 30%, #66BB6A 90%)',
            '&:hover': {
              background: 'linear-gradient(45deg, #43A047 30%, #4CAF50 90%)',
            },
          },
        },
      ],
    },
    
    // Card overrides for healthcare data display
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
          border: '1px solid rgba(0,0,0,0.08)',
          '&:hover': {
            boxShadow: '0 4px 16px rgba(0,0,0,0.15)',
          },
        },
      },
    },
    
    // Input field overrides
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
            '&:hover .MuiOutlinedInput-notchedOutline': {
              borderColor: healthcareColors.primary[400],
            },
            '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
              borderWidth: 2,
            },
          },
        },
      },
    },
    
    // Alert overrides for healthcare notifications
    MuiAlert: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          '& .MuiAlert-icon': {
            fontSize: '1.5rem',
          },
        },
        standardSuccess: {
          backgroundColor: healthcareColors.success.light + '20',
          border: `1px solid ${healthcareColors.success.light}`,
        },
        standardWarning: {
          backgroundColor: healthcareColors.warning.light + '20',
          border: `1px solid ${healthcareColors.warning.light}`,
        },
        standardError: {
          backgroundColor: healthcareColors.error.light + '20',
          border: `1px solid ${healthcareColors.error.light}`,
        },
        standardInfo: {
          backgroundColor: healthcareColors.info.light + '20',
          border: `1px solid ${healthcareColors.info.light}`,
        },
      },
    },
    
    // Chip overrides for tags and status indicators
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          height: 32,
          fontSize: '0.875rem',
          fontWeight: 500,
        },
      },
    },
    
    // Table overrides for data display
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottom: `1px solid ${healthcareColors.neutral[200]}`,
          padding: '16px',
        },
        head: {
          backgroundColor: healthcareColors.neutral[50],
          fontWeight: 600,
          fontSize: '0.875rem',
          textTransform: 'uppercase',
          letterSpacing: '0.05em',
        },
      },
    },
    
    // Drawer overrides for navigation
    MuiDrawer: {
      styleOverrides: {
        paper: {
          borderRight: `1px solid ${healthcareColors.neutral[200]}`,
          backgroundColor: healthcareColors.background.paper,
        },
      },
    },
    
    // AppBar overrides
    MuiAppBar: {
      styleOverrides: {
        root: {
          boxShadow: '0 1px 3px rgba(0,0,0,0.12)',
          borderBottom: `1px solid ${healthcareColors.neutral[200]}`,
        },
      },
    },
  },
  
  // Breakpoints for responsive design
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 960,
      lg: 1280,
      xl: 1920,
    },
  },
};

// Light theme for healthcare platform
export const lightTheme = createTheme({
  ...baseThemeOptions,
  palette: {
    mode: 'light',
    primary: {
      ...healthcareColors.primary,
    },
    secondary: {
      ...healthcareColors.secondary,
    },
    error: {
      ...healthcareColors.error,
    },
    warning: {
      ...healthcareColors.warning,
    },
    info: {
      ...healthcareColors.info,
    },
    success: {
      ...healthcareColors.success,
    },
    background: {
      ...healthcareColors.background,
    },
    text: {
      primary: healthcareColors.neutral[900],
      secondary: healthcareColors.neutral[700],
      disabled: healthcareColors.neutral[500],
    },
    divider: healthcareColors.neutral[200],
    grey: healthcareColors.neutral,
  },
});

// Dark theme for healthcare platform
export const darkTheme = createTheme({
  ...baseThemeOptions,
  palette: {
    mode: 'dark',
    primary: {
      ...healthcareColors.primary,
      main: healthcareColors.primary[400],
    },
    secondary: {
      ...healthcareColors.secondary,
      main: healthcareColors.secondary[400],
    },
    error: {
      ...healthcareColors.error,
      main: healthcareColors.error.light,
    },
    warning: {
      ...healthcareColors.warning,
      main: healthcareColors.warning.light,
    },
    info: {
      ...healthcareColors.info,
      main: healthcareColors.info.light,
    },
    success: {
      ...healthcareColors.success,
      main: healthcareColors.success.light,
    },
    background: {
      default: healthcareColors.neutral[900],
      paper: healthcareColors.neutral[800],
    },
    text: {
      primary: healthcareColors.neutral[50],
      secondary: healthcareColors.neutral[300],
      disabled: healthcareColors.neutral[600],
    },
    divider: healthcareColors.neutral[700],
    grey: healthcareColors.neutral,
  },
});

// High contrast theme for accessibility
export const highContrastTheme = createTheme({
  ...baseThemeOptions,
  palette: {
    mode: 'light',
    primary: highContrastColors.primary,
    secondary: highContrastColors.secondary,
    background: highContrastColors.background,
    text: highContrastColors.text,
    error: {
      main: '#000000',
      contrastText: '#FFFFFF',
    },
    warning: {
      main: '#000000',
      contrastText: '#FFFFFF',
    },
    info: {
      main: '#000000',
      contrastText: '#FFFFFF',
    },
    success: {
      main: '#000000',
      contrastText: '#FFFFFF',
    },
  },
  components: {
    ...baseThemeOptions.components,
    MuiButton: {
      ...baseThemeOptions.components?.MuiButton,
      styleOverrides: {
        root: {
          border: '2px solid #000000',
        },
      },
    },
  },
});

// Theme type definitions
export type HealthcareTheme = typeof lightTheme;

// Available themes
export const themes = {
  light: lightTheme,
  dark: darkTheme,
  highContrast: highContrastTheme,
} as const;

export type ThemeMode = keyof typeof themes;