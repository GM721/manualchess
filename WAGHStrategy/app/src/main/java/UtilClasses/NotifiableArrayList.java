package UtilClasses;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

public class NotifiableArrayList<T> {
    private ArrayList<T> arrayList;
    private RunnableWithResource onAdd;
    private Runnable onSet;
    private Handler handler;
    private MutatorOfNotifiableArrayList<T> mutator;

    public NotifiableArrayList(Handler handler){
        this.handler = handler;
    }

    public void setOnAddListener(RunnableWithResource onAddListener) {
        this.onAdd = onAddListener;
    }

    public void setOnSetListener(Runnable onSetListener){
        this.onSet = onSetListener;
    }


    public MutatorOfNotifiableArrayList<T> getMutator(){
        if(mutator==null){
            mutator = new MutatorOfNotifiableArrayList<T>(this);
        }else{
            throw new RuntimeException("Mutator already exists, maybe it is created not only by you?");
        }
        return mutator;
    }

    public int getSize(){
        return arrayList.size();
    }

    void add(T data){
        arrayList.add(data);
        handler.obtainMessage(10,new Pair<RunnableWithResource,Integer>(onAdd,arrayList.size()-1)).sendToTarget();
    }

    void set(ArrayList<T> arrayList){
        if(arrayList!=null) {
            this.arrayList = new ArrayList<>(arrayList);
            handler.obtainMessage(0,onSet).sendToTarget();
        }else{
            Log.d("arrayList","At notifiableArrayList,set method,arrayList is null");
        }
    }

    public T getItem(int index){
        return arrayList.get(index);
    }

}