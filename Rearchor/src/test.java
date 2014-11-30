import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class test {
	final static Lock lock = new ReentrantLock();
	public static void main(String[] args) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				lock.lock();
				try {
					System.out.println("A");
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.unlock();
			}
		}).start();
		lock.lock();
		try {
			System.out.println("A");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();
	}
}
