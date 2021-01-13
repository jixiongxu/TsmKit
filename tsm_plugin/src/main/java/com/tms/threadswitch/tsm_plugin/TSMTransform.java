package com.tms.threadswitch.tsm_plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.tms.threadswitch.tsm_plugin.asm.MainProcess;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

/**
 * Process class object
 * <p>
 * add tsm logic
 */
public class TSMTransform extends Transform {

    private static final String TAG = "TSMTransform";

    public TSMTransform(Project project) {

    }

    @Override
    public String getName() {
        return "tsm_transform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<QualifiedContent.ScopeType> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws RuntimeException, IOException {
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                transformSingleFile(directoryInput.getFile());
            }
        }
    }

    private void transformSingleFile(File inputFile) {
        folderMethod1(inputFile);
    }

    public static void folderMethod1(File file) {
        if (file.exists()) {
            if (null == file.listFiles()) {
                return;
            }
            LinkedList<File> list = new LinkedList<>(Arrays.asList(Objects.requireNonNull(file.listFiles())));
            while (!list.isEmpty()) {
                File[] files = list.removeFirst().listFiles();
                if (null == files) {
                    continue;
                }
                for (File f : files) {
                    if (f.isDirectory()) {
                        list.add(f);
                    } else {
                        if (f.getAbsolutePath().endsWith(".class")) {
                            MainProcess.handlerClass(f.getAbsolutePath(), f.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

}
