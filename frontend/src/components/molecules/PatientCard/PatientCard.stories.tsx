import type { Meta, StoryObj } from '@storybook/react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { lightTheme, darkTheme, highContrastTheme } from '../../../theme';
import PatientCard from './PatientCard';

const meta: Meta<typeof PatientCard> = {
  title: 'Healthcare/Molecules/PatientCard',
  component: PatientCard,
  parameters: {
    layout: 'padded',
    docs: {
      description: {
        component: 'Patient information card with healthcare-specific formatting and data masking.',
      },
    },
  },
  decorators: [
    (Story, context) => {
      const theme = context.globals.theme === 'dark' ? darkTheme : 
                   context.globals.theme === 'highContrast' ? highContrastTheme : lightTheme;
      return (
        <ThemeProvider theme={theme}>
          <CssBaseline />
          <Box sx={{ p: 2, maxWidth: 400 }}>
            <Story />
          </Box>
        </ThemeProvider>
      );
    },
  ],
  argTypes: {
    compact: {
      control: 'boolean',
      description: 'Whether to show a compact version of the card',
    },
    showActions: {
      control: 'boolean',
      description: 'Whether to show action buttons',
    },
  },
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof PatientCard>;

// Sample patient data
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

const pendingPatient = {
  id: 'p-002',
  name: 'Michael Chen',
  dateOfBirth: '1978-11-22',
  gender: 'male' as const,
  email: 'michael.chen@email.com',
  phone: '5559876543',
  status: 'pending' as const,
  mrn: 'MRN-123789',
};

const inactivePatient = {
  id: 'p-003',
  name: 'Emma Rodriguez',
  dateOfBirth: '1992-08-07',
  gender: 'female' as const,
  email: 'emma.rodriguez@email.com',
  lastVisit: '2023-06-10',
  status: 'inactive' as const,
  mrn: 'MRN-456123',
};

export const ActivePatient: Story = {
  args: {
    patient: samplePatient,
    onEdit: (patient) => console.log('Edit patient:', patient),
    onView: (patient) => console.log('View patient:', patient),
    onMore: (patient) => console.log('More options for patient:', patient),
  },
};

export const PendingPatient: Story = {
  args: {
    patient: pendingPatient,
    onEdit: (patient) => console.log('Edit patient:', patient),
    onView: (patient) => console.log('View patient:', patient),
  },
};

export const InactivePatient: Story = {
  args: {
    patient: inactivePatient,
    onView: (patient) => console.log('View patient:', patient),
  },
};

export const CompactView: Story = {
  args: {
    patient: samplePatient,
    compact: true,
    onEdit: (patient) => console.log('Edit patient:', patient),
    onView: (patient) => console.log('View patient:', patient),
  },
};

export const NoActions: Story = {
  args: {
    patient: samplePatient,
    showActions: false,
  },
};

export const WithoutContactInfo: Story = {
  args: {
    patient: {
      ...samplePatient,
      email: undefined,
      phone: undefined,
    },
    onEdit: (patient) => console.log('Edit patient:', patient),
    onView: (patient) => console.log('View patient:', patient),
  },
};

export const MultipleCards: Story = {
  render: () => {
    const patients = [samplePatient, pendingPatient, inactivePatient];
    
    return (
      <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
        {patients.map((patient) => (
          <Box key={patient.id} sx={{ width: 300 }}>
            <PatientCard
              patient={patient}
              onEdit={(p) => console.log('Edit patient:', p)}
              onView={(p) => console.log('View patient:', p)}
              onMore={(p) => console.log('More options for patient:', p)}
            />
          </Box>
        ))}
      </Box>
    );
  },
  parameters: {
    docs: {
      description: {
        story: 'Multiple patient cards displayed in a grid layout.',
      },
    },
  },
};