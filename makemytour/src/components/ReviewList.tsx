"use client";

import React, { useState, useEffect } from 'react';
import ReviewCard from './ReviewCard';
import { getReviews } from '../api/review';

interface Review {
  reviewId: string;
  userId: string;
  reviewText: string;
  rating: number;
  createdDate: string;
  likes: number;
  images: string[];
}

interface Props {
  userId: string;
  targetType: string;
  targetId: string;
  bookingId?: string;
}

const ReviewList: React.FC<Props> = ({ userId, targetType, targetId, bookingId }) => {
  const [reviews, setReviews] = useState<Review[]>([]);

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        const data = await getReviews(targetType, targetId);
        setReviews(data);
      } catch (error) {
        console.error('Error fetching reviews:', error);
      }
    };
    fetchReviews();
  }, [targetType, targetId]);

  return (
    <div className="space-y-4">
      {reviews.length === 0 ? (
        <p className="text-gray-500 text-sm">No reviews yet. Be the first to review!</p>
      ) : (
        reviews.map((review) => (
          <ReviewCard 
            key={review.reviewId} 
            review={review} 
            userId={userId}
            onLike={(id) => console.log('like', id)}
            onFlag={(id) => console.log('flag', id)}
          />
        ))
      )}
    </div>
  );
};

export default ReviewList;

