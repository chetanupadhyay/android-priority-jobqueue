package com.birbit.android.jobqueue.persistentQueue.sqlite;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobHolder;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.inMemoryQueue.SimpleInMemoryPriorityQueue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Persistent Job Queue that keeps its data in an sqlite database.
 */
public class SqliteJobQueue extends SimpleInMemoryPriorityQueue {

    private JobSerializer jobSerializer;

    public SqliteJobQueue(Configuration configuration, long sessionId, JobSerializer jobSerializer) {
        super(configuration, sessionId);
        this.jobSerializer = jobSerializer;
    }

    public Map<String, JobHolder> getCacheMap() {
        return idCache;
    }

    public static class JavaSerializer implements JobSerializer {

        @Override
        public byte[] serialize(Object object) throws IOException {
            if (object == null) {
                return null;
            }
            ByteArrayOutputStream bos = null;
            try {
                bos = new ByteArrayOutputStream();
                ObjectOutput out = new ObjectOutputStream(bos);
                out.writeObject(object);
                // Get the bytes of the serialized object
                return bos.toByteArray();
            } finally {
                if (bos != null) {
                    bos.close();
                }
            }
        }

        @Override
        public <T extends Job> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(bytes));
                //noinspection unchecked
                return (T) in.readObject();
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
    }

    public interface JobSerializer {
        byte[] serialize(Object object) throws IOException;
        <T extends Job> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException;
    }
}
