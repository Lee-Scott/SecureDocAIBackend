import React from 'react';
import {
  Card,
  CardContent,
  CardHeader,
  Typography,
  Box,
  Chip,
  LinearProgress,
  Divider,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  Science as LabIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Remove as NormalIcon,
  Warning as WarningIcon,
  Error as CriticalIcon,
  Info as InfoIcon,
} from '@mui/icons-material';

interface LabResult {
  id: string;
  testName: string;
  value: number | string;
  unit: string;
  referenceRange: {
    min?: number;
    max?: number;
    text?: string;
  };
  status: 'normal' | 'high' | 'low' | 'critical' | 'pending';
  trend?: 'up' | 'down' | 'stable';
  testDate: string;
  notes?: string;
  flagged?: boolean;
}

interface LabResultCardProps {
  result: LabResult;
  onViewDetails?: (result: LabResult) => void;
  compact?: boolean;
  showTrend?: boolean;
  className?: string;
}

/**
 * Lab result display card with healthcare-specific formatting and indicators
 * Includes proper accessibility features and medical data visualization
 */
export const LabResultCard: React.FC<LabResultCardProps> = ({
  result,
  onViewDetails,
  compact = false,
  showTrend = true,
  className,
}) => {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'normal':
        return 'success';
      case 'high':
      case 'low':
        return 'warning';
      case 'critical':
        return 'error';
      case 'pending':
        return 'info';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'normal':
        return <NormalIcon />;
      case 'high':
      case 'low':
        return <WarningIcon />;
      case 'critical':
        return <CriticalIcon />;
      case 'pending':
        return <InfoIcon />;
      default:
        return <InfoIcon />;
    }
  };

  const getTrendIcon = (trend?: string) => {
    switch (trend) {
      case 'up':
        return <TrendingUpIcon sx={{ color: 'success.main' }} />;
      case 'down':
        return <TrendingDownIcon sx={{ color: 'error.main' }} />;
      case 'stable':
        return <NormalIcon sx={{ color: 'info.main' }} />;
      default:
        return null;
    }
  };

  const formatDate = (dateString: string): string => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const getProgressValue = (): number => {
    if (typeof result.value !== 'number' || !result.referenceRange.min || !result.referenceRange.max) {
      return 0;
    }
    
    const { min, max } = result.referenceRange;
    const value = result.value;
    
    // Calculate percentage within range (50% is optimal)
    const range = max - min;
    const position = (value - min) / range * 100;
    
    return Math.max(0, Math.min(100, position));
  };

  const getReferenceRangeText = (): string => {
    if (result.referenceRange.text) {
      return result.referenceRange.text;
    }
    
    const { min, max } = result.referenceRange;
    if (min !== undefined && max !== undefined) {
      return `${min} - ${max} ${result.unit}`;
    } else if (min !== undefined) {
      return `> ${min} ${result.unit}`;
    } else if (max !== undefined) {
      return `< ${max} ${result.unit}`;
    }
    
    return 'See reference';
  };

  return (
    <Card
      className={className}
      sx={{
        height: compact ? 'auto' : '280px',
        display: 'flex',
        flexDirection: 'column',
        border: result.flagged ? '2px solid' : '1px solid',
        borderColor: result.flagged ? 'warning.main' : 'divider',
        '&:hover': {
          boxShadow: 2,
          cursor: onViewDetails ? 'pointer' : 'default',
        },
        transition: 'all 0.2s ease-in-out',
      }}
      onClick={() => onViewDetails?.(result)}
      role="article"
      aria-label={`Lab result for ${result.testName}`}
    >
      <CardHeader
        avatar={<LabIcon sx={{ color: 'primary.main' }} />}
        title={
          <Typography
            variant={compact ? 'subtitle1' : 'h6'}
            component="h3"
            sx={{ fontWeight: 600 }}
          >
            {result.testName}
          </Typography>
        }
        subheader={formatDate(result.testDate)}
        action={
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            {showTrend && result.trend && (
              <Tooltip title={`Trend: ${result.trend}`}>
                <IconButton size="small" disabled>
                  {getTrendIcon(result.trend)}
                </IconButton>
              </Tooltip>
            )}
            <Chip
              icon={getStatusIcon(result.status)}
              label={result.status.toUpperCase()}
              size="small"
              color={getStatusColor(result.status) as 'success' | 'warning' | 'error' | 'info'}
              variant={result.status === 'normal' ? 'outlined' : 'filled'}
            />
          </Box>
        }
        sx={{ pb: 1 }}
      />
      
      <CardContent sx={{ pt: 0, flex: 1 }}>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          {/* Result Value */}
          <Box sx={{ textAlign: 'center' }}>
            <Typography
              variant="h4"
              component="div"
              sx={{
                fontWeight: 700,
                fontFamily: 'monospace',
                color: `${getStatusColor(result.status)}.main`,
              }}
            >
              {result.value}
            </Typography>
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{ fontFamily: 'monospace' }}
            >
              {result.unit}
            </Typography>
          </Box>
          
          {!compact && (
            <>
              {/* Reference Range Visualization */}
              {typeof result.value === 'number' && result.referenceRange.min && result.referenceRange.max && (
                <Box>
                  <Typography variant="caption" color="text.secondary" gutterBottom>
                    Reference Range
                  </Typography>
                  <LinearProgress
                    variant="determinate"
                    value={getProgressValue()}
                    sx={{
                      height: 8,
                      borderRadius: 4,
                      backgroundColor: 'grey.300',
                      '& .MuiLinearProgress-bar': {
                        backgroundColor: `${getStatusColor(result.status)}.main`,
                        borderRadius: 4,
                      },
                    }}
                  />
                  <Typography
                    variant="caption"
                    color="text.secondary"
                    sx={{ mt: 0.5, display: 'block' }}
                  >
                    {getReferenceRangeText()}
                  </Typography>
                </Box>
              )}
              
              <Divider />
              
              {/* Additional Information */}
              {result.notes && (
                <Typography
                  variant="body2"
                  color="text.secondary"
                  sx={{
                    fontStyle: 'italic',
                    backgroundColor: 'grey.50',
                    p: 1,
                    borderRadius: 1,
                  }}
                >
                  {result.notes}
                </Typography>
              )}
            </>
          )}
        </Box>
      </CardContent>
    </Card>
  );
};

export default LabResultCard;