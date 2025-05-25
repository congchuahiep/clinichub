import { useState } from "react";
import { Form, Button, Alert, Stack, InputGroup } from "react-bootstrap";
import { authApis, endpoints } from "../configs/APIs";
import cookie from "react-cookies";

const StarRating = ({ rating, setRating }) => (
  <div>
    {[1, 2, 3, 4, 5].map(star => (
      <i
        key={star}
        className={`bi ${star <= rating ? "bi-star-fill text-warning" : "bi-star text-secondary"}`}
        style={{ fontSize: 16, cursor: "pointer" }}
        onClick={() => setRating(star)}
      // onMouseOver={() => setRating(star)}
      />
    ))}
  </div>
);

const ReviewForm = ({ doctorId, onSuccess }) => {
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess(false);

    if (!cookie.load("token")) {
      setError("Bạn cần đăng nhập để đánh giá.");
      setLoading(false);
      return;
    }

    try {
      await authApis().post(endpoints.review(doctorId), { rating, comment });
      setSuccess(true);
      setRating(0);
      setComment("");
      if (onSuccess) onSuccess();
    } catch (err) {
      setError("Gửi đánh giá thất bại!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Form onSubmit={handleSubmit}>
      <Stack direction="horizontal">
        <Form.Label>Đánh giá của bạn</Form.Label>
        <div className="mb-2">
          <StarRating rating={rating} setRating={setRating} />
        </div>
      </Stack>
      <InputGroup className="mb-3">
        <Form.Control
          placeholder="Recipient's username"
          aria-label="Recipient's username"
          aria-describedby="basic-addon2"
        />
      <Button type="submit" variant="primary" disabled={loading || rating === 0}>
        {loading ? "Đang gửi..." : "Gửi đánh giá"}
      </Button>
      </InputGroup>
      {error && <Alert variant="danger">{error}</Alert>}
      {success && <Alert variant="success">Đánh giá thành công!</Alert>}
    </Form>
  );
};

export default ReviewForm;