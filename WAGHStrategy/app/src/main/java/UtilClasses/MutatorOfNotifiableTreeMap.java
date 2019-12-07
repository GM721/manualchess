package UtilClasses;

import android.util.Log;

import java.util.TreeMap;

public class MutatorOfNotifiableTreeMap<K,V> {
    NotifiableTreeMap<K,V> notifiableTreeMap;

    MutatorOfNotifiableTreeMap(NotifiableTreeMap<K,V> notifiableTreeMap){
        this.notifiableTreeMap = notifiableTreeMap;
    }

    public void setTreeMap(TreeMap<K,V> arrayList){
        Log.d("Tree","set");
        notifiableTreeMap.setTreeMap(arrayList);
    }

    public void addOrChangeItem(K key,V value){
        Log.d("Item","addedOrChanged");
        notifiableTreeMap.addOrChangeItem(key,value);
    }

    public V getData(K key){
        return notifiableTreeMap.getItem(key);
    }

    public boolean containsKey(K key){
        return notifiableTreeMap.containsKey(key);
    }

}
