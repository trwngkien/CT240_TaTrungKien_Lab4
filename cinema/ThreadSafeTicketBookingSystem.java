package cinema;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeTicketBookingSystem {

    private static final int TOTAL_SEATS = 50;
    private final boolean[] seats = new boolean[TOTAL_SEATS + 1];

    // 1. Khoa tuong minh bao ve mien toi han
    private final ReentrantLock lock = new ReentrantLock();

    // 2. Bien dem nguyen tu an toan luong
    private final AtomicInteger successfulBookings = new AtomicInteger(0);
    private final AtomicInteger failedBookings = new AtomicInteger(0);

    public boolean bookSeat(int seatNumber, String customerName) {
        if (seatNumber < 1 || seatNumber > TOTAL_SEATS) {
            System.err.println("Ghe khong hop le: " + seatNumber);
            return false;
        }

        // BAT DAU KHOA MIEN TOI HAN
        lock.lock();
        try {
            if (!seats[seatNumber]) {
                try { Thread.sleep(10); } catch (InterruptedException e) {}
                seats[seatNumber] = true;
                successfulBookings.incrementAndGet();
                System.out.println("[Thanh cong] " + customerName + " da dat thanh cong ghe #" + seatNumber);
                return true;
            } else {
                failedBookings.incrementAndGet();
                System.out.println("[That bai] " + customerName + " dat ghe #" + seatNumber + " nhung ghe DA DUOC DAT TRUOC!");
                return false;
            }
        } finally {
            lock.unlock(); // LUON GIAI PHONG KHOA
        }
    }

    public int getSuccessfulBookings() { return successfulBookings.get(); }
    public int getFailedBookings() { return failedBookings.get(); }

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeTicketBookingSystem bookingSystem = new ThreadSafeTicketBookingSystem();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        System.out.println("Bat dau mo phong 100 yeu cau dat ve dong thoi (THREAD-SAFE)...");

        for (int i = 1; i <= 100; i++) {
            final int customerId = i;
            final int targetSeat = (i % 50) + 1;
            executor.submit(() -> {
                bookingSystem.bookSeat(targetSeat, "Khach hang #" + customerId);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("\n========= THONG KE TONG KET (THREAD-SAFE RUN) =========");
        System.out.println("Tong so luot dat THANH CONG: " + bookingSystem.getSuccessfulBookings());
        System.out.println("Tong so luot dat THAT BAI: " + bookingSystem.getFailedBookings());
        System.out.println("=======================================================");
    }
}