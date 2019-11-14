
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.Logger;

public class MyJsonObject {
    private char[] beiGaoChars;
    private int curent;
    private Hashtable<String, Class> classTables;
    private Hashtable<Class, Method> bigDecimalTo;
    private Hashtable<Character, Method> analyKeyValueTable;

    public MyJsonObject() throws NoSuchMethodException {

        //预处理字符串转Class<?>
        classTables = new Hashtable<String, Class>() {{
            put("byte", byte.class);
            put("Byte", Byte.class);
            put("short", short.class);
            put("Short", Short.class);
            put("int", int.class);
            put("Integer", Integer.class);
            put("long", Long.class);
            put("Long", Long.class);
            put("BigInteger", BigInteger.class);
            put("float", float.class);
            put("Float", Float.class);
            put("double", double.class);
            put("Double", Double.class);
        }};

        //预处理BigDecimal转基本类型方法
        Class tClass = BigDecimal.class;
        bigDecimalTo = new Hashtable<Class, Method>() {{
            put(byte.class, tClass.getMethod("byteValueExact"));
            put(Byte.class, tClass.getMethod("byteValueExact"));
            put(short.class, tClass.getMethod("shortValueExact"));
            put(Short.class, tClass.getMethod("shortValueExact"));
            put(int.class, tClass.getMethod("intValueExact"));
            put(Integer.class, tClass.getMethod("intValueExact"));
            put(long.class, tClass.getMethod("longValueExact"));
            put(Long.class, tClass.getMethod("longValueExact"));
            put(BigInteger.class, tClass.getMethod("toBigIntegerExact"));
            put(float.class, tClass.getMethod("floatValue"));
            put(Float.class, tClass.getMethod("floatValue"));
            put(double.class, tClass.getMethod("doubleValue"));
            put(Double.class, tClass.getMethod("doubleValue"));
        }};

        //预处理解析键值对（key：value）中的值的方法
        Method[] methods = MyJsonObject.class.getDeclaredMethods();
        analyKeyValueTable = new Hashtable<>();
        for (Method method: methods) {
            String name = method.getName();
            switch (name) {
                case "analyseString":
                    analyKeyValueTable.put('\"', method);
                    break;
                case "analyseNumber":
                    analyKeyValueTable.put('-', method);
                    for (char ch = '0'; ch <= '9'; ch++) {
                            analyKeyValueTable.put(ch, method);
                    }
                    break;
                case "analyseBoolean":
                    analyKeyValueTable.put('t', method);
                    analyKeyValueTable.put('f', method);
                    break;
                case "analyseNull":
                    analyKeyValueTable.put('n', method);
                    break;
                case "analyseArray":
                    analyKeyValueTable.put('[', method);
                    break;
                case "analyseObject":
                    analyKeyValueTable.put('{', method);
                    break;
                default:
                    break;
            }
        }

    }

    public class JsonFormatException extends Exception {
        public JsonFormatException(String message) {
            super(message);
        }
    }

    public class NotSupportedNumberTypeException extends Exception {
        public NotSupportedNumberTypeException(String message) {
            super(message);
        }
    }

    public class NotSupportTypeException extends Exception {
        public NotSupportTypeException(String message) {
            super(message);
        }
    }

    /**
     * 功能描述： <br/>
     * 跳过一段连续的分割符（空格，制表符，换行），直到不是分隔符为止，或终止于字符数组末尾。
     */
    private void clearSplitChars() {
        while (curent < beiGaoChars.length &&
                (beiGaoChars[curent] == ' ' ||
                beiGaoChars[curent] == '\t' ||
                beiGaoChars[curent] == '\n')) {
            ++curent;
        }
    }

    /**
     * 功能描述： <br/>
     * 解析一个字符串。
     */
    private String analyseString(Class tClass) throws JsonFormatException {
        ++curent;
        int begin = curent;
        while (curent < beiGaoChars.length &&
                !(beiGaoChars[curent] == '\"' &&
                        beiGaoChars[curent - 1] != '\\')) {
            ++curent;
        }
        if (curent < beiGaoChars.length &&
                beiGaoChars[curent] == '\"' &&
                curent - 1 > begin) {
            int end = curent;
            ++curent;
            return String.valueOf(beiGaoChars, begin, end - begin);
        } else {
            int start = Math.max(0, curent - 20);
            int count = Math.min(40, beiGaoChars.length - curent + 5);
            char[] chars = new char[count - start];
            Arrays.fill(chars, ' ');
            String str = String.valueOf(chars);
            throw new JsonFormatException("index:" + curent + "\n" +
                    String.valueOf(beiGaoChars, start, count) + "\n" +
                    str + "^\n" +
                    "message:在解析JSON中的一个字符串时，发现它没有以\"结尾\n");
        }
    }

    /**
     * 功能描述： <br/>
     * 解析一个数字，并转换为tClass类型。
     */
    private Object analyseNumber(Class tClass) throws JsonFormatException, NotSupportedNumberTypeException, InvocationTargetException, IllegalAccessException {
        int begin = curent;
        while (curent < beiGaoChars.length &&
                beiGaoChars[curent] != ' ' &&
                beiGaoChars[curent] != ',' &&
                beiGaoChars[curent] != ']' &&
                beiGaoChars[curent] != '}') {
            ++curent;
        }
        if (curent == beiGaoChars.length) {
            int start = Math.max(0, curent - 20);
            int count = Math.min(40, beiGaoChars.length - curent + 5);
            char[] chars= new char[curent - start];
            Arrays.fill(chars, ' ');
            String str = String.valueOf(chars);
            throw new JsonFormatException("index:" + curent + "\n" +
                    String.valueOf(beiGaoChars, start, curent) + "\n" +
                    str + "^\n" +
                    "message:在解析JSON中的一对键值对（key：value）中的值value完毕时，缺少一些JSON结尾标志字符，比如']'或'}'。\n");
        } else {
            int end = curent;
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(beiGaoChars, begin, end - begin));
            Object obj;
            if (tClass == BigDecimal.class) {
                obj = bigDecimal;
            } else if(bigDecimalTo.containsKey(tClass)) {
                Method method = bigDecimalTo.get(tClass);
                obj = method.invoke(bigDecimal);
            } else {
                int start = Math.max(0, curent - 20);
                int count = Math.min(40, beiGaoChars.length - curent + 5);
                char[] chars = new char[curent - start];
                Arrays.fill(chars, ' ');
                String str = String.valueOf(chars);
                throw new NotSupportedNumberTypeException("index:" + curent + "\n" +
                        String.valueOf(beiGaoChars, start, count) + "\n" +
                        str + "^\n" +
                        "message:在解析一个JSON中的键值对（key：value）中的值value时，不能将BigDecimal转换为" + tClass.getName() + "类型\n");
            }
            return obj;
        }
    }

    /**
     * 功能描述： <br/>
     * 解析一个逻辑类型。
     */
    private boolean analyseBoolean(Class tClass) throws JsonFormatException {
        if (curent + 3 < beiGaoChars.length &&
                String.valueOf(beiGaoChars, curent, 3).equals("true")) {
            curent += 4;
            return true;
        } else if (curent + 4 < beiGaoChars.length &&
                String.valueOf(beiGaoChars, curent, 4).equals("false")) {
            curent += 5;
            return false;
        } else {
            int start = Math.max(0, curent - 20);
            int count = Math.min(40, beiGaoChars.length - curent + 5);
            char[] chars = new char[curent - start];
            Arrays.fill(chars, ' ');
            String str = String.valueOf(chars);
            throw new JsonFormatException("index:" + curent + "\n" +
                    String.valueOf(beiGaoChars, start, count) + "\n" +
                    str + "^\n" +
                    "message:在解析一个JSON中的逻辑类型完毕时，发现JSON也被解析完毕了，缺少']'或'}'结尾。");
        }
    }

    /**
     * 功能描述： <br/>
     * 解析一个空值。
     */
    private Object analyseNull(Class tClass) throws JsonFormatException {
        if (curent + 3 < beiGaoChars.length &&
                String.valueOf(beiGaoChars, curent, 3).equals("null")) {
            curent += 4;
            if (curent >= beiGaoChars.length) {
                int start = Math.max(0, curent - 20);
                int count = Math.min(40, beiGaoChars.length - curent + 5);
                char[] chars = new char[curent - start];
                Arrays.fill(chars, ' ');
                String str = String.valueOf(chars);
                throw new JsonFormatException("index:" + curent + "\n" +
                        String.valueOf(beiGaoChars, start, count) + "\n" +
                        str + "^\n" +
                        "message:在解析完一个JSON中的一个null时，发现JSON已经解析完毕，缺少']'或'}'等字符作为JSON结尾。");
            }
        } else {
            int start = Math.max(0, curent - 20);
            int count = Math.min(40, beiGaoChars.length - curent + 5);
            char[] chars = new char[curent - start];
            Arrays.fill(chars, ' ');
            String str = String.valueOf(chars);
            throw new JsonFormatException("index:" + curent + "\n" +
                    String.valueOf(beiGaoChars, start, count) + "\n" +
                    str + "^\n" +
                    "message:在解析一个JSON中的空值时，发现它虽然是以n开头，但却不是null，不是一个合法的JSON类型。");
        }
        return null;
    }

    private class Node {
        private Object object;
        private List<Integer> list;

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public List<Integer> getList() {
            return list;
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }
    }

    /**
     * 功能描述： <br/>
     * 解析一个数组。 <br/>
     * 数组内部只能存储java定义的类型 <br/>
     * （byte，Byte，short，Short，int，Integer，long，Long，BigInteger，float，Float，double，Double，BigDecimal）
     */
    private Object analyseArray(Class tClass) throws JsonFormatException, InvocationTargetException, IllegalAccessException, NotSupportTypeException {
        String name = tClass.getSimpleName();
        if (name.contains("[")) {
            name = name.substring(0, name.indexOf('['));
            if (classTables.containsKey(name)) {
                tClass = classTables.get(name);
            } else {
                int start = Math.max(0, curent - 20);
                int count = Math.min(40, beiGaoChars.length - curent + 5);
                char[] chars = new char[curent - start];
                Arrays.fill(chars, ' ');
                String str = String.valueOf(chars);
                throw new NotSupportTypeException("index" + curent + "\n" +
                        String.valueOf(beiGaoChars, start, count) + "\n" +
                        str + "^\n" +
                        "message:在解析JSON时，不支持这种类型的数组。");
            }
        }
        //获取数组维度
        int[] array = new int[256];
        Stack<Integer> stack = new Stack<>();
        List<Node> list = new ArrayList<>();
        stack.push(0);
        stack.push(0);
        ++curent;
        char old = '['; //1 - 2 : []
        while (curent < beiGaoChars.length && stack.size() > 1) {
            clearSplitChars();
            if (beiGaoChars[curent] == '[') {
                stack.push(0);
                old = beiGaoChars[curent++];
            } else if (beiGaoChars[curent] == ']') {
                if (old == '[') {
                    int start = Math.max(0, curent - 20);
                    int count = Math.min(40, beiGaoChars.length - curent + 5);
                    char[] chars = new char[curent - start];
                    Arrays.fill(chars, ' ');
                    String str = String.valueOf(chars);
                    throw new JsonFormatException("index" + curent + "\n" +
                            String.valueOf(beiGaoChars, start, count) + "\n" +
                            str + "^\n" +
                            "message:在解析JSON的一个数组时，发现它是空数组[]。");
                }
                stack.pop();
                stack.push(stack.pop() + 1);
                array[stack.size() - 1] = Math.max(array[stack.size() - 1], stack.peek());
                old = beiGaoChars[curent++];
            } else if (beiGaoChars[curent] == ',') {
                old = beiGaoChars[curent++];
            } else {
                old = beiGaoChars[curent];
                Object o = analyKeyValue(tClass);
                Node node = new Node();
                node.setObject(o);
                stack.push(stack.pop() + 1);
                array[stack.size() - 1] = Math.max(array[stack.size() - 1], stack.peek());
                int size = stack.size();
                List<Integer> list1 = new LinkedList<>();
                int j = 0;
                for (Integer a : stack) {
                    if (j > 0 && j < size - 1) {
                        list1.add(a);
                    } else if (j == size - 1) {
                        list1.add(a - 1);
                    }
                    j++;
                }
                node.setList(list1);
                list.add(node);
            }
        }
        if (stack.size() != 1) {
            int start = Math.max(0, curent - 20);
            int count = Math.min(40, beiGaoChars.length - curent + 5);
            char[] chars = new char[curent - start];
            Arrays.fill(chars, ' ');
            String str = String.valueOf(chars);
            throw new JsonFormatException("index" + curent + "\n" +
                    String.valueOf(beiGaoChars, start, count) + "\n" +
                    str + "^\n" +
                    "message:在解析一个JSON中的一个数组时，发现异常，'['和']'的数量不匹配。");
        }
        int end = 256;
        while (end - 1 > 0 && array[end - 1] == 0) {
            --end;
        }
        Object obj = Array.newInstance(tClass, Arrays.copyOfRange(array, 1, end));
        for (Node node : list) {
            List<Integer> list1 = node.getList();
            int size = list1.size();
            if (size == 1) {
                Array.set(obj, list1.get(0), node.getObject());
            } else if (size == 2) {
                Object[] p = (Object[]) obj;
                Array.set(p[list1.get(0)], list1.get(1), node.getObject());
            } else {
                Object[] p = null;
                int a = 0, b = 0, j = 0;
                for (Integer i : list1) {
                    if (j < size - 2) {
                        p = (Object[]) p[i];
                    } else if (j == size - 2) {
                        a = i;
                    } else if (j == size - 3) {
                        b = i;
                    }
                    j++;
                }
                Array.set(p[a], b, node.getObject());
            }
        }
        return obj;
    }

    /**
     * 功能描述： <br/>
     * 接下来的将会解析一个JOSN形式的对象。
     */
    private Object analyseObject(Class tClass) throws IllegalAccessException, InstantiationException, JsonFormatException, InvocationTargetException, NotSupportTypeException {
        //预处理tClass中的set方法
        Hashtable<String, Method> hashtable = new Hashtable<>();
        Method[] methods = tClass.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("set") && method.getParameterCount() == 1) {
                char[] chars = name.substring(3).toCharArray();
                if (beiGaoChars.length > 2 &&
                        Character.isUpperCase(chars[0]) &&
                        (Character.isDigit(chars[1]) ||
                                Character.isLowerCase(chars[1]) ||
                                chars[1] == '_')) {
                    chars[0] = Character.toLowerCase(chars[0]);
                }

                String key = String.valueOf(chars);
                hashtable.put(key, method);
            }
        }
        Object obj = tClass.newInstance();
        do {
            clearSplitChars();
            if (beiGaoChars[curent] != '{' &&
                    beiGaoChars[curent] != ',') {
                break;
            }
            ++curent;
            clearSplitChars();
            String keyName = analyKeyName();
            clearSplitChars();
            if (curent >= beiGaoChars.length ||
                    beiGaoChars[curent] != ':') {
                int start = Math.max(0, curent - 20);
                int count = Math.min(40, beiGaoChars.length - curent + 5);
                char[] chars = new char[curent - start];
                Arrays.fill(chars, ' ');
                String str = String.valueOf(chars);
                throw new JsonFormatException("index" + curent + "\n" +
                        String.valueOf(beiGaoChars, start, count) + "\n" +
                        str + "^\n" +
                        "messages:在解析一个JSON中的一个键值对（key：value）时，发现没有键与值中间没有分隔符‘:’。");
            } else {
                ++curent;
            }
            Method method = hashtable.get(keyName);
            if (method != null) {
                Class t = method.getParameterTypes()[0];
                String name = t.getSimpleName();
                if (t.getSimpleName().contains("[")) {
                    name = name.substring(0, name.indexOf("["));
                    if (classTables.containsKey(name)) {
                        t = classTables.get(name);
                    } else {
                        int start = Math.max(0, curent - 20);
                        int count = Math.min(40, beiGaoChars.length - curent + 5);
                        char[] chars = new char[curent - start];
                        String str = String.valueOf(chars);
                        throw new NotSupportTypeException("index:" + curent + "\n" +
                                String.valueOf(beiGaoChars, start, count) + "\n" +
                                str + "^\n" +
                                "message:在解析JSON的一对键值对（key：value）的值时，发现它是一个数组，但不支持这种类型的数组\n");
                    }
                }
                Object o2 = analyKeyValue(t);
                method.invoke(obj, o2);
            } else {
                int start = Math.max(0, curent - 20);
                int count = Math.min(40, beiGaoChars.length - curent + 5);
                char[] chars = new char[curent - start];
                String str = String.valueOf(chars);
                String message = "index:" + curent + "\n" +
                        String.valueOf(beiGaoChars, start, count) + "\n" +
                        str + "^\n" +
                        "message:在解析JSON的一对键值对（key：value）的值时，没有找到属性" + keyName + "的set方法进行注入\n";

                Enumeration<Method> elements = hashtable.elements();
                message += "所有找到的set方法：\n";
                while (elements.hasMoreElements()) {
                    Method method1 = elements.nextElement();
                    message += method1.getName() + "\n";
                }
                throw new NotSupportTypeException(message);
            }
        } while (true);

        clearSplitChars();
        if (curent < beiGaoChars.length &&
                beiGaoChars[curent] == '}') {
            ++curent;
            return obj;
        } else {
            int start = Math.max(0, curent - 20);
            int count = Math.min(40, beiGaoChars.length - curent + 5);
            char[] chars = new char[curent - start];
            String str = String.valueOf(chars);
            throw new JsonFormatException("index:" + curent + "\n" +
                    String.valueOf(beiGaoChars, start,  count) + "\n" +
                    str + "^\n" +
                    "message:在解析一个JSON的对象时，发现它没有以字符'}'结尾。");
        }
    }

    /**
     * 功能描述： <br/>
     * 解析一个键的名称。 <br/>
     * 返回： <br/>
     * 一个String类型的键名。
     */
    private String analyKeyName() throws JsonFormatException {
        return analyseString(null);
    }

    /**
     * 功能描述： <br/>
     * 解析一个键值对（key：value）的值。
     */
    private Object analyKeyValue(Class tClass) throws JsonFormatException, IllegalAccessException, InvocationTargetException {
        clearSplitChars();
        if (curent >= beiGaoChars.length) {
            int start = Math.max(0, curent - 20);
            int count =  Math.min(40, beiGaoChars.length - curent + 5);
            char[] chars = new char[curent - start];
            String str = String.valueOf(chars);
            throw new JsonFormatException("index:" + curent + "\n" +
                    String.valueOf(beiGaoChars, start, count) + "\n" +
                    str + "^\n" +
                    "message:在解析一个JSON中的一个键值对（key：value）的值（value）之前，JSON就已经被解析完毕，缺少一些JSON结尾标志字符，比如']'或'}'\n");
        }
        if (analyKeyValueTable.containsKey(beiGaoChars[curent])) {
            Method method = analyKeyValueTable.get(beiGaoChars[curent]);
            return method.invoke(this, tClass);
        } else {
            int start = Math.max(0, curent - 20);
            int count =  Math.min(40, beiGaoChars.length - curent + 5);
            char[] chars = new char[curent - start];
            String str = String.valueOf(chars);
            throw new JsonFormatException("index:" + curent + "\n" +
                    String.valueOf(beiGaoChars, start, count) + "\n" +
                    "message:在解析JSON一个键值对（key：value）中的值（value）时，发现它不是JSON支持的类型\n");
        }
    }

    /**
     * 功能描述： <br/>
     * 解析一个json，转变为tClass类型的对象。
     */
    public Object toPojo(String json, Class tClass) throws InvocationTargetException, InstantiationException, JsonFormatException, IllegalAccessException, NotSupportTypeException {
        beiGaoChars = json.toCharArray();
        curent = 0;
        return analyseObject(tClass);
    }

    /**
     * 功能描述：  <br/>
     * 解析一个json，转变为tClass类型的数组。
     */
    public Object toArray(String json, Class tClass) throws IllegalAccessException, JsonFormatException, InvocationTargetException, NotSupportTypeException {
        beiGaoChars = json.toCharArray();
        curent = 0;
        return analyseArray(tClass);
    }

}
