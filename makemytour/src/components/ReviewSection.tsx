"use client";

import React from 'react';
import ReviewForm from './ReviewForm';
import ReviewList from './ReviewList';

interface Props {
  userId: string;
  targetType: 'FLIGHT' | 'HOTEL';
  targetId: string;
  bookingId: string;
}

const ReviewSection: React.FC<Props> = ({ userId, targetType, targetId, bookingId }) => {
  const [refreshKey, setRefreshKey] = React.useState(0);

  const handleReviewAdded = () => {
    setRefreshKey(prev => prev + 1);
  };

  return (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-semibold mb-4">Write a Review</h3>
        <ReviewForm 
          userId={userId} 
          bookingId={bookingId} 
          targetType={targetType} 
          targetId={targetId}
          onAdded={handleReviewAdded}
        />
      </div>
      <div key={refreshKey}>
        <h3 className="text-lg font-semibold mb-4">Reviews</h3>
        <ReviewList 
          userId={userId} 
          targetType={targetType} 
          targetId={targetId} 
          bookingId={bookingId} 
        />
      </div>
    </div>
  );
};

export default ReviewSection;

