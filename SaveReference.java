package sample;
import java.util.HashMap;
import java.util.Random;

/*========================
管理Reference的static類別
========================*/

public class SaveReference{

    private static HashMap<String, Object> objArr = new HashMap<String, Object> ();
    private static int delay = 10;

    public SaveReference(){
        throw new Error("This is a static class");
    }
    
    public static void addReference(String name, Object obj){
        objArr.put(name, obj);
    }

    public static Object getReference(String name){
        return objArr.get(name);
    }

    public static int getDelay(){
        return delay;
    }

}
