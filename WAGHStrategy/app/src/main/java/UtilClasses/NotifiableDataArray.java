package UtilClasses;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import CommonClasses.Message;

public class NotifiableDataArray<K,V> {

    private TreeMap<K,V> treeMap;
    private Handler handler;
    private Runnable onTreeMapSetAction;
    private Runnable onTreeMapInsertAction;
    private Runnable onElementChangedAction;
    private MutatorOfNotifiableDataArray<K,V> mutator = null;

    public NotifiableDataArray(Handler handler){
        this.handler = handler;
    }

    public NotifiableDataArray(Handler handler, TreeMap<K,V> treeMap, Runnable onTreeMapSetAction){
        this.handler = handler;
        this.treeMap = treeMap;
        this.onTreeMapSetAction = onTreeMapSetAction;
    }

    public NotifiableDataArray(Handler handler, TreeMap<K,V> treeMap,
                               Runnable onTreeMapSetAction, Runnable onTreeMapInsertAction){
        this.handler = handler;
        this.treeMap = treeMap;
        this.onTreeMapSetAction = onTreeMapSetAction;
        this.onTreeMapInsertAction = onTreeMapInsertAction;
    }

    public MutatorOfNotifiableDataArray<K,V> getMutator(){
        if(mutator==null){
            mutator = new MutatorOfNotifiableDataArray<K,V>(this);
        }else{
            throw new RuntimeException("Mutator already exists, maybe it is created not only by you?");
        }
        return mutator;
    }

    private void onSet(){
        handler.obtainMessage(0, onTreeMapSetAction).sendToTarget();
    }

    private void onArrayInsert(){
        handler.obtainMessage(0, onTreeMapInsertAction).sendToTarget();
    }

    private void onElementChanged(){
        handler.obtainMessage(0,onElementChangedAction).sendToTarget();
    }

    public void setOnTreeMapSetAction(Runnable onTreeMapSetAction){
        this.onTreeMapSetAction = onTreeMapSetAction;
    }

    public void setOnTreeMapInsertAction(Runnable onTreeMapInsertAction){
        this.onTreeMapInsertAction = onTreeMapInsertAction;
    }

    public void setOnElementChangedAction(Runnable onElementChangedAction){
        this.onElementChangedAction = onElementChangedAction;
    }

    void setTreeMap(TreeMap<K,V> treeMap){
        this.treeMap = treeMap;
        onSet();
    }

    void addOrChangeItem(K key,V value){
        if (this.treeMap.containsKey(key)){
            treeMap.put(key,value);
            onElementChanged();
        }
        else {
            treeMap.put(key,value);
            onArrayInsert();
        }
    }

    public V getItem(K key){
        return treeMap.get(key);
    }

    public TreeMap<K,V> getTreeMap(){
        return treeMap;
    }

    public V getItem(int n){
        Iterator<V> iterator = treeMap.values().iterator();
        for (int j = 0; j < n; j++) {
            iterator.next();
        }
        return iterator.next();
    }


    public int getSize(){
        return treeMap.size();
    }

    public ArrayList<V> getArrayList(){
        ArrayList<V> arrayList = new ArrayList<V>(this.treeMap.values());
        return arrayList;
    }


}
