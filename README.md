# ✈️ Flight & Hotel Booking System

A full-stack web application that allows users to search, book, cancel, and review flights and hotels. The platform also includes a refund tracking system and an interactive review & rating feature to enhance user experience.

---

## 🚀 Features

### 🧳 Booking System
- Search and book flights and hotels
- Real-time availability of seats and rooms
- Secure and smooth booking process

### ❌ Cancellation & Refund
- Cancel bookings with reason selection
- Automatic refund calculation based on policy
- Refund status tracking (Pending → Processed → Completed)

### 💰 Refund Tracker
- Visual timeline of refund progress
- Displays refund amount, percentage, and expected date
- User-friendly dashboard for tracking

### ⭐ Review & Rating System
- Rate flights and hotels (1–5 stars)
- Write detailed reviews
- Reply to other users’ reviews
- Sort and filter reviews (latest, highest rated, etc.)
- Flag inappropriate content for moderation

---

## 🛠️ Tech Stack

### Frontend
- React.js
- TypeScript
- Tailwind CSS
- Redux (State Management)

### Backend
- Spring Boot (Java)
- REST APIs

### Database
- MongoDB

---

## ⚙️ Installation & Setup

### 1 Backend Setup (Spring Boot)

.\run-backend.bat

Backend will run on:

http://localhost:8082


---

### 2 Frontend Setup (React)

cd makemytour
npm install
npm run dev

Frontend will run on:

http://localhost:3000



---

## 🔗 API Endpoints (Sample)

### Booking
- `POST /api/book/flight`
- `POST /api/book/hotel`

### Cancellation
- `POST /api/cancel`

### Refund
- `GET /api/refund/user/{userId}`
- `GET /api/refund/admin/statistics`

### Reviews
- `POST /api/reviews`
- `GET /api/reviews/hotel/{hotelId}`
- `GET /api/reviews/flight/{flightId}`

---

## 📊 Refund Policy

- **Within 24 hours of booking** → 50% refund  
- **After 24 hours** → No refund  

---

## 🧪 Key Functionalities Implemented

- Dynamic refund calculation based on travel date  
- Real-time UI updates after booking/cancellation  
- Error handling and API integration  
- Review validation to prevent duplicate submissions  

---

## 🎯 Future Enhancements

- Payment gateway integration  
- Email/SMS notifications  
- Admin dashboard for moderation  
- Advanced filtering and search  

---

## 👨‍💻 Author

Developed as part of an internship project.  
Focused on implementing real-world features with clean structure and maintainable code.

---

## 📌 Notes

- Ensure backend is running before starting frontend  
- Update API base URLs if running on different ports  

---


### Learn More

To learn more about Next.js, take a look at the following resources:

Next.js Documentation - learn about Next.js features and API.
Learn Next.js - an interactive Next.js tutorial.
You can check out the Next.js GitHub repository - your feedback and contributions are welcome!

### Deploy on render

The easiest way to deploy your Next.js app is to use the render Platform from the creators of Next.js.

Check out our Next.js deployment documentation for more details.
