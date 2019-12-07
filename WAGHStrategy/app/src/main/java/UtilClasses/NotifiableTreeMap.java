package UtilClasses;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class NotifiableTreeMap<K,V> {

    private TreeMap<K,V> treeMap;
    private Handler handler;
    private Runnable onTreeMapSetAction;
    private RunnableWithResource onTreeMapInsertAction;
    private RunnableWithResource onElementChangedAction;
    private MutatorOfNotifiableTreeMap<K,V> mutator = null;

    public NotifiableTreeMap(Handler handler){
        this.handler = handler;
    }

    public NotifiableTreeMap(Handler handler, TreeMap<K,V> treeMap, Runnable onTreeMapSetAction){
        this.handler = handler;
        this.treeMap = treeMap;
        this.onTreeMapSetAction = onTreeMapSetAction;
    }

    public NotifiableTreeMap(Handler handler, TreeMap<K,V> treeMap,
                             Runnable onTreeMapSetAction, RunnableWithResource onTreeMapInsertAction){
        this.handler = handler;
        this.treeMap = treeMap;
        this.onTreeMapSetAction = onTreeMapSetAction;
        this.onTreeMapInsertAction = onTreeMapInsertAction;
    }

    public MutatorOfNotifiableTreeMap<K,V> getMutator(){
        if(mutator==null){
            mutator = new MutatorOfNotifiableTreeMap<K,V>(this);
        }else{
            throw new RuntimeException("Mutator already exists, maybe it is created not only by you?");
        }
        return mutator;
    }

    private void onSet(){
        handler.obtainMessage(0, onTreeMapSetAction).sendToTarget();
    }

    private void onArrayInsert(int element){
        handler.obtainMessage(10,new Pair<RunnableWithResource,Integer>(onTreeMapInsertAction,element)).sendToTarget();
    }

    private void onElementChanged(int element){
        handler.obtainMessage(10,new Pair<RunnableWithResource,Integer>(onElementChangedAction,element)).sendToTarget();
    }

    public void setOnTreeMapSetAction(Runnable onTreeMapSetAction){
        this.onTreeMapSetAction = onTreeMapSetAction;
    }

    public void setOnTreeMapInsertAction(RunnableWithResource onTreeMapInsertAction){
        this.onTreeMapInsertAction = onTreeMapInsertAction;
    }

    public void setOnElementChangedAction(RunnableWithResource onElementChangedAction){
        this.onElementChangedAction = onElementChangedAction;
    }

    void setTreeMap(TreeMap<K,V> treeMap){
        this.treeMap = treeMap;
        onSet();
    }

    void addOrChangeItem(K key,V value){
        if (this.treeMap.containsKey(key)){
            treeMap.put(key, value);
            Iterator<K> iterator = treeMap.keySet().iterator();
            int i=0;
            Log.d("Right before cycle","Cycle");
            while(!iterator.next().equals(key)){
                Log.d("Right in cycle","Cycle");
                i++;
            }
            onElementChanged(i);
        }
        else {
            treeMap.put(key,value);
            onArrayInsert(treeMap.size()-1);
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
        if(treeMap!=null) {
            return treeMap.size();
        }else return -1;
    }

    public ArrayList<V> getArrayList(){
        ArrayList<V> arrayList = new ArrayList<V>(this.treeMap.values());
        return arrayList;
    }

    public boolean containsKey(K key){
        return treeMap.containsKey(key);
    }


}
