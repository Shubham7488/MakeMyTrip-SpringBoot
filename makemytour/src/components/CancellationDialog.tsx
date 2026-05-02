import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  CheckCircle,
  XCircle,
  Loader2,
  MapPin,
  Calendar,
  DollarSign,
  ArrowRight
} from 'lucide-react';
import axios from 'axios';

// Axios instance
const api = axios.create({
  timeout: 5000
});

interface CancellationDialogProps {
  isOpen: boolean;
  onClose: () => void;
  booking: any;
  userId: string;
  onCancellationSuccess: () => void;
}

interface CancellationReason {
  _id: string;
  reasonName: string;
  description: string;
  active: boolean;
}

// Default fallback reasons
const DEFAULT_REASONS: CancellationReason[] = [
  { _id: '1', reasonName: 'Change of Plans', description: '', active: true },
  { _id: '2', reasonName: 'Found Better Price', description: '', active: true },
  { _id: '3', reasonName: 'Schedule Conflict', description: '', active: true },
  { _id: '4', reasonName: 'Medical Emergency', description: '', active: true },
  { _id: '5', reasonName: 'Family Issue', description: '', active: true },
  { _id: '6', reasonName: 'Financial Reasons', description: '', active: true },
  { _id: '7', reasonName: 'Other', description: '', active: true }
];

const CancellationDialog: React.FC<CancellationDialogProps> = ({
  isOpen,
  onClose,
  booking,
  userId,
  onCancellationSuccess
}) => {
  const [selectedReason, setSelectedReason] = useState('');
  const [reasons, setReasons] = useState<CancellationReason[]>(DEFAULT_REASONS);
  const [reasonsLoading, setReasonsLoading] = useState(false);
  const [loading, setLoading] = useState(false);
  const [refundLoading, setRefundLoading] = useState(false);
  const [refundInfo, setRefundInfo] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [additionalNotes, setAdditionalNotes] = useState('');

  useEffect(() => {
    if (isOpen) {
      fetchCancellationReasons();
      calculateRefundLocally();
    } else {
      resetForm();
    }
  }, [isOpen]);

  const fetchCancellationReasons = async () => {
    setReasonsLoading(true);
    try {
      const res = await api.get('/api/booking/cancel/reasons');
      setReasons(res.data?.reasons || res.data || DEFAULT_REASONS);
    } catch {
      setReasons(DEFAULT_REASONS);
    } finally {
      setReasonsLoading(false);
    }
  };

  // 🔥 MAIN FIX: Dynamic refund calculation
  const calculateRefundLocally = () => {
    setRefundLoading(true);

    const bookingDate = new Date(booking?.date);
    const now = new Date();

    const hoursDiff =
      (now.getTime() - bookingDate.getTime()) / (1000 * 60 * 60);

    let refundPercentage = 0;

    if (hoursDiff <= 6) refundPercentage = 90;
    else if (hoursDiff <= 12) refundPercentage = 75;
    else if (hoursDiff <= 24) refundPercentage = 50;
    else refundPercentage = 0;

    const refundAmount =
      (booking?.totalPrice || 0) * (refundPercentage / 100);

    const refundDate = new Date();
    refundDate.setDate(refundDate.getDate() + 5);

    setRefundInfo({
      eligible: refundPercentage > 0,
      refundPercentage,
      refundAmount,
      refundDate
    });

    setRefundLoading(false);
  };

  const handleCancellation = async () => {
    if (!selectedReason) {
      setError('Please select a cancellation reason');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const bookingId = booking?.id || booking?.bookingId || booking?._id;
      const url = `/api/booking/cancel/${bookingId}?userId=${userId}&cancellationReasonId=${selectedReason}&adminNotes=${encodeURIComponent(
        additionalNotes || ''
      )}`;

      const res = await api.post(url);

      if (res.data?.success) {
        setSuccess(true);
        setTimeout(() => {
          onCancellationSuccess();
          onClose();
          resetForm();
        }, 2000);
      } else {
        setError('Cancellation failed');
      }
    } catch (err: any) {
      setError(err.response?.data?.error || 'Cancellation failed');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setSelectedReason('');
    setAdditionalNotes('');
    setError(null);
    setSuccess(false);
  };

  if (!booking) return null;

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl bg-white">
        <DialogHeader>
          <DialogTitle>Cancel Booking</DialogTitle>
          <DialogDescription>
            Booking ID: {(booking?.id || booking?.bookingId)?.slice(0, 12)}
          </DialogDescription>
        </DialogHeader>

        {/* Refund Info */}
        {refundLoading ? (
          <div className="flex items-center gap-2">
            <Loader2 className="animate-spin" /> Calculating refund...
          </div>
        ) : (
          refundInfo && (
            <div className="p-4 border rounded-lg bg-green-50">
              <p className="font-bold">
                {refundInfo.refundPercentage > 0
                  ? `${refundInfo.refundPercentage}% Refund Eligible`
                  : 'No Refund Available'}
              </p>
              <p>
                Refund Amount: $
                {refundInfo.refundAmount.toFixed(2)}
              </p>
              <p className="text-sm">
                📅 Expected refund date:{' '}
                {refundInfo.refundDate.toLocaleDateString()}
              </p>
            </div>
          )
        )}

        {/* Reason */}
        <Label>Cancellation Reason</Label>
        <select
          value={selectedReason}
          onChange={(e) => setSelectedReason(e.target.value)}
          className="w-full border p-2 rounded"
        >
          <option value="">Select reason</option>
          {reasons.map((r) => (
            <option key={r._id} value={r._id}>
              {r.reasonName}
            </option>
          ))}
        </select>

        <Textarea
          placeholder="Additional notes (optional)"
          value={additionalNotes}
          onChange={(e) => setAdditionalNotes(e.target.value)}
        />

        {error && <p className="text-red-600">{error}</p>}

        <div className="flex justify-end gap-2 mt-4">
          <Button variant="outline" onClick={onClose}>
            Close
          </Button>
          <Button
            onClick={handleCancellation}
            disabled={loading || !selectedReason}
          >
            {loading ? 'Processing...' : 'Confirm Cancellation'}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default CancellationDialog;
