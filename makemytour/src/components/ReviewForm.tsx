import React, { useState } from 'react';
import RatingStars from './RatingStars';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8082';

interface Props {
  userId: string;
  bookingId: string;
  targetType: 'FLIGHT'|'HOTEL';
  targetId: string;
  onAdded?: () => void;
}

const ReviewForm: React.FC<Props> = ({ userId, bookingId, targetType, targetId, onAdded }) => {
  const [rating, setRating] = useState(5);
  const [text, setText] = useState('');
  const [images, setImages] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const submit = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log('Submitting review - userId:', userId, 'bookingId:', bookingId, 'targetType:', targetType, 'targetId:', targetId);
      const body = { userId, bookingId, targetType, targetId, rating, reviewText: text, images };
      const res = await fetch(`${BASE_URL}/api/review/add`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
      const data = await res.json();
      if (data.success) {
        setText('');
        setImages([]);
        onAdded && onAdded();
      } else {
        setError(data.error || 'Failed');
      }
    } catch (e:any) {
      setError(e.message || 'Error');
    } finally { setLoading(false); }
  };

  return (
    <div className="p-4 border rounded">
      <h3 className="font-semibold">Write a review</h3>
      <RatingStars value={rating} editable onChange={setRating} />
      <textarea value={text} onChange={(e) => setText(e.target.value)} className="w-full p-2 mt-2 border rounded" placeholder="Share your experience" />
      {/* Image upload placeholder - expects URL list for now */}
      <input value={images.join(',')} onChange={(e)=>setImages(e.target.value.split(',').map(s=>s.trim()).filter(Boolean))} placeholder="Comma-separated image URLs" className="w-full mt-2 p-2 border rounded" />
      {error && <div className="text-red-500">{error}</div>}
      <div className="mt-2">
        <button onClick={submit} disabled={loading} className="px-4 py-2 bg-blue-600 text-white rounded">{loading? 'Saving...' : 'Submit'}</button>
      </div>
    </div>
  );
};

export default ReviewForm;
