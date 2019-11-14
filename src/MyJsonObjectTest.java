/*
 *@author ChenCheng
 *@date 2019/11/13
 */

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class MyJsonObjectTest {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, MyJsonObject.NotMemberException, MyJsonObject.NotSupportedObjectArrayException, MyJsonObject.NotSupportedNumberTypeException, MyJsonObject.JsonFormatException, InvocationTargetException, MyJsonObject.NotSupportTypeException, NoSuchMethodException {
        String s = "{\"id\":1,\"name\":\"cc\"}\n";
        MyJsonObject myJsonObject = new MyJsonObject();
        User user = (User) myJsonObject.toPojo(s, User.class);
        System.out.println(user);
    }
}
class User{
    private int id;
    private String name;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
