import type { Meta, StoryObj } from '@storybook/react';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { lightTheme, darkTheme, highContrastTheme } from '../../../theme';
import HealthAlert from './HealthAlert';

const meta: Meta<typeof HealthAlert> = {
  title: 'Healthcare/Molecules/HealthAlert',
  component: HealthAlert,
  parameters: {
    layout: 'padded',
    docs: {
      description: {
        component: 'Healthcare-specific alert component with medical severity levels and accessibility features.',
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
          <Box sx={{ p: 2 }}>
            <Story />
          </Box>
        </ThemeProvider>
      );
    },
  ],
  argTypes: {
    severity: {
      control: 'select',
      options: ['success', 'warning', 'error', 'info', 'medical'],
      description: 'The severity level of the alert',
    },
    title: {
      control: 'text',
      description: 'Optional title for the alert',
    },
    dismissible: {
      control: 'boolean',
      description: 'Whether the alert can be dismissed',
    },
    collapsible: {
      control: 'boolean',
      description: 'Whether the alert content can be collapsed',
    },
    defaultExpanded: {
      control: 'boolean',
      description: 'Whether the collapsible alert is expanded by default',
    },
    children: {
      control: 'text',
      description: 'The content of the alert',
    },
  },
  tags: ['autodocs'],
};

export default meta;
type Story = StoryObj<typeof HealthAlert>;

export const Success: Story = {
  args: {
    severity: 'success',
    title: 'Test Results Available',
    children: 'Your lab results are now available in your patient portal. All values are within normal ranges.',
  },
};

export const Warning: Story = {
  args: {
    severity: 'warning',
    title: 'Appointment Reminder',
    children: 'You have an upcoming appointment tomorrow at 2:00 PM. Please arrive 15 minutes early for check-in.',
  },
};

export const Error: Story = {
  args: {
    severity: 'error',
    title: 'Critical Lab Values',
    children: 'Some of your lab values require immediate attention. Please contact your healthcare provider as soon as possible.',
  },
};

export const Info: Story = {
  args: {
    severity: 'info',
    title: 'Health Information Update',
    children: 'New health guidelines have been published. Review the updated recommendations in your care plan.',
  },
};

export const Medical: Story = {
  args: {
    severity: 'medical',
    title: 'Medication Interaction Alert',
    children: 'A potential interaction has been detected between your current medications. Please consult with your pharmacist.',
  },
};

export const Dismissible: Story = {
  args: {
    severity: 'info',
    title: 'System Maintenance',
    dismissible: true,
    children: 'Scheduled maintenance will occur tonight from 12:00 AM to 4:00 AM. Some features may be temporarily unavailable.',
  },
};

export const Collapsible: Story = {
  args: {
    severity: 'warning',
    title: 'Medication Side Effects',
    collapsible: true,
    defaultExpanded: false,
    children: 'Common side effects may include nausea, dizziness, and fatigue. If you experience severe symptoms, contact your healthcare provider immediately. Monitor your symptoms and report any concerns during your next visit.',
  },
};

export const CollapsibleAndDismissible: Story = {
  args: {
    severity: 'medical',
    title: 'Pre-procedure Instructions',
    dismissible: true,
    collapsible: true,
    defaultExpanded: true,
    children: `
      Important instructions for your upcoming procedure:
      
      • Fast for 12 hours before the procedure
      • Take prescribed medications as directed
      • Arrive 30 minutes early for preparation
      • Bring a list of current medications
      • Arrange for transportation home after the procedure
      
      Contact us if you have any questions or concerns.
    `,
  },
};

export const LongContent: Story = {
  args: {
    severity: 'info',
    title: 'Comprehensive Health Report Available',
    children: `
      Your annual comprehensive health report has been completed and is now available for review. 
      This report includes detailed analysis of your recent lab work, imaging studies, and physical examination findings. 
      
      Key highlights:
      • Cardiovascular health markers are within normal limits
      • Blood glucose levels show excellent control
      • Bone density scan results indicate healthy bone structure
      • Recommended preventive care measures have been updated
      
      Please schedule a follow-up appointment to discuss these results in detail with your healthcare provider. 
      If you have immediate questions or concerns, please contact our office during regular business hours.
    `,
  },
};