package com.media.dongfeng.utils;

import java.lang.reflect.Array;    
import java.lang.reflect.Constructor;    
import java.lang.reflect.Field;    
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;    
   
public class Reflection {
    
    public Object getProperty(Object owner, String fieldName) throws Exception 
    {    
        Class ownerClass = owner.getClass();    
   
        Field field = ownerClass.getField(fieldName);    
   
        Object property = field.get(owner);    
   
        return property;    
    }    
   
    public Object getStaticProperty(String className, String fieldName)    
            throws Exception {    
        Class ownerClass = Class.forName(className);    
   
        Field field = ownerClass.getField(fieldName);    
   
        Object property = field.get(ownerClass);    
   
        return property;    
    }    
   
    public Object invokeMethod(Object owner, String methodName, Object[] args) 
            throws Exception {    
   
        Class ownerClass = owner.getClass();    
   
        Class[] argsClass = new Class[args.length];    
   
        for (int i = 0, j = args.length; i < j; i++) {    
            argsClass[i] = args[i].getClass();    
        }    
   
        Method method = ownerClass.getMethod(methodName, argsClass);    
   
        return method.invoke(owner, args);    
    }    
   
    public Object invokeStaticMethod(String className, String methodName,    
            Object[] args) throws Exception {    
        Class ownerClass = Class.forName(className);    
   
        Class[] argsClass = new Class[args.length];    
   
        for (int i = 0, j = args.length; i < j; i++) {    
            argsClass[i] = args[i].getClass();    
        }    
   
        Method method = ownerClass.getMethod(methodName, argsClass);    
   
        return method.invoke(null, args);    
    }    
   
    public Object newInstance(String className, Object[] args) throws Exception {    
        Class newoneClass = Class.forName(className);    
   
        Class[] argsClass = new Class[args.length];    
   
        for (int i = 0, j = args.length; i < j; i++) {    
            argsClass[i] = args[i].getClass();    
        }    
   
        Constructor cons = newoneClass.getConstructor(argsClass);    
   
        return cons.newInstance(args);    
   
    }    
   
    public boolean isInstance(Object obj, Class cls) {    
        return cls.isInstance(obj);    
    }    
        
    public Object getByArray(Object array, int index) {    
        return Array.get(array,index);    
    }

    public Object invokeMethod( Object ownerObj, String methodName, Class<?>[] parameterTypes,
            Object[] params ) {
        try {
            Class<?> ownerType = ownerObj.getClass();
            Method method = ownerType.getMethod( methodName, parameterTypes );
            return method.invoke( ownerObj, params );
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
    
	public Object invokeParamsMethod(Object ownerObj, String methodName,
			Class<?>[] parameterTypes, Object[] params)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Class<?> ownerType = ownerObj.getClass();
		Method method = ownerType.getMethod(methodName, parameterTypes);
		method.setAccessible(true);
		return method.invoke(ownerObj, params);
    }

    public Object invokeStaticMethod( String className, String methodName,
            Class<?>[] parameterTypes, Object[] params ) {
        try {
            Class<?> ownerClass = Class.forName( className );
            Method method = ownerClass.getMethod( methodName, parameterTypes );

            return method.invoke( null, params );
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}

