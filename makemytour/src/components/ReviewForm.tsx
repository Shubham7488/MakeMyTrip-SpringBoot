"use client";

import React, { useState } from 'react';
import RatingStars from './RatingStars';
import { addReview } from '../api/review';

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
      const data = await addReview({ userId, bookingUniqueId: bookingId, targetType, targetId, rating, reviewText: text, images });
      setText('');
      setRating(5);
      setImages([]);
      onAdded?.();
    } catch (e: any) {
      console.error('Review submit error:', e);
      setError(e.message || 'Error submitting review');
    } finally { 
      setLoading(false); 
    }
  };

  return (
    <div className="p-4 border rounded-lg bg-white">
      <h3 className="font-semibold mb-3">Write a review</h3>
      <RatingStars value={rating} editable onChange={setRating} />
      <textarea 
        value={text} 
        onChange={(e) => setText(e.target.value)} 
        className="w-full p-3 mt-3 border rounded-lg resize-vertical min-h-[100px] focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
        placeholder="Share your honest experience about your stay..." 
      />
      <div className="mt-3 text-sm text-gray-500">
        Image URLs (comma separated)
      </div>
      <input 
        value={images.join(', ')} 
        onChange={(e)=>setImages(e.target.value.split(',').map(s=>s.trim()).filter(Boolean))} 
        placeholder="https://example.com/img1.jpg, https://example.com/img2.jpg" 
        className="w-full p-3 mt-1 border rounded-lg" 
      />
      {error && <div className="text-red-500 text-sm mt-2 p-2 bg-red-50 rounded">{error}</div>}
      <div className="mt-4">
        <button 
          onClick={submit} 
          disabled={loading || !text.trim()} 
          className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
        >
          {loading ? 'Submitting...' : 'Submit Review'}
        </button>
      </div>
    </div>
  );
};

export default ReviewForm;

