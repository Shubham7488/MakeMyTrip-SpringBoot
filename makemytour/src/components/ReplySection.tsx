"use client";

import React, { useState } from 'react';

interface Props {
  reviewId: string;
  userId: string;
}

const ReplySection: React.FC<Props> = ({ reviewId, userId }) => {
  const [reply, setReply] = useState('');

  const handleReply = () => {
    // Handle reply submission
    console.log('Reply to', reviewId, ':', reply);
    setReply('');
  };

  return (
    <div className="mt-4 pt-4 border-t">
      <textarea
        value={reply}
        onChange={(e) => setReply(e.target.value)}
        placeholder="Write a reply..."
        className="w-full p-3 border rounded-lg resize-vertical min-h-[80px]"
        rows={3}
      />
      <button 
        onClick={handleReply}
        disabled={!reply.trim()}
        className="mt-2 px-4 py-2 bg-blue-600 text-white rounded-lg disabled:opacity-50"
      >
        Post Reply
      </button>
    </div>
  );
};

export default ReplySection;

