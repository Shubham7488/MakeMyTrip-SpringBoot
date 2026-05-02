import React, { useState, useEffect } from 'react';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { AlertCircle } from 'lucide-react';
import RefundCard from './RefundCard';

interface RefundsListProps {
  userId?: string;
  refreshTrigger?: number;
}

const RefundsList: React.FC<RefundsListProps> = ({ userId, refreshTrigger = 0 }) => {
  const [refunds, setRefunds] = useState<any[]>([]);
  const [filteredRefunds, setFilteredRefunds] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [sortBy, setSortBy] = useState<string>('recent');
  const [currentPage, setCurrentPage] = useState(1);

  const itemsPerPage = 5;

  const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8082';
  const DEV_PREVIEW = process.env.NEXT_PUBLIC_DEV_PREVIEW === 'true';
  const DEV_PREVIEW_USER = process.env.NEXT_PUBLIC_DEV_PREVIEW_USER || '69f4e9d778651b21c2f97922';
  const effectiveUserId = userId || (DEV_PREVIEW ? DEV_PREVIEW_USER : '');

  // ✅ FETCH REFUNDS
  const fetchRefunds = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await fetch(`${BASE_URL}/api/refund/user/${effectiveUserId}`);
      const data = await res.json();

      console.log("Refund API Response:", data); // 🔥 DEBUG

      if (data && data.success && Array.isArray(data.refunds)) {
        // normalize ids and fields (handle different possible id names)
        const normalized = data.refunds.map((r: any) => ({
          ...r,
          refundId: r.refundId || r.id || r._id,
          status: r.status ? String(r.status).toUpperCase() : 'PENDING',
          refundAmount: (r.refundAmount ?? r.refund_amount ?? r.refund) || 0,
          originalAmount: (r.originalAmount ?? r.original_amount ?? r.amount) || 0,
          createdDate: r.createdDate || r.created_date || r.createdAt || r.created_at || new Date().toISOString(),
        }));
        setRefunds(normalized);
      } else {
        setRefunds([]);
        setError('No refunds found');
      }
    } catch (err) {
      console.error("Error fetching refunds:", err);
      setError('Server error while fetching refunds');
    } finally {
      setLoading(false);
    }
  };

  // ✅ AUTO FETCH
  useEffect(() => {
    if (effectiveUserId) {
      fetchRefunds();
    }
  }, [effectiveUserId, refreshTrigger]); // 🔥 IMPORTANT

  // ✅ FILTER + SORT
  useEffect(() => {
    let data = [...refunds];

    if (statusFilter !== 'ALL') {
      data = data.filter(r => r.status === statusFilter);
    }

    if (sortBy === 'recent') {
      data.sort((a, b) =>
        new Date(b.createdDate).getTime() - new Date(a.createdDate).getTime()
      );
    } else if (sortBy === 'amount-high') {
      data.sort((a, b) => (b.refundAmount || 0) - (a.refundAmount || 0));
    } else if (sortBy === 'amount-low') {
      data.sort((a, b) => (a.refundAmount || 0) - (b.refundAmount || 0));
    }

    setFilteredRefunds(data);
    setCurrentPage(1);
  }, [refunds, statusFilter, sortBy]);

  // ✅ DELETE (ADMIN)
  const handleDelete = async (refundId: string) => {
    if (!window.confirm('Delete this refund?')) return;

    try {
      const res = await fetch(`${BASE_URL}/api/refund/${refundId}/admin/delete`, {
        method: 'DELETE'
      });

      const data = await res.json();

      if (data.success) {
        // refresh list to ensure consistency
        await fetchRefunds();
      } else {
        alert('Delete failed');
      }
    } catch (err) {
      alert('Error deleting refund');
    }
  };

  // ✅ PAGINATION
  const totalPages = Math.ceil(filteredRefunds.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedRefunds = filteredRefunds.slice(startIndex, startIndex + itemsPerPage);

  // ✅ STATS FIX (IMPORTANT)
  const stats = {
    total: refunds.length,
    pending: refunds.filter(r => String(r.status).toUpperCase() === 'PENDING').length,
    processed: refunds.filter(r => String(r.status).toUpperCase() === 'PROCESSED').length,
    completed: refunds.filter(r => String(r.status).toUpperCase() === 'COMPLETED').length,
    // total refunded amount (sum of refundAmount)
    totalAmount: refunds.reduce((sum, r) => sum + (Number(r.refundAmount) || 0), 0)
  };

  // ✅ LOADING
  if (loading) {
    return (
      <div className="text-center py-8">
        <p>Loading refunds...</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">

      {/* ERROR */}
      {error && (
        <div className="bg-red-50 border p-3 rounded">
          <AlertCircle className="inline mr-2" />
          {error}
        </div>
      )}

      {/* STATS */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
        <Card className="p-3">
          Total: {stats.total}
        </Card>
        <Card className="p-3">
          Pending: {stats.pending}
        </Card>
        <Card className="p-3">
          Processed: {stats.processed}
        </Card>
        <Card className="p-3">
          Completed: {stats.completed}
        </Card>
        <Card className="p-3">
          ₹{stats.totalAmount.toFixed(2)}
        </Card>
      </div>

      {/* CONTROLS */}
      <div className="flex gap-3">

        <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="ALL">All</option>
          <option value="PENDING">Pending</option>
          <option value="PROCESSED">Processed</option>
          <option value="COMPLETED">Completed</option>
        </select>

        <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
          <option value="recent">Recent</option>
          <option value="amount-high">High</option>
          <option value="amount-low">Low</option>
        </select>

        <Button onClick={fetchRefunds}>Refresh</Button>
      </div>

      {/* LIST */}
      {paginatedRefunds.length === 0 ? (
        <p>No refunds found</p>
      ) : (
        paginatedRefunds.map((refund) => {
          const id = refund.refundId || refund.id || refund._id;
          return (
            <RefundCard
              key={id}
              refund={refund}
              onDelete={() => handleDelete(id)}
            />
          );
        })
      )}

      {/* PAGINATION */}
      {totalPages > 1 && (
        <div className="flex justify-between">
          <Button
            onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
            disabled={currentPage === 1}
          >
            Prev
          </Button>

          <span>{currentPage} / {totalPages}</span>

          <Button
            onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
            disabled={currentPage === totalPages}
          >
            Next
          </Button>
        </div>
      )}
    </div>
  );
};

export default RefundsList;