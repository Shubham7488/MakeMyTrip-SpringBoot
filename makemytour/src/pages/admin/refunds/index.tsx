import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { AlertCircle, CheckCircle, TrendingUp, DownloadCloud } from 'lucide-react';
import axios from 'axios';

/**
 * AdminRefundDashboard Component
 * Admin panel for managing and monitoring all refunds
 */
export default function AdminRefundDashboard() {
  const [refunds, setRefunds] = useState<any[]>([]);
  const [statistics, setStatistics] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [selectedRefund, setSelectedRefund] = useState<any>(null);
  const [newStatus, setNewStatus] = useState('');
  const [adminNotes, setAdminNotes] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [sortBy, setSortBy] = useState('recent');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [refundsRes, statsRes] = await Promise.all([
        fetch('/api/refund/admin/all'),
        fetch('/api/refund/admin/statistics')
      ]);

      const refundsData = await refundsRes.json();
      const statsData = await statsRes.json();

      if (refundsData.success) setRefunds(refundsData.refunds);
      if (statsData.success) setStatistics(statsData.statistics);
    } catch (err) {
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusUpdate = async (refundId: string, status: string) => {
    try {
      const response = await fetch(`/api/refund/${refundId}/admin/process`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
          newStatus: status,
          adminNotes: adminNotes
        })
      });

      const data = await response.json();
      if (data.success) {
        setRefunds(refunds.map(r => r.refundId === refundId ? data.refund : r));
        setSelectedRefund(null);
        setAdminNotes('');
        alert('Refund status updated successfully');
        fetchData();
      }
    } catch (err) {
      console.error('Error updating refund:', err);
      alert('Error updating refund');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'PROCESSED': return 'bg-blue-100 text-blue-800';
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const filteredAndSortedRefunds = refunds
    .filter(r => filterStatus === 'ALL' || r.status === filterStatus)
    .sort((a, b) => {
      if (sortBy === 'recent') {
        return new Date(b.createdDate).getTime() - new Date(a.createdDate).getTime();
      } else if (sortBy === 'amount-high') {
        return b.refundAmount - a.refundAmount;
      } else if (sortBy === 'amount-low') {
        return a.refundAmount - b.refundAmount;
      }
      return 0;
    });

  const exportData = () => {
    const csv = [
      ['Refund ID', 'Booking ID', 'User ID', 'Amount', 'Status', 'Created Date'],
      ...refunds.map(r => [
        r.refundId,
        r.bookingId,
        r.userId,
        r.refundAmount,
        r.status,
        new Date(r.createdDate).toLocaleDateString()
      ])
    ]
      .map(row => row.join(','))
      .join('\n');

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `refunds-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
  };

  if (loading) {
    return <div className="flex items-center justify-center h-96">
      <div className="text-center">
        <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4"></div>
        <p className="text-gray-600">Loading admin dashboard...</p>
      </div>
    </div>;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-8">
      <div className="max-w-7xl mx-auto px-4">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900">Admin Refund Dashboard</h1>
          <p className="text-gray-600 mt-2">Manage and monitor all refund requests</p>
        </div>

        {/* Statistics Cards */}
        {statistics && (
          <div className="grid grid-cols-1 md:grid-cols-5 gap-4 mb-8">
            <Card className="p-4 bg-gradient-to-br from-blue-50 to-blue-100">
              <p className="text-sm text-blue-700">Total Refunds</p>
              <p className="text-3xl font-bold text-blue-900 mt-2">{statistics.total}</p>
            </Card>
            <Card className="p-4 bg-gradient-to-br from-yellow-50 to-yellow-100">
              <p className="text-sm text-yellow-700">Pending</p>
              <p className="text-3xl font-bold text-yellow-900 mt-2">{statistics.pending}</p>
            </Card>
            <Card className="p-4 bg-gradient-to-br from-purple-50 to-purple-100">
              <p className="text-sm text-purple-700">Processed</p>
              <p className="text-3xl font-bold text-purple-900 mt-2">{statistics.processed}</p>
            </Card>
            <Card className="p-4 bg-gradient-to-br from-green-50 to-green-100">
              <p className="text-sm text-green-700">Completed</p>
              <p className="text-3xl font-bold text-green-900 mt-2">{statistics.completed}</p>
            </Card>
            <Card className="p-4 bg-gradient-to-br from-red-50 to-red-100">
              <p className="text-sm text-red-700">Total Amount</p>
              <p className="text-3xl font-bold text-red-900 mt-2">
                ${statistics.totalRefundAmount?.toFixed(0)}
              </p>
            </Card>
          </div>
        )}

        {/* Controls and Export */}
        <div className="flex flex-col md:flex-row gap-4 mb-8 bg-white p-4 rounded-lg border border-gray-200">
          <div className="flex-1">
            <label className="text-sm font-semibold text-gray-700 block mb-2">Filter by Status</label>
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
            >
              <option value="ALL">All Statuses</option>
              <option value="PENDING">Pending</option>
              <option value="PROCESSED">Processed</option>
              <option value="COMPLETED">Completed</option>
            </select>
          </div>

          <div className="flex-1">
            <label className="text-sm font-semibold text-gray-700 block mb-2">Sort By</label>
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg"
            >
              <option value="recent">Most Recent</option>
              <option value="amount-high">Amount (High to Low)</option>
              <option value="amount-low">Amount (Low to High)</option>
            </select>
          </div>

          <div className="flex items-end">
            <Button onClick={exportData} className="bg-green-600 hover:bg-green-700 w-full">
              <DownloadCloud className="w-4 h-4 mr-2" />
              Export to CSV
            </Button>
          </div>
        </div>

        {/* Refunds Table */}
        <Card className="bg-white border-gray-200 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">Refund ID</th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">User ID</th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">Amount</th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">Percentage</th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">Status</th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">Reason</th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">Created</th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {filteredAndSortedRefunds.map((refund) => (
                  <tr key={refund.refundId} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm font-mono text-gray-900">
                      {refund.refundId.substring(0, 12)}...
                    </td>
                    <td className="px-6 py-4 text-sm font-mono text-gray-600">
                      {refund.userId.substring(0, 12)}...
                    </td>
                    <td className="px-6 py-4 text-sm font-semibold text-gray-900">
                      ${refund.refundAmount.toFixed(2)}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {refund.refundPercentage}%
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <Badge className={getStatusColor(refund.status)}>
                        {refund.status}
                      </Badge>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {refund.cancellationReason || 'N/A'}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {new Date(refund.createdDate).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <Button
                        size="sm"
                        onClick={() => setSelectedRefund(refund)}
                        variant="outline"
                      >
                        Update
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {filteredAndSortedRefunds.length === 0 && (
            <div className="text-center py-12">
              <AlertCircle className="w-12 h-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600">No refunds found</p>
            </div>
          )}
        </Card>

        {/* Update Modal */}
        {selectedRefund && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <Card className="w-full max-w-md bg-white">
              <div className="p-6">
                <h3 className="text-xl font-bold text-gray-900 mb-4">Update Refund Status</h3>

                <div className="space-y-4 mb-6">
                  <div>
                    <p className="text-sm text-gray-600">Refund ID</p>
                    <p className="font-mono text-sm mt-1 text-gray-900">{selectedRefund.refundId}</p>
                  </div>

                  <div>
                    <p className="text-sm text-gray-600">Current Status</p>
                    <p className="text-sm mt-1">
                      <Badge className={getStatusColor(selectedRefund.status)}>
                        {selectedRefund.status}
                      </Badge>
                    </p>
                  </div>

                  <div>
                    <label className="text-sm font-semibold text-gray-700 block mb-2">
                      New Status
                    </label>
                    <select
                      value={newStatus}
                      onChange={(e) => setNewStatus(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                    >
                      <option value="">Select status...</option>
                      <option value="PENDING">Pending</option>
                      <option value="PROCESSED">Processed</option>
                      <option value="COMPLETED">Completed</option>
                    </select>
                  </div>

                  <div>
                    <label className="text-sm font-semibold text-gray-700 block mb-2">
                      Admin Notes
                    </label>
                    <textarea
                      value={adminNotes}
                      onChange={(e) => setAdminNotes(e.target.value)}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                      rows={3}
                      placeholder="Add notes about this refund..."
                    />
                  </div>
                </div>

                <div className="flex gap-2">
                  <Button
                    onClick={() => setSelectedRefund(null)}
                    variant="outline"
                    className="flex-1"
                  >
                    Cancel
                  </Button>
                  <Button
                    onClick={() => handleStatusUpdate(selectedRefund.refundId, newStatus)}
                    disabled={!newStatus}
                    className="flex-1 bg-blue-600 hover:bg-blue-700"
                  >
                    <CheckCircle className="w-4 h-4 mr-2" />
                    Update
                  </Button>
                </div>
              </div>
            </Card>
          </div>
        )}
      </div>
    </div>
  );
}
