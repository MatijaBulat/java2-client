package hr.algebra.client.utils;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.lang.reflect.Parameter;

public class ReflectionUtils {
    private static boolean isPrivate(int modifers){
        return Modifier.isPrivate(modifers);
    }
    private static boolean isProtected(int modifers){
        return Modifier.isProtected(modifers);
    }
    private static boolean isPublic(int modifers){
        return Modifier.isPublic(modifers);
    }
    private static boolean isStatic(int modifers){
        return Modifier.isStatic(modifers);
    }
    private static boolean isFinal(int modifers){
        return Modifier.isFinal(modifers);
    }

    public static String retrieveModifiers(int modifiers) {
        String modifiersString = "";

        if(isPublic(modifiers)) {
            modifiersString += "public ";
        }

        if(isPrivate(modifiers)) {
            modifiersString += "private ";
        }

        if(isProtected(modifiers)) {
            modifiersString += "protected ";
        }

        if(isStatic(modifiers)) {
            modifiersString += "static ";
        }

        if(isFinal(modifiers)) {
            modifiersString += "final ";
        }

        return modifiersString;
    }

    public static String retreiveParameters(Parameter[] parameters) {

        String paramsString = "";

        for(Parameter p : parameters) {
            paramsString += p.getType().getSimpleName() + " " + p.getName();

            if(!p.equals(parameters[parameters.length - 1])) {
                paramsString += ", ";
            }
        }

        return paramsString;
    }

    public static void readClassInfo(Class<?> clazz, StringBuilder classInfo) {
        appendPackage(clazz, classInfo);

        appendModifiers(clazz, classInfo);
        classInfo.append(" ").append(clazz.getSimpleName());
        appendParent(clazz, classInfo, true);
        appendInterfaces(clazz, classInfo);
    }

    private static void appendPackage(Class<?> clazz, StringBuilder classInfo) {
        classInfo.append("<i>")
                .append(clazz.getPackage())
                .append("</i>")
                .append("</p>");
    }

    private static void appendModifiers(Class<?> clazz, StringBuilder classInfo) {
        classInfo.append(Modifier.toString(clazz.getModifiers()));

    }

    private static void appendParent(Class<?> clazz, StringBuilder classInfo, boolean first) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null) {
            return;
        }
        if (first) {
            classInfo.append("<b>").append("\nextends").append("</b>");
        }
        classInfo
                .append(" ")
                .append(parent.getName());
        appendParent(parent, classInfo, false);
    }

    private static void appendInterfaces(Class<?> clazz, StringBuilder classInfo) {
        if (clazz.getInterfaces().length > 0) {
            classInfo.append("<b>").append("\nimplements ").append("</b>");
            classInfo.append(
                    Arrays.stream(clazz.getInterfaces())
                            .map(Class::getName)
                            .collect(Collectors.joining(" "))
            ).append("</p>");
        }
    }

    public static void readClassAndMembersInfo(Class<?> clazz, StringBuilder classAndMembersInfo) {
        readClassInfo(clazz, classAndMembersInfo);
        appendFields(clazz, classAndMembersInfo);
        appendMethods(clazz, classAndMembersInfo);
        appendConstructors(clazz, classAndMembersInfo);
    }

    private static void appendFields(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Field[] fields = clazz.getDeclaredFields();
        classAndMembersInfo.append("\n\n");
        classAndMembersInfo.append(
                Arrays.stream(fields)
                        .map(Objects::toString)
                        .collect(Collectors.joining("</p>"))
        );
    }

    private static void appendMethods(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            classAndMembersInfo.append("<br>");
            appendAnnotations(method, classAndMembersInfo);
            classAndMembersInfo
                    .append("</p><i>")
                    .append(Modifier.toString(method.getModifiers()))
                    .append(" ")
                    .append(method.getReturnType())
                    .append(" ")
                    .append(method.getName())
                    .append("</i>");
            appendParameters(method, classAndMembersInfo);
            appendExceptions(method, classAndMembersInfo);
        }
    }

    private static void appendAnnotations(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append(
                Arrays.stream(executable.getAnnotations())
                        .map(Objects::toString)
                        .collect(Collectors.joining("\n")));
    }

    private static void appendParameters(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append(
                Arrays.stream(executable.getParameters())
                        .map(Objects::toString)
                        .collect(Collectors.joining(", ", "(", ")"))
        );
    }

    private static void appendExceptions(Executable executable, StringBuilder classAndMembersInfo) {
        if (executable.getExceptionTypes().length > 0) {
            classAndMembersInfo.append(" throws ");
            classAndMembersInfo.append(
                    Arrays.stream(executable.getExceptionTypes())
                            .map(Class::getName)
                            .collect(Collectors.joining(" "))
            );
        }
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            classAndMembersInfo.append("<p><b>");
            appendAnnotations(constructor, classAndMembersInfo);
            classAndMembersInfo
                    .append("\n")
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append(" ")
                    .append(constructor.getName());
            appendParameters(constructor, classAndMembersInfo);
            appendExceptions(constructor, classAndMembersInfo);
            classAndMembersInfo.append("</b></p>");
        }
    }
}
