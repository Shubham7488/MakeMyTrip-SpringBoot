import React from 'react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Trash2, Eye } from 'lucide-react';

interface RefundCardProps {
  refund: any;
  onView?: () => void;
  onDelete?: () => void;
}

/**
 * RefundCard Component
 * Displays individual refund information in a card format
 */
export const RefundCard: React.FC<RefundCardProps> = ({ refund, onView, onDelete }) => {
  const getStatusBadgeColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'PROCESSED':
        return 'bg-blue-100 text-blue-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING':
        return '⏳';
      case 'PROCESSED':
        return '⚙️';
      case 'COMPLETED':
        return '✅';
      default:
        return '❓';
    }
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch {
      return 'Invalid Date';
    }
  };

  return (
    <Card className="p-4 border border-gray-200 bg-white hover:shadow-md transition-shadow">
      <div className="space-y-3">
        {/* Header */}
        <div className="flex justify-between items-start">
          <div>
            <p className="text-sm font-semibold text-gray-900">
              Refund ID: {refund?.refundId?.substring(0, 12)}...
            </p>
            <p className="text-xs text-gray-500 mt-1">
              Booking ID: {refund?.bookingId?.substring(0, 12)}...
            </p>
          </div>
          <Badge className={getStatusBadgeColor(refund?.status)}>
            {getStatusIcon(refund?.status)} {refund?.status}
          </Badge>
        </div>

        {/* Amount Section */}
        <div className="grid grid-cols-2 gap-4 bg-gray-50 p-3 rounded-lg">
          <div>
            <p className="text-xs text-gray-600">Original Amount</p>
            <p className="text-sm font-semibold text-gray-900">
              ${refund?.originalAmount?.toFixed(2)}
            </p>
          </div>
          <div>
            <p className="text-xs text-gray-600">Refund Amount</p>
            <p className="text-sm font-semibold text-green-600">
              ${refund?.refundAmount?.toFixed(2)}
            </p>
          </div>
        </div>

        {/* Refund Percentage */}
        <div className="flex items-center justify-between">
          <span className="text-sm text-gray-600">Refund Percentage:</span>
          <Badge className="bg-blue-100 text-blue-800">{refund?.refundPercentage}%</Badge>
        </div>

        {/* Dates */}
        <div className="space-y-2 text-xs text-gray-600 border-t border-gray-200 pt-3">
          <div className="flex justify-between">
            <span>Created:</span>
            <span className="font-medium text-gray-900">{formatDate(refund?.createdDate)}</span>
          </div>
          {refund?.processedDate && (
            <div className="flex justify-between">
              <span>Processed:</span>
              <span className="font-medium text-gray-900">{formatDate(refund?.processedDate)}</span>
            </div>
          )}
          {refund?.completedDate && (
            <div className="flex justify-between">
              <span>Completed:</span>
              <span className="font-medium text-gray-900">{formatDate(refund?.completedDate)}</span>
            </div>
          )}
        </div>

        {/* Reason */}
        {refund?.cancellationReason && (
          <div className="bg-gray-50 p-2 rounded-lg">
            <p className="text-xs text-gray-600">Reason</p>
            <p className="text-sm text-gray-900 font-medium">{refund?.cancellationReason}</p>
          </div>
        )}

        {/* Admin Notes */}
        {refund?.adminNotes && (
          <div className="bg-blue-50 p-2 rounded-lg border border-blue-200">
            <p className="text-xs text-blue-700 font-medium">Admin Notes</p>
            <p className="text-xs text-blue-900 mt-1">{refund?.adminNotes}</p>
          </div>
        )}

        {/* Actions */}
        <div className="flex gap-2 border-t border-gray-200 pt-3">
          {onView && (
            <Button
              variant="outline"
              size="sm"
              onClick={onView}
              className="flex-1"
            >
              <Eye className="w-4 h-4 mr-1" />
              View Details
            </Button>
          )}
          {onDelete && (
            <Button
              variant="outline"
              size="sm"
              onClick={onDelete}
              className="text-red-600 hover:text-red-700 hover:bg-red-50 flex-1"
            >
              <Trash2 className="w-4 h-4 mr-1" />
              Delete
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
};

export default RefundCard;
