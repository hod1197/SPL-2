package bgu.spl.mics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private AtomicReference<T> result = new AtomicReference<>();
    private AtomicBoolean isDone = new AtomicBoolean(false);

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public T get() {
        if (!this.isDone()) {
            try {
                this.condition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return this.result.get();
	}
	
	/**
     * Resolves the result of this Future object.
     */
	public void resolve (T result) {
		if (this.isDone()) {
			return; // Ignore if already resolved
		}

		this.result.set(result);
		this.isDone.set(true);;
		this.condition.signalAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return this.isDone.get();
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
        if (!this.isDone()) {
            try {
                if (!this.condition.await(timeout, unit)) {
                    return null;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return this.result.get();
	}
}
