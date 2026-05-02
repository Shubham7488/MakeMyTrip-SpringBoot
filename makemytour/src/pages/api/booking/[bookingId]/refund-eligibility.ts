import type { NextApiRequest, NextApiResponse } from 'next';

export default function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method !== 'GET') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const { bookingId, userId } = req.query;

  if (!bookingId || !userId) {
    return res.status(400).json({ error: 'Missing bookingId or userId' });
  }

  // Mock refund calculation - 50% refund if booked within 24 hours
  const refundPercentage = 50;
  const mockBookingPrice = 5000; // Mock price
  const refundAmount = (mockBookingPrice * refundPercentage) / 100;

  res.setHeader('Content-Type', 'application/json');
  res.status(200).json({
    eligible: true,
    refundPercentage,
    refundAmount,
    daysBooked: 0,
    message: `You are eligible for ${refundPercentage}% refund`
  });
}
