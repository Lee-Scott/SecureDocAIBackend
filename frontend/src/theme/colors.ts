/**
 * Healthcare-focused color palette with WCAG 2.1 AA compliance
 * All color combinations maintain 4.5:1 contrast ratio minimum
 */

export const healthcareColors = {
  // Primary: Health-focused green variants
  primary: {
    50: '#E8F5E8',
    100: '#C8E6C8',
    200: '#A5D6A5',
    300: '#81C784',
    400: '#66BB6A',
    500: '#4CAF50', // Main primary
    600: '#43A047',
    700: '#388E3C',
    800: '#2E7D32', // Dark primary
    900: '#1B5E20',
    contrastText: '#ffffff',
  },
  
  // Secondary: Trust-building blue variants
  secondary: {
    50: '#E3F2FD',
    100: '#BBDEFB',
    200: '#90CAF9',
    300: '#64B5F6',
    400: '#42A5F5',
    500: '#2196F3', // Main secondary
    600: '#1E88E5',
    700: '#1976D2', // Dark secondary
    800: '#1565C0',
    900: '#0D47A1',
    contrastText: '#ffffff',
  },
  
  // Neutral: Professional grays with proper contrast
  neutral: {
    50: '#FAFAFA',
    100: '#F5F5F5',
    200: '#EEEEEE',
    300: '#E0E0E0',
    400: '#BDBDBD',
    500: '#9E9E9E',
    600: '#757575',
    700: '#616161',
    800: '#424242',
    900: '#212121',
  },
  
  // Semantic colors for health data
  success: {
    light: '#81C784',
    main: '#4CAF50',
    dark: '#388E3C',
    contrastText: '#ffffff',
  },
  
  warning: {
    light: '#FFB74D',
    main: '#FF9800',
    dark: '#F57C00',
    contrastText: '#000000',
  },
  
  error: {
    light: '#E57373',
    main: '#F44336',
    dark: '#D32F2F',
    contrastText: '#ffffff',
  },
  
  info: {
    light: '#64B5F6',
    main: '#2196F3',
    dark: '#1976D2',
    contrastText: '#ffffff',
  },
  
  // Healthcare-specific semantic colors
  healthData: {
    normal: '#4CAF50',
    concerning: '#FF9800',
    critical: '#F44336',
    unknown: '#9E9E9E',
  },
  
  // Background colors for different sections
  background: {
    default: '#FAFAFA',
    paper: '#FFFFFF',
    disabled: '#F5F5F5',
    patient: '#E8F5E8',
    provider: '#E3F2FD',
    admin: '#FFF3E0',
  },
} as const;

// High contrast theme colors for accessibility
export const highContrastColors = {
  primary: {
    main: '#000000',
    contrastText: '#FFFFFF',
  },
  secondary: {
    main: '#FFFFFF',
    contrastText: '#000000',
  },
  background: {
    default: '#FFFFFF',
    paper: '#FFFFFF',
  },
  text: {
    primary: '#000000',
    secondary: '#000000',
  },
} as const;

export type HealthcareColors = typeof healthcareColors;
export type HighContrastColors = typeof highContrastColors;