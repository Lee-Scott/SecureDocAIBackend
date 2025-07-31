import React from 'react';
import {
  Card,
  CardContent,
  CardHeader,
  Typography,
  Box,
  Chip,
  Avatar,
  IconButton,
  Divider,
} from '@mui/material';
import {
  Person as PersonIcon,
  Edit as EditIcon,
  Visibility as ViewIcon,
  MoreVert as MoreIcon,
} from '@mui/icons-material';

interface PatientInfo {
  id: string;
  name: string;
  dateOfBirth: string;
  gender: 'male' | 'female' | 'other' | 'prefer-not-to-say';
  email?: string;
  phone?: string;
  lastVisit?: string;
  status: 'active' | 'inactive' | 'pending';
  avatar?: string;
  mrn?: string; // Medical Record Number
}

interface PatientCardProps {
  patient: PatientInfo;
  onEdit?: (patient: PatientInfo) => void;
  onView?: (patient: PatientInfo) => void;
  onMore?: (patient: PatientInfo) => void;
  compact?: boolean;
  showActions?: boolean;
  className?: string;
}

/**
 * Patient information card with healthcare-specific formatting
 * Includes proper data masking and accessibility features
 */
export const PatientCard: React.FC<PatientCardProps> = ({
  patient,
  onEdit,
  onView,
  onMore,
  compact = false,
  showActions = true,
  className,
}) => {
  const calculateAge = (dateOfBirth: string): number => {
    const today = new Date();
    const birthDate = new Date(dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    return age;
  };

  const formatDate = (dateString: string): string => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active':
        return 'success';
      case 'inactive':
        return 'default';
      case 'pending':
        return 'warning';
      default:
        return 'default';
    }
  };

  const maskEmail = (email: string): string => {
    const [local, domain] = email.split('@');
    const maskedLocal = local.substring(0, 2) + '*'.repeat(local.length - 2);
    return `${maskedLocal}@${domain}`;
  };

  const maskPhone = (phone: string): string => {
    return phone.replace(/(\d{3})\d{3}(\d{4})/, '$1-***-$2');
  };

  return (
    <Card
      className={className}
      sx={{
        height: compact ? 'auto' : '300px',
        display: 'flex',
        flexDirection: 'column',
        '&:hover': {
          boxShadow: 2,
          transform: 'translateY(-2px)',
        },
        transition: 'all 0.2s ease-in-out',
      }}
      role="article"
      aria-label={`Patient card for ${patient.name}`}
    >
      <CardHeader
        avatar={
          <Avatar
            src={patient.avatar}
            sx={{
              bgcolor: 'primary.main',
              width: compact ? 40 : 56,
              height: compact ? 40 : 56,
            }}
            aria-label={`${patient.name}'s avatar`}
          >
            {patient.avatar ? null : <PersonIcon />}
          </Avatar>
        }
        title={
          <Typography
            variant={compact ? 'h6' : 'h5'}
            component="h3"
            sx={{ fontWeight: 600 }}
          >
            {patient.name}
          </Typography>
        }
        subheader={
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 0.5 }}>
            <Chip
              label={patient.status.toUpperCase()}
              size="small"
              color={getStatusColor(patient.status) as 'success' | 'warning' | 'error' | 'info'}
              variant="outlined"
            />
            {patient.mrn && (
              <Typography variant="caption" color="text.secondary">
                MRN: {patient.mrn}
              </Typography>
            )}
          </Box>
        }
        action={
          showActions && (
            <Box>
              {onView && (
                <IconButton
                  aria-label={`View ${patient.name}'s details`}
                  onClick={() => onView(patient)}
                  size="small"
                >
                  <ViewIcon />
                </IconButton>
              )}
              {onEdit && (
                <IconButton
                  aria-label={`Edit ${patient.name}'s information`}
                  onClick={() => onEdit(patient)}
                  size="small"
                >
                  <EditIcon />
                </IconButton>
              )}
              {onMore && (
                <IconButton
                  aria-label={`More options for ${patient.name}`}
                  onClick={() => onMore(patient)}
                  size="small"
                >
                  <MoreIcon />
                </IconButton>
              )}
            </Box>
          )
        }
        sx={{ pb: 1 }}
      />
      
      <CardContent sx={{ pt: 0, flex: 1 }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
          <Box>
            <Typography variant="body2" color="text.secondary">
              Age: {calculateAge(patient.dateOfBirth)} years old
            </Typography>
            <Typography variant="body2" color="text.secondary">
              DOB: {formatDate(patient.dateOfBirth)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Gender: {patient.gender.replace('-', ' ')}
            </Typography>
          </Box>
          
          {!compact && (
            <>
              <Divider />
              <Box>
                {patient.email && (
                  <Typography variant="body2" color="text.secondary">
                    Email: {maskEmail(patient.email)}
                  </Typography>
                )}
                {patient.phone && (
                  <Typography variant="body2" color="text.secondary">
                    Phone: {maskPhone(patient.phone)}
                  </Typography>
                )}
                {patient.lastVisit && (
                  <Typography variant="body2" color="text.secondary">
                    Last Visit: {formatDate(patient.lastVisit)}
                  </Typography>
                )}
              </Box>
            </>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default PatientCard;