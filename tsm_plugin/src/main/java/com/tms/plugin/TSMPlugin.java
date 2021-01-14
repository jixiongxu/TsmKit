package com.tms.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TSMPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        AppExtension android = project.getExtensions().findByType(AppExtension.class);
        if (android != null) {
            android.registerTransform(new TSMTransform(project));
        }
    }
}
