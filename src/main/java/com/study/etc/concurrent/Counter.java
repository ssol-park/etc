package com.study.etc.concurrent;

import lombok.Getter;

@Getter
public class Counter {
    private volatile int count = 0;

    public void increament() {
        this.count++;
    }

    public synchronized void increamentSync() {
        this.count++;
    }

    public int getCount() {
        return this.count;
    }
}
