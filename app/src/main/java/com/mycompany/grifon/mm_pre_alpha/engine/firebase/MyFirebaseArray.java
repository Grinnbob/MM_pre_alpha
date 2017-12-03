package com.mycompany.grifon.mm_pre_alpha.engine.firebase;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vlad on 03.12.2017.
 */

public class MyFirebaseArray  implements ChildEventListener {
        public interface OnChangedListener {
            enum EventType {ADDED, CHANGED, REMOVED, MOVED}

            void onChanged(OnChangedListener.EventType type, int index, int oldIndex);

            void onCancelled(DatabaseError databaseError);
        }

        private Query mQuery;
        private OnChangedListener mListener;
        private List<DataSnapshot> mSnapshots = new ArrayList<>();

        public MyFirebaseArray(Query ref) {
            mQuery = ref;
            mQuery.addChildEventListener(this);
        }

        public void cleanup() {
            mQuery.removeEventListener(this);
        }

        public int getCount() {
            return mSnapshots.size();
        }

        public DataSnapshot getItem(int index) {
            return mSnapshots.get(index);
        }

        private int getIndexForKey(String key) {
            int index = 0;
            for (DataSnapshot snapshot : mSnapshots) {
                if (snapshot.getKey().equals(key)) {
                    return index;
                } else {
                    index++;
                }
            }
            throw new IllegalArgumentException("Key not found");
        }

        @Override
        public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
            int index = 0;
            if (previousChildKey != null) {
                index = getIndexForKey(previousChildKey) + 1;
            }
            mSnapshots.add(index, snapshot);
            notifyChangedListeners(MyFirebaseArray.OnChangedListener.EventType.ADDED, index);
        }

        @Override
        public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
            int index = getIndexForKey(snapshot.getKey());
            mSnapshots.set(index, snapshot);
            notifyChangedListeners(MyFirebaseArray.OnChangedListener.EventType.CHANGED, index);
        }

        @Override
        public void onChildRemoved(DataSnapshot snapshot) {
            int index = getIndexForKey(snapshot.getKey());
            mSnapshots.remove(index);
            notifyChangedListeners(MyFirebaseArray.OnChangedListener.EventType.REMOVED, index);
        }

        @Override
        public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
            int oldIndex = getIndexForKey(snapshot.getKey());
            mSnapshots.remove(oldIndex);
            int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
            mSnapshots.add(newIndex, snapshot);
            notifyChangedListeners(MyFirebaseArray.OnChangedListener.EventType.MOVED, newIndex, oldIndex);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            notifyCancelledListeners(error);
        }

        public void setOnChangedListener(MyFirebaseArray.OnChangedListener listener) {
            mListener = listener;
        }

        protected void notifyChangedListeners(MyFirebaseArray.OnChangedListener.EventType type, int index) {
            notifyChangedListeners(type, index, -1);
        }

        protected void notifyChangedListeners(MyFirebaseArray.OnChangedListener.EventType type, int index, int oldIndex) {
            if (mListener != null) {
                mListener.onChanged(type, index, oldIndex);
            }
        }

        protected void notifyCancelledListeners(DatabaseError databaseError) {
            if (mListener != null) {
                mListener.onCancelled(databaseError);
            }
        }
    }

