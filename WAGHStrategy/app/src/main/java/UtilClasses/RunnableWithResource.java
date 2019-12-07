package UtilClasses;

public interface RunnableWithResource<T>{
    void run(T... resources);
}
