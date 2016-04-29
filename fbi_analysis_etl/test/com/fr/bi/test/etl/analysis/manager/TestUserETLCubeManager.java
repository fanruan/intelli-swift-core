/**
 * 
 */
package com.fr.bi.test.etl.analysis.manager;

import com.fr.base.FRContext;
import com.fr.bi.etl.analysis.manager.UserETLCubeManager;
import com.fr.dav.LocalEnv;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author Daniel
 *
 */
public class TestUserETLCubeManager extends TestCase {
	

	public void testMutiThreadadd(){
		
		UserETLCubeManager manager = new UserETLCubeManager();
		try {
			Field f = manager.getClass().getDeclaredField("cubePathMap");
			f.setAccessible(true);
			final Map<String, String> map = (Map<String, String>) f.get(manager);
			new Thread(){
				@Override
				public void run(){
					int i = 0;
					while(true){
						map.put(String.valueOf(i++), String.valueOf(i));
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}.start();
			
			new Thread(){
				@Override
				public void run(){
					int i = 0;
					while(true){
						map.remove(i++);
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while(iter.hasNext()){
				System.out.print(iter.next().getKey());
			}
			System.out.println();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			iter = map.entrySet().iterator();
			while(iter.hasNext()){
				System.out.print(iter.next().getKey());
			}
			System.out.println();
			
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
	
	public void testXMLReadWrite(){
		
		LocalEnv env = new LocalEnv();
		FRContext.setCurrentEnv(env);
		UserETLCubeManager manager = new UserETLCubeManager();
		String[][]  array = {{"abc", "abcd"},
				{"1", "2"},
				{"39", "4"},
				{"5", "7"}};
		try {
			Field f = manager.getClass().getDeclaredField("cubePathMap");
			f.setAccessible(true);
			Map<String, String> map = (Map<String, String>) f.get(manager);
			for(String[] a : array){
				map.put(a[0], a[1]);
			}
			env.writeResource(manager);
			manager = new UserETLCubeManager();
			manager.readXMLFile();
			Map<String, String> oldMap = map;
			map = (Map<String, String>) f.get(manager);
			assertEquals(map, oldMap);
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}