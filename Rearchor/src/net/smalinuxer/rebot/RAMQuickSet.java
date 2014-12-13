package net.smalinuxer.rebot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RAMQuickSet<K> implements Set<K> {

	private Set<K> mSet;

	private static final String DEAULT_SET_STORE_PATH = File.separator + "data";

	private static final String DEAULT_SET_BAK_NAME = File.separator  + "file.bak";

	private static final String DEAULT_CFG_NAME = File.separator + "cfg.bak";

	public RAMQuickSet() {
		mSet = new HashSet<K>();
		File cfgfile = getFile(DEAULT_CFG_NAME);
		File file = getFile(DEAULT_SET_BAK_NAME);
		if (cfgfile != null && file != null && file.exists() && Searchor.USE_QUICK_SET) {
			ObjectInputStream in = null;
			ObjectInputStream cfgIn = null;
			try {
				cfgIn = new ObjectInputStream(new FileInputStream(cfgfile));
				Date date = (Date) cfgIn.readObject();
				if(new Date().getTime() - date.getTime() < Searchor.UPDATE_STORE_DUP_MILLSECORD){
					in = new ObjectInputStream(new FileInputStream(file));
					mSet = (Set<K>) in.readObject();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (in != null) {
						in.close();
						in = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (cfgIn != null) {
						cfgIn.close();
						cfgIn = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void store() throws IOException {
		File file = null;
		try {
			file = createFile(DEAULT_SET_BAK_NAME);
		} catch (FileNotFoundException e) {
			throw e;
		}
		ObjectWrite(file, mSet);
		if(mSet.size() != 0){
			File cgfFile = null;
			try {
				cgfFile = createFile(DEAULT_CFG_NAME);
			} catch (FileNotFoundException e) {
				throw e;
			}
			ObjectWrite(cgfFile, new Date());
		}
	}

	public void ObjectWrite(File file, Object obj) throws IOException {
		ObjectOutputStream output = null;
		try {
			if (file != null && file.exists()) {
				output = new ObjectOutputStream(new FileOutputStream(file,
						false));
				output.writeObject(obj);
				output.flush();
			}
		} finally {
			if (output != null) {
				output.flush();
				output.close();
				output = null;
			}
		}
	}

	protected File getFile(String fileName) {
		String path = System.getProperty("user.dir") + DEAULT_SET_STORE_PATH
				+ fileName;
		File file = new File(path);
		return file;
	}

	protected File createFile(String fileName) throws FileNotFoundException {
		String relativelyPath = System.getProperty("user.dir");
		File file = new File(relativelyPath + DEAULT_SET_STORE_PATH);
		if (file.exists() || file.mkdirs()) {
			file = new File(file.getAbsolutePath() + fileName);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (!file.exists()) {
				throw new FileNotFoundException("create bak fail!!!");
			}
		}
		return file;
	}

	@Override
	public int size() {
		return mSet.size();
	}

	@Override
	public boolean isEmpty() {
		return mSet.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return mSet.contains(o);
	}

	@Override
	public Iterator<K> iterator() {
		return mSet.iterator();
	}

	@Override
	public Object[] toArray() {
		return mSet.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return mSet.toArray(a);
	}

	@Override
	public boolean add(K e) {
		return mSet.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return mSet.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return mSet.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends K> c) {
		return mSet.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return mSet.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return mSet.removeAll(c);
	}

	@Override
	public void clear() {
		mSet.clear();
	}

	/**
	 * test
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		/*RAMQuickSet<String> set = new RAMQuickSet<String>();
		set.add("abc");
		set.add("cbd");
		set.add("bde");
		set.store(); // 一定需要store
		*/
		RAMQuickSet<String> set = new RAMQuickSet<String>();
		for(String str : set){
			System.out.println(str);
		}
	}
}
