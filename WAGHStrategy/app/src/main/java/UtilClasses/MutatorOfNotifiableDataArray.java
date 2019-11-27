package UtilClasses;

import android.util.Log;

import java.util.TreeMap;

public class MutatorOfNotifiableDataArray<K,V> {
    NotifiableDataArray<K,V> notifiableDataArray;

    MutatorOfNotifiableDataArray(NotifiableDataArray<K,V> notifiableDataArray){
        this.notifiableDataArray = notifiableDataArray;
    }

    public void setTreeMap(TreeMap<K,V> arrayList){
        Log.d("Tree","set");
        notifiableDataArray.setTreeMap(arrayList);
    }

    public void addOrChangeItem(K key,V value){
        Log.d("Item","addedOrChanged");
        notifiableDataArray.addOrChangeItem(key,value);
    }

    public V getData(K key){
        return notifiableDataArray.getItem(key);
    }

}
