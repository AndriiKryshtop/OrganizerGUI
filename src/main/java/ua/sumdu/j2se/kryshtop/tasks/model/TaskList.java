package ua.sumdu.j2se.kryshtop.tasks.model;

import ua.sumdu.j2se.kryshtop.tasks.model.exceptions.InvalidTaskIndexException;
import ua.sumdu.j2se.kryshtop.tasks.model.exceptions.NullTaskException;

import java.io.Serializable;


public abstract class TaskList implements Iterable<Task>, Cloneable, Serializable{
    protected int size;

    public abstract void add(Task task) throws NullTaskException;

    public abstract boolean remove(Task task);

    public abstract Task getTask(int index) throws InvalidTaskIndexException;

    /**
     * To get number of records in the task list
     * @return number of records in this task list
     */
    public int size(){
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskList tasks = (TaskList) o;

        if (size() != tasks.size()) return false;

        for (int i=0; i < size(); i++){
            if (!getTask(i).equals(tasks.getTask(i))){
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + size();

        for(int i=0; i < size(); i++){
            result = 37 * result + (getTask(i) == null ? 0 : getTask(i).hashCode());
        }

        return result;
    }

    @Override
    public abstract String toString();
}
