import java.lang.reflect.Method;

public class Reflection {
    public static void main(String[] args) throws Exception {
        Class c = Manager.class;

        System.out.println("public class "+c.getSimpleName()+"Stub {");

        for(Method m: c.getMethods()) {
            System.out.println("   public " + m.getReturnType().getSimpleName()+ " " + m.getName() + "( ... ) {");
            System.out.println(    "}");
        }

        System.out.println("}");
    }
}
