import React from 'react';
import RatingStars from './RatingStars';
import ReplySection from './ReplySection';

interface Props {
  review: any;
  userId: string;
  onLike?: (id:string)=>void;
  onFlag?: (id:string)=>void;
}

const ReviewCard: React.FC<Props> = ({ review, userId, onLike, onFlag }) => {
  return (
    <div className="p-4 border rounded">
      <div className="flex justify-between items-start">
        <div>
          <div className="font-semibold">{review.userId}</div>
          <div className="text-sm text-gray-600">{new Date(review.createdDate).toLocaleString()}</div>
        </div>
        <div>
          <RatingStars value={review.rating || 0} />
        </div>
      </div>
      <div className="mt-2">{review.reviewText}</div>
      {review.images && review.images.length>0 && (
        <div className="mt-2 grid grid-cols-3 gap-2">
          {review.images.map((img:string,i:number)=>(<img key={i} src={img} className="w-full h-24 object-cover rounded" alt="review-img"/>))}
        </div>
      )}
      <div className="mt-2 flex gap-2">
        <button onClick={()=>onLike && onLike(review.reviewId)} className="text-sm">Helpful ({review.likes||0})</button>
        <button onClick={()=>onFlag && onFlag(review.reviewId)} className="text-sm text-red-600">Flag</button>
      </div>
      <ReplySection reviewId={review.reviewId} userId={userId} />
    </div>
  );
};

export default ReviewCard;
