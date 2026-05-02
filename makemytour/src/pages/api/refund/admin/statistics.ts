import { NextApiRequest, NextApiResponse } from 'next';
import DataStore from '@/lib/dataStore';

export default function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method === 'GET') {
    const statistics = DataStore.getStatistics();

    return res.status(200).json({
      success: true,
      statistics,
    });
  }

  return res.status(405).json({ error: 'Method not allowed' });
}
