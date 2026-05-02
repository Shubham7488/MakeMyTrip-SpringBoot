// Shared data store for mock API endpoints
// In production, this would be a real database

export class DataStore {
  static refunds: any[] = [];
  static bookings: any[] = [];

  static addRefund(refund: any) {
    this.refunds.push(refund);
  }

  static getRefundsForUser(userId: string) {
    return this.refunds.filter(r => r.userId === userId);
  }

  static getAllRefunds() {
    return this.refunds;
  }

  static addBooking(booking: any) {
    this.bookings.push(booking);
  }

  static getBookingsForUser(userId: string) {
    return this.bookings.filter(b => b.userId === userId);
  }

  static removeBooking(bookingId: string) {
    this.bookings = this.bookings.filter(b => b.id !== bookingId);
  }

  static getStatistics() {
    const pending = this.refunds.filter(r => r.status === 'PENDING').length;
    const processed = this.refunds.filter(r => r.status === 'PROCESSED').length;
    const completed = this.refunds.filter(r => r.status === 'COMPLETED').length;
    const totalAmount = this.refunds.reduce((sum, r) => sum + (r.refundAmount || 0), 0);

    return {
      totalRefunds: this.refunds.length,
      totalRefundAmount: totalAmount,
      pendingRefunds: pending,
      processedRefunds: processed,
      completedRefunds: completed,
      statusBreakdown: {
        PENDING: pending,
        PROCESSED: processed,
        COMPLETED: completed,
      },
    };
  }
}

export default DataStore;
