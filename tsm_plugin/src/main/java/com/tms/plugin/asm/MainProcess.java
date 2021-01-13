package com.tms.plugin.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainProcess {

    // handler class to process
    public static void handlerClass(String input, String output) {
        try {
            FileInputStream fis = new FileInputStream(new File(input));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            ClassReader classReader = new ClassReader(buffer);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            TSMClassVisitor visitor = new TSMClassVisitor(Opcodes.ASM7, classWriter);
            classReader.accept(visitor, ClassWriter.COMPUTE_FRAMES);

            FileOutputStream fos = new FileOutputStream(new File(output));
            fos.write(classWriter.toByteArray());
            fos.flush();
        } catch (Exception e) {
            System.out.println("tsm error：" + input);
            System.out.println("tsm error：" + e.getMessage());
        }
    }
}
