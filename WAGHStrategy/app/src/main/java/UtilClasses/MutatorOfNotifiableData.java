package UtilClasses;

public class MutatorOfNotifiableData<T>{

    NotifiableData<T> notifiableData;

    MutatorOfNotifiableData(NotifiableData<T> notifiableData){
        this.notifiableData = notifiableData;
    }

    public void changeData(T data){
        notifiableData.changeData(data);
    }

    public NotifiableData<T> getNotifiableData(){
        return notifiableData;
    }


}
