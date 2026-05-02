import React from 'react';

interface RatingStarsProps {
  value: number;
  editable?: boolean;
  onChange?: (v: number) => void;
}

const RatingStars: React.FC<RatingStarsProps> = ({ value, editable = false, onChange }) => {
  const stars = [1, 2, 3, 4, 5];
  return (
    <div className="flex items-center">
      {stars.map((s) => (
        <button
          key={s}
          onClick={() => editable && onChange && onChange(s)}
          className={`px-1 ${s <= value ? 'text-yellow-400' : 'text-gray-300'}`}
        >
          ★
        </button>
      ))}
    </div>
  );
};

export default RatingStars;
