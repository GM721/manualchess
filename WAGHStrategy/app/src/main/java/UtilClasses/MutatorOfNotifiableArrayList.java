package UtilClasses;

import java.util.ArrayList;

public class MutatorOfNotifiableArrayList<T> {
    NotifiableArrayList<T> notifiableArrayList;

    MutatorOfNotifiableArrayList(NotifiableArrayList<T> notifiableArrayList){
        this.notifiableArrayList = notifiableArrayList;
    }

    public void add(T data){
        notifiableArrayList.add(data);
    }

    public void set(ArrayList<T> arrayList){
        notifiableArrayList.set(arrayList);
    }
}
