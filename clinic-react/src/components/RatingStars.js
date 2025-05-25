/**
 * Hiển thị số sao đánh giá dựa trên avgRating (0.0 - 5.0)
 * @param {number} avgRating
 * @param {boolean} showText - Có hiển thị số điểm và "Chưa có đánh giá" không
 */
const RatingStars = ({ avgRating = 0, showText = true }) => {
  return (
    <div className="text-center">
      <div>
        {[...Array(5)].map((_, i) => {
          if (avgRating === 0) return <i key={i} className="bi bi-star text-warning"></i>;
          if (i < Math.floor(avgRating)) return <i key={i} className="bi bi-star-fill text-warning"></i>;
          if (i < avgRating) return <i key={i} className="bi bi-star-half text-warning"></i>;
          return <i key={i} className="bi bi-star text-warning"></i>;
        })}
      </div>
      <div>
        {showText &&
          (avgRating === 0
            ? <span className="text-muted">Chưa có đánh giá</span>
            : <span>{avgRating.toFixed(1)}/5</span>
          )}
      </div>
    </div>
  );
};

export default RatingStars;