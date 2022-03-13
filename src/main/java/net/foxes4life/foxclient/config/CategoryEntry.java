package net.foxes4life.foxclient.config;

import com.google.gson.annotations.Expose;
import net.foxes4life.foxclient.Main;

public class CategoryEntry<T> {
    @Expose
    public final String name;

    @Expose
    private T value;
    
    public CategoryEntry(T value, String name) {
        this.value = value;
        this.name = name;
    }
    
    public CategoryEntry<T> setValue(Object newVal) {
        if(newVal == null) {
            System.out.println("value is null");
            return this;
        }

        //noinspection unchecked
        this.value = (T) newVal;
        return this;
    }
    
    public T getValue() {
        return this.value;
    }
}
