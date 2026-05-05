const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8082';

export async function getReviews(targetType: string, targetId: string) {
  const res = await fetch(`${BASE_URL}/api/review/target?type=${targetType}&id=${targetId}`);
  if (!res.ok) {
    console.error('Reviews API failed:', res.status, res.statusText);
    return [];
  }
  const data = await res.json();
  console.log('getReviews API response:', data);
  return Array.isArray(data) ? data : [];
}

export async function addReview(reviewData: any) {
  const res = await fetch(`${BASE_URL}/api/review/add`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reviewData),
  });
  if (!res.ok) {
    console.error('addReview failed:', res.status, res.statusText);
    throw new Error(`Failed to add review: ${res.status} ${res.statusText}`);
  }
  const data = await res.json();
  console.log('addReview response:', data);
  if (!data.success) throw new Error(data.error || 'Failed to add review');
  return data;
}
