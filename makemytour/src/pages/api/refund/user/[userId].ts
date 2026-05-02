import { NextApiRequest, NextApiResponse } from 'next';
import DataStore from '@/lib/dataStore';

export default function handler(req: NextApiRequest, res: NextApiResponse) {
  const { userId } = req.query;

  if (!userId) {
    return res.status(400).json({ error: 'User ID is required' });
  }

  if (req.method === 'GET') {
    // Get refunds for a user
    const userRefunds = DataStore.getRefundsForUser(userId as string);
    
    return res.status(200).json({
      success: true,
      refunds: userRefunds,
      total: userRefunds.length,
    });
  }

  if (req.method === 'POST') {
    // Create a new refund
    const { bookingId, bookingType, originalAmount, refundAmount, status, reason, createdDate } = req.body;

    const newRefund = {
      refundId: `REF-${Date.now()}`,
      userId,
      bookingId,
      bookingType,
      originalAmount,
      refundAmount,
      refundPercentage: (refundAmount / originalAmount) * 100,
      status: status || 'PENDING',
      cancellationReason: reason || 'Customer Request',
      adminNotes: '',
      createdDate: createdDate || new Date().toISOString(),
      processedDate: status === 'PROCESSED' ? new Date().toISOString() : null,
      completedDate: status === 'COMPLETED' ? new Date().toISOString() : null,
    };

    DataStore.addRefund(newRefund);
    
    return res.status(201).json({
      success: true,
      refund: newRefund,
    });
  }

  return res.status(405).json({ error: 'Method not allowed' });
}
