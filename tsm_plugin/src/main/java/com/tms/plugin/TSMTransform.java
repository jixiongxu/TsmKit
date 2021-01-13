package com.tms.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.tms.plugin.asm.MainProcess;

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
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        //消费型输入，可以从中获取jar包和class文件夹路径。需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //引用型输入，无需输出。
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();
        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for(TransformInput input : inputs) {
            for(DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(),
                        Format.DIRECTORY);
                //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                System.out.println("我要插装了 -"+directoryInput.getFile().getAbsolutePath());
                transformSingleFile(directoryInput.getFile());
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }
    }

    public static void transformSingleFile(File file) {
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
                            System.out.println("插装文件 -"+f.getAbsolutePath());
                            MainProcess.handlerClass(f.getAbsolutePath(), f.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

}
