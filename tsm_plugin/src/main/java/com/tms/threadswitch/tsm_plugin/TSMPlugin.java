package com.tms.threadswitch.tsm_plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TSMPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        AppExtension android = project.getExtensions().findByType(AppExtension.class);
        TSMExtension mTsmExtension = project.getExtensions().create("tsm", TSMExtension.class);
        if (android != null) {
            android.registerTransform(new TSMTransform(project));
        }
    }
}
