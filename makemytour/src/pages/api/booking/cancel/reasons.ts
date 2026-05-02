import type { NextApiRequest, NextApiResponse } from 'next';

export default function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method !== 'GET') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const reasons = [
    { _id: '1', reasonName: 'Change of Plans', description: 'My travel plans have changed', active: true },
    { _id: '2', reasonName: 'Found Better Price', description: 'Found a better deal elsewhere', active: true },
    { _id: '3', reasonName: 'Schedule Conflict', description: 'There is a conflict with my schedule', active: true },
    { _id: '4', reasonName: 'Medical Emergency', description: 'Medical emergency or health reasons', active: true },
    { _id: '5', reasonName: 'Family Issue', description: 'Family issues or urgent matters', active: true },
    { _id: '6', reasonName: 'Financial Reasons', description: 'Financial constraints', active: true },
    { _id: '7', reasonName: 'Other', description: 'Other reason not listed above', active: true },
  ];

  res.setHeader('Content-Type', 'application/json');
  res.status(200).json(reasons);
}
