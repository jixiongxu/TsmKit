package com.tms.plugin.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

public class TSMMethodVisitor extends AdviceAdapter {

    private boolean inject = false;

    private boolean isMainThread = true;

    protected TSMMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        System.out.println("tsm Processing：" + getName());
        if (!inject) {
            return;
        }
        visitorChildThread();
    }

    /**
     * create function run on child thread code
     */
    private void visitorChildThread() {
        mv.visitCode();
        //String threadName = "main"
        invokeStatic(Type.getType("Ljava/lang/Thread;"), new Method("currentThread", "()Ljava/lang/Thread;"));
        invokeVirtual(Type.getType("Ljava/lang/Thread;"), new Method("getName", "()Ljava/lang/String;"));
        int currentThreadName = newLocal(Type.getType("Ljava/lang/String;"));
        storeLocal(currentThreadName);

        //boolean var6 = var5.equals("main");
        loadLocal(currentThreadName);
        mv.visitLdcInsn("main");
        invokeVirtual(Type.getType("Ljava/lang/String;"), new Method("equals", "(Ljava/lang/Object;)Z"));
        int isMain = newLocal(Type.BYTE_TYPE);
        storeLocal(isMain);
        loadLocal(isMain);
        Label l1 = new Label();
        mv.visitJumpInsn(isMainThread ? IFNE : IFEQ, l1);

        //String methodName = "function"
        int methodName = newLocal(Type.getType("Ljava/lang/String;"));
        mv.visitLdcInsn(getName());
        storeLocal(methodName);

        // Object[] params = new Object[2];
        Type[] argumentTypes = getArgumentTypes();
        int argumentTypesObject = newLocal(Type.getType("Ljava/lang/Object;"));
        mv.visitIntInsn(SIPUSH, argumentTypes.length);
        mv.visitTypeInsn(ANEWARRAY, "Ljava/lang/Object;");
        storeLocal(argumentTypesObject);
        for (int index = 0; index < argumentTypes.length; index++) {
            mv.visitVarInsn(ALOAD, argumentTypesObject);
            mv.visitIntInsn(BIPUSH, index);
            mv.visitVarInsn(ALOAD, index + 1);
            mv.visitInsn(AASTORE);
        }
        // invoke
        mv.visitVarInsn(ALOAD, 0);//this
        mv.visitVarInsn(ALOAD, methodName); //
        mv.visitVarInsn(ALOAD, argumentTypesObject);
        mv.visitMethodInsn(INVOKESTATIC, "com/tms/android/TsmReflect", isMainThread ? "invokeMain" : "invokeChild", "(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V", false);
        mv.visitInsn(RETURN);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_NEW, 0, null, 0, null);
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        super.visitFrame(-1, numLocal, local, numStack, stack);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        String name = "com.tms.android.TsmKit";
        name = name.replaceAll("\\.", "/");
        name = String.format("%1s%2s%3s", "L", name, ";");
        name = name.replaceAll(" ", "");
        if (name.equals(descriptor)) {
            inject = true;
        }
        AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
        return new MyAnnotationVisitor(api, annotationVisitor);
    }

    private class MyAnnotationVisitor extends AnnotationVisitor {

        public MyAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
            super(api, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (!name.equals("main")) {
                isMainThread = true;
            }
            isMainThread = Boolean.parseBoolean(value.toString());
        }
    }

}
