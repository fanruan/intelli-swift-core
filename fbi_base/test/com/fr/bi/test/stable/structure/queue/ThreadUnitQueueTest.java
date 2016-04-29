/**
 * 
 */
package com.fr.bi.test.stable.structure.queue;

import java.lang.reflect.Field;
import java.util.Deque;

import com.fr.bi.common.inter.Delete;
import com.fr.bi.stable.structure.queue.ConcurrentUUIDObject;
import com.fr.bi.stable.structure.queue.ThreadUnitedQueue;
import com.fr.cache.list.IntList;

import junit.framework.TestCase;

/**
 * @author Daniel
 *
 */
public class ThreadUnitQueueTest extends TestCase {
	
	public static int v = 0;
	private static IntList release = new IntList();
	private static IntList delete = new IntList();
	
	private static IntList release_fact = new IntList();
	private static IntList delete_fact = new IntList();
	
	private class A implements Delete {
		private int index = v++;
		
		A(){
			release.add(index);
			delete.add(index);
		}

		/* (non-Javadoc)
		 * @see com.fr.bi.common.inter.Release#clear()
		 */
		@Override
		public void clear() {
			synchronized (release_fact) {
				release_fact.add(index);
			}
		}
		
		@Override
		public String toString(){
			return String.valueOf(index);
		}

		/* (non-Javadoc)
		 * @see com.fr.bi.common.inter.Delete#delete()
		 */
		@Override
		public void delete() {
			synchronized (delete_fact) {
				delete_fact.add(index);
			}
		}
		
	}
	
	public void testQueue(){
		ThreadUnitedQueue<A> t = new ThreadUnitedQueue<A>();
		A a = new A();
		t.add(a);
		assertEquals(a.index, t.get().index);
		t.add(new A());
		assertEquals(a.index, t.get().index);
		t.releaseObject();
		a = new A();
		t.add(a);
		assertEquals(a.index, t.get().index);
		t.add(new A());
		assertEquals(a.index, t.get().index);
		System.out.println(a.index);
		t.clear();
	}
	
	public static void sleep(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void testMutiThread(){
		final ThreadUnitedQueue<A> t = new ThreadUnitedQueue<A>();
		createThread(0, t);
		createThread(0, t);
		createThread(1, t);
		createThread(2, t);
		createThread(2, t);
		createThread(5, t);
		createThread(5, t);
		createThread(5, t);
		createThread(5, t);
		createThread(5, t);
		createThread(5, t);
		createThread(5, t);
		createThread(6, t);
		createThread(5, t);
		createThread(8, t);
		int i = 0;
		while(i < 25){
			t.add(new A());
			ThreadUnitQueueTest.sleep();
			i++;
		}
		Field f;
		try {
			f = t.getClass().getDeclaredField("queue");
			f.setAccessible(true);
			Deque<ConcurrentUUIDObject<A>> q = (Deque<ConcurrentUUIDObject<A>>) f.get(t);
			t.releaseObject();
			ThreadUnitQueueTest.sleep();
			assertEquals(q.size(),1);
			assertEquals(t.get().index, ThreadUnitQueueTest.v - 1);
			t.clear();
			release.sort();
			delete.sort();
			delete_fact.sort();
			release_fact.sort();
			delete.remove(delete.size() - 1);
			assertEquals(release, release_fact);
			assertEquals(delete, delete_fact);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createThread(final int time, final ThreadUnitedQueue<A> t){
		new Thread(){
			@Override
			public void run(){
				for(int i = 0; i < time; i++){
					ThreadUnitQueueTest.sleep();
				}
				A a = t.get();
//				System.out.println(a);
				ThreadUnitQueueTest.sleep();
				assertEquals(a.index, t.get().index);
				ThreadUnitQueueTest.sleep();
				assertEquals(a.index, t.get().index);
				ThreadUnitQueueTest.sleep();
				assertEquals(a.index, t.get().index);
				ThreadUnitQueueTest.sleep();
				assertEquals(a.index, t.get().index);
				t.releaseObject();
			}
			
		}.start();
	}

}