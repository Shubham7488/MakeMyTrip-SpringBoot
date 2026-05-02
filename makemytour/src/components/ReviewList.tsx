import React, { useEffect, useState } from 'react';
import ReviewCard from './ReviewCard';
import ReviewForm from './ReviewForm';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8082';

interface Props {
  userId: string;
  targetType: 'FLIGHT'|'HOTEL';
  targetId: string;
  bookingId?: string;
}

const ReviewList: React.FC<Props> = ({ userId, targetType, targetId, bookingId }) => {
  const [reviews, setReviews] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string|null>(null);
  const [sortBy, setSortBy] = useState<'mostHelpful'|'newest'|'highest'|'default'>('default');
  const [filterRating, setFilterRating] = useState<number|null>(null);

  const fetchReviews = async () => {
    try {
      setLoading(true);
      const res = await fetch(`${BASE_URL}/api/review/target/${targetType}/${targetId}`);
      const data = await res.json();
      console.log('reviews api', data);
      if (data && data.success) setReviews(data.reviews || []);
      else setError('No reviews');
    } catch (e:any) { setError(e.message); }
    finally { setLoading(false); }
  };

  useEffect(()=>{ fetchReviews(); }, [targetId]);

  const handleLike = async (id:string) => {
    await fetch(`${BASE_URL}/api/review/like/${id}`, { method: 'POST' });
    fetchReviews();
  };
  const handleFlag = async (id:string) => {
    await fetch(`${BASE_URL}/api/review/flag/${id}`, { method: 'POST' });
    fetchReviews();
  };

  const filtered = reviews
    .filter(r => filterRating ? r.rating === filterRating : true)
    .sort((a,b)=>{
      if (sortBy==='newest') return new Date(b.createdDate).getTime() - new Date(a.createdDate).getTime();
      if (sortBy==='highest') return (b.rating||0)-(a.rating||0);
      if (sortBy==='mostHelpful') return (b.likes||0)-(a.likes||0);
      return 0;
    });

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex gap-2">
          <select onChange={(e)=>setSortBy(e.target.value as any)} value={sortBy} className="p-2 border rounded">
            <option value="default">Default</option>
            <option value="mostHelpful">Most Helpful</option>
            <option value="newest">Newest</option>
            <option value="highest">Highest Rated</option>
          </select>
          <select onChange={(e)=>setFilterRating(e.target.value?Number(e.target.value):null)} value={filterRating ?? ''} className="p-2 border rounded">
            <option value="">All ratings</option>
            <option value="5">5</option>
            <option value="4">4</option>
            <option value="3">3</option>
            <option value="2">2</option>
            <option value="1">1</option>
          </select>
        </div>
      </div>

      <ReviewForm userId={userId} bookingId={bookingId||''} targetType={targetType} targetId={targetId} onAdded={fetchReviews} />

      {loading ? <div>Loading...</div> : (
        filtered.length===0 ? <div>No reviews yet</div> : filtered.map(r=> (
          <ReviewCard key={r.reviewId||r.id} review={r} userId={userId} onLike={handleLike} onFlag={handleFlag} />
        ))
      )}
    </div>
  );
};

export default ReviewList;
