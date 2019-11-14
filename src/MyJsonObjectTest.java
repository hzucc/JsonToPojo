/*
 *@author ChenCheng
 *@date 2019/11/13
 */

import com.hzucc.MyUtil.entity.Problem;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class MyJsonObjectTest {
    public static void main(String[] args) throws NoSuchMethodException, MyJsonObject.NotSupportTypeException, IllegalAccessException, InstantiationException, MyJsonObject.JsonFormatException, InvocationTargetException, IOException {
        String s = "{\"problemId\":1044,\"problemName\":\"计算几何1\",\"problemContent\":\"<h1 style=\\\"text-align: center;\\\">计算几何1</h1><h2 style=\\\"text-align: left;\\\">题目描述：</h2><blockquote>给出三个点，求出三点围城的三角形的面积。</blockquote><h2>输入格式：</h2><blockquote>三行，每行两个整数x和y，中间用空格隔开。</blockquote><h2>输出格式：</h2><blockquote>一行，一个浮点数，保留两位小数。</blockquote><h2>输入样例：</h2><blockquote>0 0<br>0 1<br>1 0</blockquote><h2>输出样例：</h2><blockquote>0.50</blockquote>\",\"timeLimit\":{\"cTimeLimit\":1,\"c_cppTimeLimit\":1,\"javaTimeLimit\":1,\"goTimeLimit\":1,\"python3TimeLimit\":1},\"memoryLimit\":{\"cMemoryLimit\":126,\"c_cppMemoryLimit\":128,\"javaMemoryLimit\":128,\"goMemoryLimit\":128,\"python3MemoryLimit\":128}}";
        char[] chars = s.toCharArray();
        MyJsonObject myJsonObject = new MyJsonObject();
        Problem problem = (Problem) myJsonObject.toPojo(s, Problem.class);
        System.out.println(problem);
    }
}

