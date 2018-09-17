/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.fr.swift.structure.array;

/**
 * A resizable, ordered or unordered int array. Avoids the boxing that occurs with ArrayList<Integer>. If unordered, this class
 * avoids a memory copy when removing elements (the last element is moved to the removed element's position).
 *
 * @author Nathan Sweet
 */
public class DirectIntList implements IntList {
    public int size;
    private DirectIntArray items;

    protected DirectIntList() {
        this(16);
    }

    protected DirectIntList(int capacity) {
        items = new DirectIntArray(capacity);
    }

    protected DirectIntList(int capacity, int defaultValue) {
        items = new DirectIntArray(capacity, defaultValue);
        size = items.size();
    }

    @Override
    public void add(int value) {
        DirectIntArray items = this.items;
        if (size == items.size()) {
            items = resize(Math.max(8, (int) (size * 1.75f)));
        }
        items.put(size++, value);
    }

    @Override
    public int get(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        }
        return items.get(index);
    }

    @Override
    public void set(int index, int val) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
        }
        items.put(index, val);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
        items.release();
    }

    private DirectIntArray resize(int newSize) {
        DirectIntArray newItems = new DirectIntArray(items, newSize);
        this.items = newItems;
        return newItems;
    }
}