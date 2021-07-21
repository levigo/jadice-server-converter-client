package org.levigo.jadice.server.converterclient.util;


import java.io.IOException;

import com.levigo.jadice.document.io.SeekableInputStream;


class UncloseableSeekableInputStreamWrapper extends SeekableInputStream {
    private final SeekableInputStream delegate;
    private boolean locked = false;

    public UncloseableSeekableInputStreamWrapper(SeekableInputStream delegate) {
        this.delegate = delegate;
    }

    public long getSizeEstimate() {
        return this.delegate.getSizeEstimate();
    }

    public long length() throws IOException {
        return this.delegate.length();
    }

    public int read() throws IOException {
        return this.delegate.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return this.delegate.read(b, off, len);
    }

    public void seek(long pos) throws IOException {
        this.delegate.seek(pos);
    }

    public void close() throws IOException {
        if (!this.locked) {
            super.close();
            this.delegate.close();
        }

    }

    public boolean isCloseLocked() {
        return this.locked;
    }

    public void lockClose() {
        this.locked = true;
    }

    public void unlockClose() {
        this.locked = false;
    }

    public long getStreamPosition() throws IOException {
        return this.delegate.getStreamPosition();
    }
}
