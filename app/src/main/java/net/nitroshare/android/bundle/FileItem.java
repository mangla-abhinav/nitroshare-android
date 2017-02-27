package net.nitroshare.android.bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An individual file for transfer
 *
 * Note that Android's Java doesn't include java.nio.file so only the
 * last_modified property is usable on the platform.
 */
public class FileItem extends Item {

    // Additional properties for files
    private static final String READ_ONLY = "read_only";
    private static final String EXECUTABLE = "executable";
    private static final String LAST_MODIFIED = "last_modified";

    private File mFile;
    private Map<String, Object> mProperties;

    /**
     * Create a new file item from the specified file
     */
    public FileItem(File file) {
        mProperties = new HashMap<>();
        mProperties.put(TYPE, "file");
        mProperties.put(NAME, file.getName());
        mProperties.put(SIZE, Long.toString(mFile.length()));
        mProperties.put(READ_ONLY, !mFile.canWrite());
        mProperties.put(EXECUTABLE, mFile.canExecute());
        mProperties.put(LAST_MODIFIED, Long.toString(mFile.lastModified()));

        // TODO: these are used for temporary compatibility with 0.3.x
        mProperties.put("created", 0);
        mProperties.put("last_read", 0);
        mProperties.put("directory", false);
    }

    /**
     * Create a new file item from the specified path and filename
     * @param path absolute path
     * @param filename filename relative to path
     */
    public FileItem(String path, String filename) {
        this(new File(new File(path), filename));
    }

    @Override
    public Map<String, Object> getProperties() {
        return mProperties;
    }

    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;

    @Override
    public void open(Mode mode) throws IOException {
        switch (mode) {
            case Read:
                mInputStream = new FileInputStream(mFile);
                break;
            case Write:
                mOutputStream = new FileOutputStream(mFile);
                break;
        }
    }

    @Override
    public int read(byte[] data) throws IOException {
        int numBytes = mInputStream.read(data);
        if (numBytes == -1) {
            numBytes = 0;
        }
        return numBytes;
    }

    @Override
    public void write(byte[] data) throws IOException {
        mOutputStream.write(data);
    }

    @Override
    public void close() throws IOException {
        if (mInputStream != null) {
            mInputStream.close();
        }
        if (mOutputStream != null) {
            mOutputStream.close();
        }
    }
}
