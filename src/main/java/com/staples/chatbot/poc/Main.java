package com.staples.chatbot.poc;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static Map<String,Integer> cases = new HashMap<String,Integer>();

    public static void main(String[] args) throws ScriptException, NoSuchMethodException, IOException {
    }

    public static void main4(String[] args) throws ScriptException, NoSuchMethodException {

//        String script =
//        "var fun1 = function(context) { print('Hi there from Javascript, ' + context); return context.count>2&&context.foo==='baasdr';};";

        String script =
                "var fun1 = function(context) { print('Hi there from Javascript, ' + context); context.foo='baasdr';context.bar++};";

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(script);

        HashMap<String,Object> context = new HashMap<>();
        context.put("now",new Date());
        context.put("count",3);
        context.put("foo","bar");

        Invocable invocable = (Invocable) engine;

        Object result = invocable.invokeFunction("fun1", context);
//        System.out.println(result);
//        System.out.println(result.getClass());
        System.out.println(context.get("foo"));
        System.out.println(context.get("bar"));
    }

    public static void main2(String[] args) {
        Pattern regex = Pattern.compile("(\\d{10})");
        Matcher regexMatcher = regex.matcher("this is my order number 1294567890");
        if(regexMatcher.find()){
            String order = regexMatcher.group(1);
            System.out.println(order);
        }
    }

    public static void oldmain(String[] args) {

        int[] values = new int[10];
        int max = 6;

        init(values);

        BigInteger newMax = BigInteger.valueOf(max);

        int goodCnt=0;

        newMax = newMax.pow(values.length);

        int loopMax = 6*6*6*6*6*6*6*6*6*6;


        for(int i=0;i<loopMax;i++){
            increment(values,0,max);
            if(isValid(values,20)){
                ++goodCnt;
                countCase(values);
                System.out.println(toString(values));
            }
        }

        System.out.printf("Good Count"+goodCnt);

        Iterator<String> i = cases.keySet().iterator();
        List<String> keys = new ArrayList<>();

        while(i.hasNext()){
            keys.add(i.next());
        }

        Collections.sort(keys);
        Collections.reverse(keys);

        for(int c=0;c<keys.size();c++){
            String key = keys.get(c);
            System.out.println(key+"\t"+cases.get(key));
        }

    }


    private static void countCase(int[] values) {
        countCase(getKey(values));
    }

    private static void countCase(String key) {
        Integer value = cases.get(key);
        if(value==null){
            value=0;
        }

        value = value + 1;

        cases.put(key,value);
    }

    public static String getKey(int[] values) {
        List<Integer> items = new ArrayList<Integer>();
        for(int i=0;i<values.length;i++){
            items.add(values[i]);
        }

        Collections.sort(items);

        Collections.reverse(items);

        return toString(items);

    }

    public static boolean isValid(int[] values,int sum){
        int total = 0;
        for(int i=0;i<values.length;i++){
            total = total + values[i];
            if(total>sum){
                return false;
            }
        }
        if(total==sum){
            return true;
        }
        return false;

    }

    private static void init(int[] values){
        for(int i=0;i<values.length;i++){
            values[i]=1;
        }
        values[0]=0;
    }


    private static String toString(List<Integer> values) {
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<values.size();i++){
            if(i>0){
                sb.append(",");
            }
            sb.append(values.get(i));
        }
        return sb.toString();
    }

    private static String toString(int[] values) {
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<values.length;i++){
            if(i>0){
                sb.append(",");
            }
            sb.append(values[i]);
        }
        return sb.toString();
    }


    private static int[] increment(int[] values,int position,int max){
        if(position>values.length){
            position=0;
        }

        int newValue = values[position];
        newValue = newValue+1;
        if(newValue>max){
            // reset the current position to 1
            values[position]=1;
            return increment(values,++position,max);
        }

        values[position]=newValue;
        return values;

    }
}
