package com.fr.swift.structure.external.map;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.concurrent.SimpleThreadFactory;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 * Created by FineSoft on 2015/6/23.
 */
public abstract class ExternalMap<K, V> implements Map<K, V>, Iterable<Map.Entry<K, V>> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ExternalMap.class);
    private static final String DATA_FOLDER_NAME = "externalData";
    private static final int BUFFER_SIZE = 20000;
    private static final String FILE_FOR_COUNT_NAME = "count";
    /**
     * 进行外排的时候,每一次读入的个数
     */
    private static final int EXTERNAL_READ_COUNT = 1;

    private static final ThreadFactory THREADS = new SimpleThreadFactory("Swift-ExternalMap-WriteFile");

    public static boolean DEBUG = false;
    public static boolean VERBOSE = false;
    public static boolean THREAD = true;
    public static boolean HYBRID = true;
    private Comparator comparator;
    private TreeMap<K, V> currentContainer;
    private Long containerSize;
    private long containerValueMaxSize;
    private long containerValueSize;
    private String diskContainerPath;
    private WriteFile writeFile;
    private ReadFile readFile;
    private boolean traversal;
    private Map<Integer, Integer> fileContentSize;
//    /**
//     * 如果base文件夹是新建的，那么就要删掉
//     */
//    private boolean containerFolderNeedDel = false;
    /**
     * 外排的文件个数
     */
    private int fileCount = 0;

    public ExternalMap(Comparator comparator, String dataFolderAbsPath) {
        this((long) BUFFER_SIZE, comparator, dataFolderAbsPath);
    }

    public ExternalMap(Comparator comparator) {
        this((long) BUFFER_SIZE, comparator, ExternalMap.class.getClassLoader().getResource("").getPath());
    }

    public ExternalMap(Long containerSize, Comparator comparator, String diskContainerPath) {
        this(containerSize, comparator, diskContainerPath, false);
    }

    public ExternalMap(Long containerSize, Comparator comparator, String diskContainerPath, boolean isKeepDiskFile) {
        this.containerSize = containerSize;
        this.containerValueMaxSize = Math.max(containerSize, containerSize << 6);
        this.containerValueSize = 0;
        traversal = false;
        this.diskContainerPath = diskContainerPath;
        fileContentSize = new HashMap<Integer, Integer>();
        File file = new File(diskContainerPath);
//        if (!file.exists()) {
//            this.containerFolderNeedDel = true;
//            file.mkdirs();
//        } else {
        if (!isKeepDiskFile) {
            FileUtil.delete(file);
            file.mkdirs();
        }
//        }
        if (DEBUG) {
            LOGGER.info(this.diskContainerPath);
        }
        writeFile = new WriteFile();
        readFile = new ReadFile();
        this.comparator = comparator;
        currentContainer = getNewCurrentContainer();
    }

    public static long computeBufferSize(long rowCount) {
        long base = 10000000;
        if (rowCount <= 0) {
            return 1;
        }
        if (rowCount <= base / 100) {
            return rowCount;
        } else if (rowCount < base) {
            return (rowCount) / 3;
        } else if (rowCount < (base * 100)) {
            return (rowCount) / 30;
        } else {
            return base;
        }
    }

    public void writeExternal() {
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(getFolderPath() + File.separator + "serialMap"));
            out.writeObject(fileContentSize);
            out.writeInt(fileCount);
            out.writeBoolean(traversal);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    public void readExternal() {
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(getFolderPath() + File.separator + "serialMap"));
            fileContentSize = (Map<Integer, Integer>) in.readObject();
            fileCount = in.readInt();
            traversal = in.readBoolean();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private TreeMap<K, V> getNewCurrentContainer() {
        return new TreeMap<K, V>(comparator);
    }

    public WriteFile getWriteFile() {
        return writeFile;
    }

    private void writeLog(String content) {
        if (DEBUG) {
            LOGGER.info(diskContainerPath + "--" + content);
        }
    }

    public int getWriterFileBufferSize() {
        return writeFile.getBufferSize();
    }

    public abstract ExternalMapIO<K, V> getExternalMapIO(String id_filePath);

    public abstract ExternalMapIO<K, V> getMemMapIO(TreeMap<K, V> currentContainer);

    /**
     * 当进行外排的过程中，不同文件中的相同key的value需要进行合并
     *
     * @param list 来自不同文件的相同key的集合，按照文件ID进行排序。
     */
    public abstract V combineValue(TreeMap<Integer, V> list);

    private String getKey(ArrayList<Byte> content) {
        Iterator<Byte> it = content.iterator();

        byte[] bcontent = new byte[content.size()];
        for (int i = 0; i < content.size(); i++) {
            bcontent[i] = content.get(i);
        }
        return new String(bcontent);
    }

    private String getContainerName() {
        return "main";
    }

    protected String getContainerPath() {
        return getFolderPath() + File.separator + getContainerName();
    }

    private String getDataFilePath(int fileID) {
        return getFolderPath() + File.separator + fileID;
    }

    private String getDataSeparatorFileCount() {
        return getFolderPath() + File.separator + fileCount;
    }

    private String getContainerTempPath() {
        return diskContainerPath + File.separator + "temp";
    }

    private String getFolderPath() {
        String path = diskContainerPath + File.separator + DATA_FOLDER_NAME;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    private DataInputStream getCount() {
        return getInputStream(getDataSeparatorFileCount());
    }

    private DataInputStream getSperateFileInput(int fileID) {
        return getInputStream(getDataFilePath(fileID));
    }

    private DataInputStream getMainFileInput() {
        return getInputStream(getContainerPath());
    }

    private DataInputStream getInputStream(String path) {
        try {
            File file = new File(path);
            FileInputStream fileIn = new FileInputStream(file);
            return new DataInputStream(fileIn);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public int size() {
        int size = 0;
        try {
            if (readFile != null) {
                size = readFile.getSize();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new PrivateEntryIterator();
    }

    public Iterator<Map.Entry<K, V>> getIterator() {
        return new PrivateEntryIterator();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return currentContainer.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return currentContainer.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return currentContainer.get(key);
    }

    @Override
    public V put(K key, V value) {
        V v = currentContainer.put(key, value);
        if (currentContainer.size() >= containerSize || containerValueSize++ > containerValueMaxSize) {
            dump();
        }
        return v;
    }

    protected void increaseValueSize() {
        if (containerValueSize++ > containerValueMaxSize) {
            dump();
        }
    }

    private void dump() {
        if (DEBUG) {
            LOGGER.debug("Dump:" + currentContainer.size());
        }

        if (!currentContainer.isEmpty()) {
            writeFile.push(currentContainer);
            currentContainer = getNewCurrentContainer();
            containerValueSize = 0;
        }
    }

    /**
     * 加入map全部内容，并且连同容器已有的全部写磁盘
     *
     * @param map
     */
    public void dump(Map<K, V> map) {
        if (!map.isEmpty()) {
            currentContainer.putAll(map);
            dump();
        }
    }

    public void dumpMap() {
        dump();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        currentContainer.clear();
        release();

        FileUtil.delete(diskContainerPath);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> t) {
        throw new UnsupportedOperationException();
    }

    public void release() {
        this.writeFile.stop();
        this.readFile.release();
    }

    public void display(Map<K, V> content) {
        if (DEBUG & VERBOSE) {
            if (content.isEmpty()) {
                return;
            }
            Iterator<K> it = content.keySet().iterator();
            while (it.hasNext()) {
                K name = it.next();
                LOGGER.info(name + "  :");
                V list = content.get(name);
                LOGGER.info(list.toString() + " ");

            }
            LOGGER.info("\n");
        }
    }

    public void display() {
        if (DEBUG) {
            Iterator<Map.Entry<K, V>> it = getIterator();
            while (it.hasNext()) {
                Map.Entry<K, V> entry = it.next();
                LOGGER.info(entry.getKey() + "  :");
                V list = entry.getValue();
                LOGGER.info(list.toString() + " ");

            }
        }
    }

    private void displaySeparateFile() {
//       LOGGER.infoln("**************displaySeparateFile************");
//        for (int i = 0; i < fileCount; i++) {
//            System.out.println("ID:" + i + "  :");
//            ExternalMapIO<K, V> io = getExternalMapIO(getDataFilePath(i));
//            Map<K, V> result = null;
//            do {
//                try {
//                    result = io.read();
//                } catch (FileNotFoundException ex) {
//                     LOGGER.error(e.getMessage(), e);
//                }
//                display(result);
//            } while (result.size() != 0);
//            System.out.println("");
//
//        }
//        System.out.println("*************displaySeparateFile*************");

    }

    public boolean isDumpComplete() {
        return writeFile.isFree();
    }

    enum DumpState {
        RUNNING,
        FREE
    }

    public static class Entry<K, V> implements Map.Entry<K, V> {
        K key;
        V value;

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return this.value;
        }
    }

    class ReadFile {
        ArrayList<Integer> need2Read;
        Map<K, ArrayList<Integer>> nameIdMap;
        Map<Integer, ExternalMapIO<K, V>> fileHandles;
        Map<K, TreeMap<Integer, V>> container;
        int lineCount = 0;
        int size = -1;
        Entry<K, V> next = null;

        public ReadFile() {
            fileHandles = new HashMap<Integer, ExternalMapIO<K, V>>();
        }

        private int getSize() throws Exception {
            if (!traversal) {
                throw new Exception("need to traverse this map one time");
            }
            return lineCount;
        }

        public void prepare() {
            release();
            if (!traversal) {
                lineCount = 0;
            }

            fileHandles.clear();
            /**
             * 记录当前遍历到名称出自于哪个文件。
             */
            nameIdMap = new HashMap<K, ArrayList<Integer>>();

            /**
             * 记录文件的id。该文件在下一轮要被读取。
             */
            need2Read = new ArrayList<Integer>();

            try {
                container = comparator != null ? new TreeMap<K, TreeMap<Integer, V>>(comparator) : new TreeMap<K, TreeMap<Integer, V>>();
                writeLog("writer file size");
                for (int i = 0; i < fileCount; i++) {
                    for (int count4Read = 0; count4Read < EXTERNAL_READ_COUNT; count4Read++) {
                        need2Read.add(i);
                    }
                    ExternalMapIO<K, V> in = getExternalMapIO(getDataFilePath(i));
                    if (i == fileCount - 1) {
                        in.setSize(containerSize.intValue());
                    } else {
                        in.setSize(fileContentSize.get(i));
                    }
                    writeLog("writer file :" + i + ",size :" + fileContentSize.get(i));

                    fileHandles.put(i, in);
                }
                if (HYBRID) {
                    fileHandles.put(fileCount, getMemMapIO(currentContainer));
                    for (int count4Read = 0; count4Read < EXTERNAL_READ_COUNT; count4Read++) {
                        need2Read.add(fileCount);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        public boolean hasNext() {
            if (next == null) {
                next = getOne();
                return next != null;
            }
            return true;

        }

        public Entry<K, V> next() {
            Entry<K, V> result;
            if (next == null) {
                result = getOne();
            } else {
                result = next;
            }
            next = null;
            return result;
        }

        private Entry<K, V> getOne() {
            if (seekLocalTopOne()) {
                return null;
            }
            return seekGlobalTopOne();
        }

        /**
         * 寻找局部的top
         *
         * @return 是否找到
         */
        private boolean seekLocalTopOne() {
            return traversalNeed2Read();
        }

        /**
         * 通过seekGlobalTopOne函数，遍历need2read对象后。
         * container里面包含了need2read中每个文件数据的局部top。
         * 下面就是从container中找出所有对象的top，
         * 同时如果top来自多个不同的文件，还要进行数据值的合并。
         *
         * @return topOne
         */
        private Entry<K, V> seekGlobalTopOne() {
            if (container.isEmpty()) {
                /**
                 * 没有数据了，合并结束。
                 */
                traversal = true;
                size = lineCount;
                release();
                return null;
            } else {
                /**
                 * 读取第一行数据写文件，并取出该数据的来源文件id数组，下一轮读取数据就从改id数组中读取。
                 */
                Iterator<K> temp = container.keySet().iterator();
                K firstKey = temp.hasNext() ? temp.next() : null;
                if (firstKey == null) {
                    size = lineCount;
                    traversal = true;
                    release();
                    return null;
                }
                TreeMap<Integer, V> listValue = container.get(firstKey);
                if (!traversal) {
                    lineCount++;
                }
                need2Read.addAll(nameIdMap.get(firstKey));
                if (DEBUG && VERBOSE) {
                    LOGGER.info("first name:" + firstKey + ", source:" + nameIdMap.get(firstKey) + " -- ");
                }
                nameIdMap.remove(firstKey);
                container.remove(firstKey);
                Entry<K, V> result = new Entry<K, V>();
                result.key = firstKey;
                result.value = combineValue(listValue);
                return result;
            }
        }

        /**
         * 遍历need2read里面记录的Id对应的文件，从中获取每个文件的第一行数据
         * 这里的文件可能是硬盘文件，也可能是是实现接口的内存对象
         *
         * @return
         */
        private boolean traversalNeed2Read() {
            Iterator<Integer> it = need2Read.iterator();
            while (it.hasNext()) {
                Integer id = it.next();
                if (DEBUG && VERBOSE) {
                    LOGGER.info("read file id is:" + id);
                }
                /**
                 * 读取id对应文件中的1行数据。
                 */
                Pair<K, V> oneRound;
                try {
                    oneRound = fileHandles.get(id).read();
                } catch (FileNotFoundException ex) {
                    LOGGER.info("Can't find the External sort file ");
                    LOGGER.error(ex.getMessage(), ex);
                    release();
                    return true;
                }
                if (oneRound != null) {
                    /**
                     * 记录当前record的出处。
                     */
                    K name = oneRound.getKey();
                    if (!nameIdMap.containsKey(name)) {
                        nameIdMap.put(name, new ArrayList<Integer>());
                    }
                    nameIdMap.get(name).add(id);
                    /**
                     * 融合到map中,List是安照文件顺序排列的value的集合。
                     * 在combine函数中对list进行组合得到最终的value结果
                     */
                    TreeMap<Integer, V> list = container.get(name);
                    if (list == null) {
                        list = generateComparedTreeMap();
                    }
                    list.put(id, oneRound.getValue());
                    container.put(name, list);
                } else {
                    continue;
                }
            }
            need2Read.clear();
            return false;
        }

        private TreeMap<Integer, V> generateComparedTreeMap() {
            return new TreeMap<Integer, V>(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1 - o2;
                }
            });
        }

        public void release() {
            try {
                Iterator<Integer> it = fileHandles.keySet().iterator();
                while (it.hasNext()) {
                    int id = it.next();
                    fileHandles.get(id).close();
                }
//                File file = new File(getContainerPath());
//                if (file.exists()) {
//                    file.deleteById();
//                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    class WriteFile implements Runnable {

        private Boolean isFinished = false;
        private DumpState dumpState = DumpState.FREE;
        private Thread writeThread;
        private BlockingQueue<Map<K, V>> buffer = new ArrayBlockingQueue<Map<K, V>>(1);

        WriteFile() {
            writeThread = THREADS.newThread(this);
            writeThread.start();
        }

        public DumpState getDumpState() {
            return dumpState;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public int getBufferSize() {
            return buffer.size();
        }

        public void stop() {
            while (buffer.size() != 0) {
                try {
                    LOGGER.info("waiting for finishing the mission");
                    Thread.sleep(100);
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
            writeThread.interrupt();
        }

        public void push(TreeMap<K, V> container) {
//            checkThreshold();
            try {
                dumpState = DumpState.RUNNING;
                buffer.put(container);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

        }

//        public void checkThreshold() {
//            synchronized (this) {
//                if (THREAD) {
//                    System.out.println("left memory:" + memoryHelper.leftMemoryPercent());
//                }
//                if (memoryHelper.leftMemoryPercent() > 0.9&&buffer.size()>2) {
//                    try {
//                        if (THREAD) {
//                            System.out.println("wait");
//                        }
//                        wait();
//                    } catch (Exception ex) {
//                        LOGGER.error(ex.getMessage(), ex);
//                    }
//                }
//            }
//        }

        @Override
        public void run() {
            try {
                while (true) {
                    dumpState = DumpState.FREE;
                    Map<K, V> writeFileContainer = buffer.take();
                    dumpState = DumpState.RUNNING;
                    long start = System.currentTimeMillis();
                    if (!writeFileContainer.isEmpty()) {
                        if (DEBUG) {
                            LOGGER.info("write");
                        }
                        fileContentSize.put(fileCount, writeFileContainer.size());
                        dumpData(writeFileContainer, fileCount);
//                        writeFileContainer.clear();
                        fileCount++;
                        updateFileCountToFile();
                        writeLog("Time of  dumping one container: " + (System.currentTimeMillis() - start));
                    }
                }
            } catch (InterruptedException ex) {
                if (DEBUG) {
                    displaySeparateFile();
                }
                isFinished = true;
                dumpState = DumpState.FREE;
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

        }

        public boolean isFree() {
            return (getDumpState() == DumpState.FREE) && buffer.isEmpty();
        }

        private void updateFileCountToFile() {
            DataOutputStream writer = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fos = new FileOutputStream(getFolderPath() + File.separator + FILE_FOR_COUNT_NAME);
                bos = new BufferedOutputStream(fos);
                writer = new DataOutputStream(bos);
                writer.writeInt(fileCount);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }


        private DataOutputStream getDataStream(int count) {
            DataOutputStream writer = null;
            try {
                FileOutputStream fos = new FileOutputStream(getDataFilePath(count));
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                writer = new DataOutputStream(bos);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            return writer;
        }


        private void dumpData(Map<K, V> writeFileContainer, int fileCount) {
            ExternalMapIO<K, V> io = getExternalMapIO(getDataFilePath(fileCount));
            try {
                Iterator<Map.Entry<K, V>> it = writeFileContainer.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<K, V> entry = it.next();
                    io.write(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                io.close();
            }
        }
    }

    private class PrivateEntryIterator implements Iterator<Map.Entry<K, V>> {
        PrivateEntryIterator() {
            if (!HYBRID) {
                dump();
            }
            try {
                if (DEBUG) {
                    LOGGER.info("wirte object is free:" + writeFile.isFree());
                }
                while (!writeFile.isFree()) {
                    Thread.sleep(10);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            readFile.prepare();
        }

        @Override
        public boolean hasNext() {
            boolean result = false;
            try {
                result = readFile.hasNext();
            } catch (Exception ex) {
                release();
                LOGGER.error(ex.getMessage(), ex);
            }
            return result;
        }


        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map.Entry<K, V> next() {
            return readFile.next();
        }
    }

}