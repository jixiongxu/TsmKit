package com.tms.plugin.asm;

import org.objectweb.asm.*;

public class TSMClassVisitor extends ClassVisitor {

    private static final String TAG = "TSMClassVisitor--->";

    private int api;

    public TSMClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
        this.api = api;
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        MethodVisitor methodVisitor = super.visitMethod(i, s, s1, s2, strings);
        return new TSMMethodVisitor(api, methodVisitor, i, s, s1);
    }

}
