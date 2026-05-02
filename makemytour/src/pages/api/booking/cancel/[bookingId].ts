import type { NextApiRequest, NextApiResponse } from 'next';
import DataStore from '@/lib/dataStore';

export default function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  // bookingId comes from URL: /api/booking/cancel/[bookingId]
  const { bookingId } = req.query;

  // other values come from query params
  const {
    userId,
    cancellationReasonId,
    additionalNotes,
    bookingType,
    originalAmount,
  } = req.query;

  const bookingIdParam = bookingId as string;
  const userIdParam = userId as string;

  if (!bookingIdParam || !userIdParam) {
    return res.status(400).json({ error: 'Missing bookingId or userId' });
  }

  // Mock cancellation processing
  const refundPercentage = 50;

  const originalAmtNumber = originalAmount
    ? Number(originalAmount)
    : 5000;

  const refundAmount = (originalAmtNumber * refundPercentage) / 100;
  const refundId = `REF-${Date.now()}`;

  // Create refund record
  const newRefund = {
    refundId,
    userId: userIdParam,
    bookingId: bookingIdParam,
    bookingType: bookingType || 'FLIGHT',
    originalAmount: originalAmtNumber,
    refundAmount,
    refundPercentage,
    status: 'PENDING',
    cancellationReason: cancellationReasonId || 'Customer Request',
    adminNotes: additionalNotes || '',
    createdDate: new Date().toISOString(),
    processedDate: null,
    completedDate: null,
  };

  // Store refund using DataStore
  DataStore.addRefund(newRefund);
  DataStore.removeBooking(bookingIdParam);

  res.setHeader('Content-Type', 'application/json');
  res.status(200).json({
    success: true,
    refundId,
    refundAmount,
    refundPercentage,
    refund: newRefund,
    message:
      'Booking cancelled successfully. Refund will be processed within 5 business days.',
  });
}
