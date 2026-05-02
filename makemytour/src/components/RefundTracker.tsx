import React from 'react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { CheckCircle2, Clock, AlertCircle } from 'lucide-react';

interface RefundTrackerProps {
  refund: any;
}

/**
 * RefundTracker Component
 * Displays the refund status timeline with visual indicators
 */
const RefundTracker: React.FC<RefundTrackerProps> = ({ refund }) => {
  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Clock className="w-5 h-5" />;
      case 'PROCESSED':
        return <AlertCircle className="w-5 h-5" />;
      case 'COMPLETED':
        return <CheckCircle2 className="w-5 h-5" />;
      default:
        return <Clock className="w-5 h-5" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800 border-yellow-300';
      case 'PROCESSED':
        return 'bg-blue-100 text-blue-800 border-blue-300';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800 border-green-300';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-300';
    }
  };

  const getStatusDescription = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'Your refund request is being processed';
      case 'PROCESSED':
        return 'Your refund has been approved and will be credited soon';
      case 'COMPLETED':
        return 'Your refund has been completed successfully';
      default:
        return 'Refund status unavailable';
    }
  };

  const stages = ['PENDING', 'PROCESSED', 'COMPLETED'];
  const currentStageIndex = stages.indexOf(refund?.status || 'PENDING');

  return (
    <Card className="p-4 border border-gray-200 bg-white">
      <div className="space-y-4">

        {/* Header */}
        <div>
          <p className="text-sm font-semibold text-gray-700">Refund Status</p>
          <p className="text-xs text-gray-500 mt-1">
            Refund ID: {refund?.refundId || 'N/A'}
          </p>
        </div>

        {/* Timeline */}
        <div className="relative">
          <div className="grid grid-cols-3 gap-4">
            {stages.map((stage, index) => (
              <div key={stage} className="flex flex-col items-center">
                <div
                  className={`w-10 h-10 rounded-full flex items-center justify-center mb-2 ${
                    index <= currentStageIndex
                      ? 'bg-blue-500 text-white'
                      : 'bg-gray-300 text-gray-600'
                  }`}
                >
                  {getStatusIcon(stage)}
                </div>

                <p className="text-xs font-semibold text-gray-700">{stage}</p>

                <p className="text-xs text-gray-500 mt-1">
                  {stage === 'PENDING' && refund?.createdDate &&
                    new Date(refund.createdDate).toLocaleDateString()}
                  {stage === 'PROCESSED' && refund?.processedDate &&
                    new Date(refund.processedDate).toLocaleDateString()}
                  {stage === 'COMPLETED' && refund?.completedDate &&
                    new Date(refund.completedDate).toLocaleDateString()}
                </p>
              </div>
            ))}
          </div>

          <div className="absolute top-5 left-0 right-0 h-0.5 bg-gray-300 -z-10" />
          <div
            className="absolute top-5 left-0 h-0.5 bg-blue-500 -z-10"
            style={{
              width: `${((currentStageIndex + 1) / stages.length) * 100}%`,
            }}
          />
        </div>

        {/* Current Status */}
        <div className={`p-3 rounded-lg border ${getStatusColor(refund?.status)}`}>
          <div className="flex items-center gap-2 mb-1">
            {getStatusIcon(refund?.status)}
            <p className="text-sm font-semibold">{refund?.status || 'UNKNOWN'}</p>
          </div>
          <p className="text-xs">{getStatusDescription(refund?.status)}</p>
        </div>

        {/* Refund Details */}
        <div className="space-y-2 border-t border-gray-200 pt-4">
          <div className="flex justify-between">
            <span className="text-sm text-gray-600">Original Amount:</span>
            <span className="text-sm font-semibold text-gray-900">
              ₹{refund?.originalAmount ? refund.originalAmount.toFixed(0) : '0'}
            </span>
          </div>

          <div className="flex justify-between">
            <span className="text-sm text-gray-600">Refund Percentage:</span>
            <Badge className="bg-blue-100 text-blue-800">
              {refund?.refundPercentage ?? 0}%
            </Badge>
          </div>

          <div className="flex justify-between">
            <span className="text-sm text-gray-600">Refund Amount:</span>
            <span className="text-sm font-semibold text-green-600">
              ₹{refund?.refundAmount ? refund.refundAmount.toFixed(0) : '0'}
            </span>
          </div>

          {refund?.expectedRefundDate && (
            <div className="flex justify-between">
              <span className="text-sm text-gray-600">Expected Date:</span>
              <span className="text-sm font-semibold text-gray-900">
                {new Date(refund.expectedRefundDate).toLocaleDateString()}
              </span>
            </div>
          )}
        </div>

        {/* Cancellation Reason */}
        {refund?.cancellationReason && (
          <div className="border-t border-gray-200 pt-4">
            <p className="text-sm text-gray-600 mb-1">Cancellation Reason:</p>
            <p className="text-sm font-semibold text-gray-900">
              {refund.cancellationReason}
            </p>
          </div>
        )}
      </div>
    </Card>
  );
};

export default RefundTracker;
