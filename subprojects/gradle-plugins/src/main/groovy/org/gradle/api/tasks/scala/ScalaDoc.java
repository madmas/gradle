/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.tasks.scala;

import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.util.GUtil;

import java.io.File;

/**
 * Task to generate Scala API documentation.
 */
public class ScalaDoc extends SourceTask {

    private File destinationDir;

    private FileCollection classpath;
    private AntScalaDoc antScalaDoc;
    private ScalaDocOptions scalaDocOptions = new ScalaDocOptions();
    private String title;

    public AntScalaDoc getAntScalaDoc() {
        if (antScalaDoc == null) {
            antScalaDoc = new AntScalaDoc(getAnt());
        }
        return antScalaDoc;
    }

    public void setAntScalaDoc(AntScalaDoc antScalaDoc) {
        this.antScalaDoc = antScalaDoc;
    }

    @OutputDirectory
    public File getDestinationDir() {
        return destinationDir;
    }

    public void setDestinationDir(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    /**
     * <p>Returns the classpath to use to locate classes referenced by the documented source.</p>
     *
     * @return The classpath.
     */
    @InputFiles
    public Iterable<File> getClasspath() {
        return classpath;
    }

    public void setClasspath(FileCollection classpath) {
        this.classpath = classpath;
    }

    public ScalaDocOptions getScalaDocOptions() {
        return scalaDocOptions;
    }

    public void setScalaDocOptions(ScalaDocOptions scalaDocOptions) {
        this.scalaDocOptions = scalaDocOptions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @TaskAction
    protected void generate() {
        ScalaDocOptions options = getScalaDocOptions();
        if (!GUtil.isTrue(options.getDocTitle())) {
            options.setDocTitle(getTitle());
        }
        if (!GUtil.isTrue(options.getWindowTitle())) {
            options.setWindowTitle(getTitle());
        }
        getAntScalaDoc().execute(getSource(), getDestinationDir(), getClasspath(), options);
    }

}
