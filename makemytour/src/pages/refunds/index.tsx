import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import RefundsList from '@/components/RefundsList';
import { Card } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { AlertCircle, TrendingUp } from 'lucide-react';

/**
 * RefundPage Component
 * Displays user's refund dashboard with tracking and history
 */
export default function RefundPage() {
  // 🔹 Handle flexible redux structure safely
  const reduxUser = useSelector((state: any) => state.user);
  const user = reduxUser?.user || reduxUser;

  const [activeTab, setActiveTab] = useState('refunds');
  const [statistics, setStatistics] = useState<any>(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  // 🔹 Fetch statistics only for admin users
  useEffect(() => {
    if (user?.id && user?.role === 'ADMIN') {
      fetchStatistics();
    }
  }, [user?.id, user?.role]);

  const fetchStatistics = async () => {
    try {
      const response = await fetch('/api/refund/admin/statistics');
      const data = await response.json();

      if (data?.success) {
        setStatistics(data.statistics);
      }
    } catch (err) {
      console.error('Error fetching statistics:', err);
    }
  };

  // 🔹 Correct login validation
  if (!user || !user.id) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <AlertCircle className="w-12 h-12 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-600">Please log in to view refunds</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-8">
      <div className="max-w-6xl mx-auto px-4">

        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900">Refund Dashboard</h1>
          <p className="text-gray-600 mt-2">
            Track and manage your booking refunds
          </p>
        </div>

        {/* Admin Statistics */}
        {statistics && user.role === 'ADMIN' && (
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
            <Card className="p-4 bg-white border-l-4 border-yellow-500">
              <p className="text-sm text-gray-600">⏳ Pending</p>
              <p className="text-2xl font-bold mt-2">{statistics.pending}</p>
            </Card>

            <Card className="p-4 bg-white border-l-4 border-blue-500">
              <p className="text-sm text-gray-600">⚙️ Processed</p>
              <p className="text-2xl font-bold mt-2">{statistics.processed}</p>
            </Card>

            <Card className="p-4 bg-white border-l-4 border-green-500">
              <p className="text-sm text-gray-600">✅ Completed</p>
              <p className="text-2xl font-bold mt-2">{statistics.completed}</p>
            </Card>

            <Card className="p-4 bg-white border-l-4 border-purple-500">
              <p className="text-sm text-gray-600 flex items-center gap-2">
                <TrendingUp className="w-4 h-4" /> Total Amount
              </p>
              <p className="text-2xl font-bold mt-2">
                ₹{statistics.totalRefundAmount?.toFixed(0)}
              </p>
            </Card>
          </div>
        )}

        {/* Main Content */}
        <Card className="bg-white border-gray-200">
          <Tabs value={activeTab} onValueChange={setActiveTab}>
            <div className="border-b px-6 pt-6">
              <TabsList className="bg-transparent">
                <TabsTrigger value="refunds">
                  My Refunds
                </TabsTrigger>
                <TabsTrigger value="info">
                  Refund Information
                </TabsTrigger>
              </TabsList>
            </div>

            <TabsContent value="refunds" className="p-6">
              <RefundsList
                userId={user.id}
                refreshTrigger={refreshTrigger}
              />
            </TabsContent>

            <TabsContent value="info" className="p-6">
              <p className="text-gray-700 text-sm">
                Refunds are processed based on cancellation policy.
                You can track the status of each refund in real time.
              </p>
            </TabsContent>
          </Tabs>
        </Card>
      </div>
    </div>
  );
}
