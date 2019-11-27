package UtilClasses;

import android.os.Handler;

import java.io.UncheckedIOException;

public class NotifiableData<T> {

    private Handler handler;
    private Runnable onChange;
    private T data;
    private MutatorOfNotifiableData<T> mutator;

    public NotifiableData(Handler handler){
        this.handler = handler;
    }

    public NotifiableData(Runnable onChange,Handler handler,T data)
    {
        this.onChange = onChange;
        this.handler = handler;
        changeData(data);
    }

    public MutatorOfNotifiableData<T> getMutator(){
        if(mutator==null) {
            mutator = new MutatorOfNotifiableData<T>(this);
        }
        else{
            throw new RuntimeException("Mutator already exists, maybe it is created not only by you?");
        }
        return mutator;
    }

    void changeData(T data){
        if(onChange==null){
            throw new RuntimeException("Runnable onChange has not set.");
        }
        else {
            this.data = data;
            handler.obtainMessage(0, onChange);
        }
    }

    public void setOnChange(Runnable runnable){
        this.onChange = runnable;
    }

    public T getData(){
        return data;
    }
}
