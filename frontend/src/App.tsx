import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Button,
  ToggleButton,
  ToggleButtonGroup,
  Divider,
} from '@mui/material';
import {
  Brightness4 as DarkModeIcon,
  Brightness7 as LightModeIcon,
  HighQuality as HighContrastIcon,
} from '@mui/icons-material';
import { ThemeProvider } from '@mui/material/styles';
import { themes, type ThemeMode } from './theme';
import HealthAlert from './components/molecules/HealthAlert/HealthAlert';
import PatientCard from './components/molecules/PatientCard/PatientCard';
import LabResultCard from './components/molecules/LabResultCard/LabResultCard';

function App() {
  const [themeMode, setThemeMode] = useState<ThemeMode>('light');

  const handleThemeChange = (
    _event: React.MouseEvent<HTMLElement>,
    newTheme: ThemeMode,
  ) => {
    if (newTheme !== null) {
      setThemeMode(newTheme);
    }
  };

  // Sample data
  const samplePatient = {
    id: 'p-001',
    name: 'Sarah Johnson',
    dateOfBirth: '1985-03-15',
    gender: 'female' as const,
    email: 'sarah.johnson@email.com',
    phone: '5551234567',
    lastVisit: '2024-01-15',
    status: 'active' as const,
    mrn: 'MRN-789456',
  };

  const sampleLabResult = {
    id: 'lab-001',
    testName: 'Hemoglobin A1C',
    value: 5.7,
    unit: '%',
    referenceRange: {
      min: 4.0,
      max: 5.6,
    },
    status: 'high' as const,
    trend: 'up' as const,
    testDate: '2024-01-15',
    notes: 'Slightly elevated, recommend dietary consultation',
  };

  return (
    <ThemeProvider theme={themes[themeMode]}>
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Box sx={{ mb: 4 }}>
          <Typography variant="h2" component="h1" gutterBottom>
            Healthcare Design System
          </Typography>
          <Typography variant="subtitle1" color="text.secondary" gutterBottom>
            WCAG 2.1 AA compliant design system for healthcare applications
          </Typography>
          
          <Box sx={{ mt: 3, display: 'flex', alignItems: 'center', gap: 2 }}>
            <Typography variant="body2">Theme:</Typography>
            <ToggleButtonGroup
              value={themeMode}
              exclusive
              onChange={handleThemeChange}
              aria-label="theme selection"
            >
              <ToggleButton value="light" aria-label="light theme">
                <LightModeIcon sx={{ mr: 1 }} />
                Light
              </ToggleButton>
              <ToggleButton value="dark" aria-label="dark theme">
                <DarkModeIcon sx={{ mr: 1 }} />
                Dark
              </ToggleButton>
              <ToggleButton value="highContrast" aria-label="high contrast theme">
                <HighContrastIcon sx={{ mr: 1 }} />
                High Contrast
              </ToggleButton>
            </ToggleButtonGroup>
          </Box>
        </Box>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
          {/* Typography Section */}
          <Paper sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
              Typography
            </Typography>
            <Typography variant="h1" gutterBottom>
              Heading 1 - Page Title
            </Typography>
            <Typography variant="h2" gutterBottom>
              Heading 2 - Section Title
            </Typography>
            <Typography variant="h3" gutterBottom>
              Heading 3 - Subsection
            </Typography>
            <Typography variant="body1" gutterBottom>
              Body 1 - Main content text with optimal line height for healthcare readability.
              This text meets WCAG 2.1 AA standards for contrast and spacing.
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Body 2 - Secondary content text for additional information.
            </Typography>
          </Paper>

          {/* Color Palette Section */}
          <Paper sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
              Color Palette
            </Typography>
            <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
              <Box
                sx={{
                  bgcolor: 'primary.main',
                  color: 'primary.contrastText',
                  p: 2,
                  borderRadius: 1,
                  textAlign: 'center',
                  minWidth: 120,
                }}
              >
                <Typography variant="h6">Primary</Typography>
                <Typography variant="body2">Health Green</Typography>
              </Box>
              <Box
                sx={{
                  bgcolor: 'secondary.main',
                  color: 'secondary.contrastText',
                  p: 2,
                  borderRadius: 1,
                  textAlign: 'center',
                  minWidth: 120,
                }}
              >
                <Typography variant="h6">Secondary</Typography>
                <Typography variant="body2">Trust Blue</Typography>
              </Box>
              <Box
                sx={{
                  bgcolor: 'error.main',
                  color: 'error.contrastText',
                  p: 2,
                  borderRadius: 1,
                  textAlign: 'center',
                  minWidth: 120,
                }}
              >
                <Typography variant="h6">Error</Typography>
                <Typography variant="body2">Critical</Typography>
              </Box>
              <Box
                sx={{
                  bgcolor: 'success.main',
                  color: 'success.contrastText',
                  p: 2,
                  borderRadius: 1,
                  textAlign: 'center',
                  minWidth: 120,
                }}
              >
                <Typography variant="h6">Success</Typography>
                <Typography variant="body2">Normal</Typography>
              </Box>
            </Box>
          </Paper>

          {/* Buttons Section */}
          <Paper sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
              Buttons
            </Typography>
            <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
              <Button variant="contained" color="primary">
                Primary Action
              </Button>
              <Button variant="outlined" color="secondary">
                Secondary Action
              </Button>
              <Button variant="text" color="info">
                Text Button
              </Button>
              <Button variant="contained" color="error">
                Critical Action
              </Button>
              <Button variant="contained" color="success">
                Confirm Action
              </Button>
            </Box>
          </Paper>

          {/* Health Alerts Section */}
          <Paper sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
              Health Alerts
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              <HealthAlert severity="success" title="Test Results Available">
                Your lab results are now available and all values are within normal ranges.
              </HealthAlert>
              <HealthAlert severity="warning" title="Appointment Reminder">
                You have an upcoming appointment tomorrow at 2:00 PM.
              </HealthAlert>
              <HealthAlert severity="medical" title="Medication Interaction" dismissible>
                A potential interaction has been detected between your medications.
              </HealthAlert>
              <HealthAlert severity="error" title="Critical Lab Values">
                Some lab values require immediate attention from your healthcare provider.
              </HealthAlert>
            </Box>
          </Paper>

          {/* Components Section */}
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            {/* Patient Card Section */}
            <Paper sx={{ p: 3, flex: 1, minWidth: 300 }}>
              <Typography variant="h4" gutterBottom>
                Patient Card
              </Typography>
              <PatientCard
                patient={samplePatient}
                onEdit={(patient) => console.log('Edit:', patient)}
                onView={(patient) => console.log('View:', patient)}
              />
            </Paper>

            {/* Lab Result Card Section */}
            <Paper sx={{ p: 3, flex: 1, minWidth: 300 }}>
              <Typography variant="h4" gutterBottom>
                Lab Result Card
              </Typography>
              <LabResultCard
                result={sampleLabResult}
                onViewDetails={(result) => console.log('View details:', result)}
              />
            </Paper>
          </Box>
        </Box>

        <Divider sx={{ my: 4 }} />

        <Box sx={{ textAlign: 'center' }}>
          <Typography variant="h5" gutterBottom>
            Accessibility Features
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ maxWidth: 600, mx: 'auto' }}>
            This design system includes proper focus management, ARIA attributes, 
            keyboard navigation, screen reader optimization, and WCAG 2.1 AA compliant 
            color contrasts for all healthcare data presentations.
          </Typography>
        </Box>
      </Container>
    </ThemeProvider>
  );
}

export default App;
