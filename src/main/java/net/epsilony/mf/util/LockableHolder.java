/*
 * Copyright (C) 2013 Man YUAN <epsilon@epsilony.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.epsilony.mf.util;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Man YUAN <epsilon@epsilony.net>
 * 
 */
public class LockableHolder<T> {

    T data;
    ReentrantLock lock = new ReentrantLock();

    public LockableHolder() {
    }

    public LockableHolder(T data) {
        super();
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void runInLock(GenericMethod<T> method) {
        try {
            lock.lock();
            method.run(data);
        } finally {
            lock.unlock();
        }
    }
}
