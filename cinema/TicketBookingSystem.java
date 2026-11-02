package cinema;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TicketBookingSystem {

    private static final int TOTAL_SEATS = 50;

    private final boolean[] seats = new boolean[TOTAL_SEATS + 1];

    private int successfulBookings = 0;
    private int failedBookings = 0;

    public boolean bookSeat(int seatNumber, String customerName) {
        if (seatNumber < 1 || seatNumber > TOTAL_SEATS) {
            System.err.println("Ghe khong hop le: " + seatNumber);
            return false;
        }
        if (!seats[seatNumber]) {
            try { Thread.sleep(10); } catch (InterruptedException e) {}
            seats[seatNumber] = true;
            successfulBookings++;
            System.out.println("[Thanh cong] " + customerName + " da dat thanh cong ghe #" + seatNumber);
            return true;
        } else {
            failedBookings++;
            System.out.println("[That bai] " + customerName + " dat ghe #" + seatNumber + " nhung ghe DA DUOC DAT TRUOC!");
            return false;
        }
    }

    public int getSuccessfulBookings() { return successfulBookings; }
    public int getFailedBookings() { return failedBookings; }

    public static void main(String[] args) throws InterruptedException {
        TicketBookingSystem bookingSystem = new TicketBookingSystem();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        System.out.println("Bat dau mo phong 100 yeu cau dat ve dong thoi...");
        for (int i = 1; i <= 100; i++) {
            final int customerId = i;
            final int targetSeat = (i % 50) + 1;
            executor.submit(() -> {
                bookingSystem.bookSeat(targetSeat, "Khach hang #" + customerId);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("\n========= THONG KE TONG KET (UNSAFE RUN) =========");
        System.out.println("Tong so luot bao THANH CONG: " + bookingSystem.getSuccessfulBookings());
        System.out.println("Tong so luot bao THAT BAI: " + bookingSystem.getFailedBookings());
        System.out.println("Tong so yeu cau da xu ly: " + (bookingSystem.getSuccessfulBookings() + bookingSystem.getFailedBookings()));
        System.out.println("=================================================");
    }
}