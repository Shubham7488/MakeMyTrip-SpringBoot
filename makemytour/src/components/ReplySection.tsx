import React, { useEffect, useState } from 'react';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8082';

interface Reply {
  replyId: string;
  reviewId: string;
  userId: string;
  text: string;
  createdDate: string;
}

interface Props {
  reviewId: string;
  userId: string;
}

const ReplySection: React.FC<Props> = ({ reviewId, userId }) => {
  const [replies, setReplies] = useState<Reply[]>([]);
  const [text, setText] = useState('');

  const fetchReplies = async () => {
    try {
      const res = await fetch(`${BASE_URL}/api/reply/review/${reviewId}`);
      const data = await res.json();
      if (data && data.success) setReplies(data.replies || []);
    } catch (e) { }
  };

  useEffect(()=>{ fetchReplies(); }, [reviewId]);

  const submit = async () => {
    try {
      const body = { reviewId, userId, text };
      const res = await fetch(`${BASE_URL}/api/review/reply/add`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
      });
      const data = await res.json();
      if (data.success) { setText(''); fetchReplies(); }
    } catch (e) {}
  };

  return (
    <div className="mt-2">
      <div className="space-y-2">
        {replies.map(r => (
          <div key={r.replyId} className="p-2 border rounded">
            <div className="text-sm text-gray-600">{r.userId} • {new Date(r.createdDate).toLocaleString()}</div>
            <div>{r.text}</div>
          </div>
        ))}
      </div>
      <div className="mt-2">
        <textarea value={text} onChange={(e)=>setText(e.target.value)} className="w-full p-2 border rounded" placeholder="Write a reply" />
        <button onClick={submit} className="mt-2 px-3 py-1 bg-green-600 text-white rounded">Reply</button>
      </div>
    </div>
  );
};

export default ReplySection;
