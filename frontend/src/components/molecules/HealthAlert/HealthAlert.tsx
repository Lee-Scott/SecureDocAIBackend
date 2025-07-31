import React from 'react';
import {
  Alert,
  type AlertProps,
  AlertTitle,
  Box,
  Collapse,
  IconButton,
  Typography,
} from '@mui/material';
import {
  Close as CloseIcon,
  LocalHospital as MedicalIcon,
  Warning as WarningIcon,
  Error as ErrorIcon,
  CheckCircle as SuccessIcon,
  Info as InfoIcon,
} from '@mui/icons-material';

interface HealthAlertProps extends Omit<AlertProps, 'severity'> {
  severity: 'success' | 'warning' | 'error' | 'info' | 'medical';
  title?: string;
  dismissible?: boolean;
  onDismiss?: () => void;
  collapsible?: boolean;
  defaultExpanded?: boolean;
  children: React.ReactNode;
}

/**
 * Healthcare-specific alert component with medical severity levels
 * Includes proper ARIA attributes and keyboard navigation
 */
export const HealthAlert: React.FC<HealthAlertProps> = ({
  severity,
  title,
  dismissible = false,
  onDismiss,
  collapsible = false,
  defaultExpanded = true,
  children,
  ...props
}) => {
  const [isOpen, setIsOpen] = React.useState(true);
  const [isExpanded, setIsExpanded] = React.useState(defaultExpanded);

  const handleDismiss = () => {
    setIsOpen(false);
    onDismiss?.();
  };

  const handleToggle = () => {
    setIsExpanded(!isExpanded);
  };

  const getIcon = () => {
    switch (severity) {
      case 'success':
        return <SuccessIcon />;
      case 'warning':
        return <WarningIcon />;
      case 'error':
        return <ErrorIcon />;
      case 'info':
        return <InfoIcon />;
      case 'medical':
        return <MedicalIcon />;
      default:
        return <InfoIcon />;
    }
  };

  const getSeverityColor = () => {
    if (severity === 'medical') return 'info';
    return severity;
  };

  if (!isOpen) return null;

  return (
    <Alert
      severity={getSeverityColor()}
      icon={getIcon()}
      action={
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {collapsible && (
            <IconButton
              aria-label={isExpanded ? 'Collapse alert' : 'Expand alert'}
              size="small"
              onClick={handleToggle}
              sx={{ color: 'inherit' }}
            >
              {isExpanded ? '−' : '+'}
            </IconButton>
          )}
          {dismissible && (
            <IconButton
              aria-label="Close alert"
              size="small"
              onClick={handleDismiss}
              sx={{ color: 'inherit' }}
            >
              <CloseIcon fontSize="inherit" />
            </IconButton>
          )}
        </Box>
      }
      role="alert"
      aria-live="polite"
      sx={{
        '& .MuiAlert-message': {
          width: '100%',
        },
        ...(severity === 'medical' && {
          backgroundColor: 'primary.light',
          '& .MuiAlert-icon': {
            color: 'primary.main',
          },
        }),
      }}
      {...props}
    >
      {title && (
        <AlertTitle sx={{ fontWeight: 600, mb: 1 }}>
          {title}
        </AlertTitle>
      )}
      
      {collapsible ? (
        <Collapse in={isExpanded}>
          <Typography variant="body2" component="div">
            {children}
          </Typography>
        </Collapse>
      ) : (
        <Typography variant="body2" component="div">
          {children}
        </Typography>
      )}
    </Alert>
  );
};

export default HealthAlert;