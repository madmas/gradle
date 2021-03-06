/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.integtests

import org.junit.Test
import org.junit.runner.RunWith
import org.gradle.util.TestFile

/**
 * @author Hans Dockter
 */

@RunWith (DistributionIntegrationTestRunner.class)
class SamplesJavaCustomizedLayoutIntegrationTest {
    // Injected by test runner
    private GradleDistribution dist;
    private GradleExecuter executer;

    @Test
    public void canBuildAndUploadJar() {
        TestFile javaprojectDir = dist.samplesDir.file('java/customizedLayout')
                                                      
        // Build and test projects
        executer.inDirectory(javaprojectDir).withTasks('clean', 'build', 'uploadArchives').run()

        // Check tests have run
        JUnitTestResult result = new JUnitTestResult(javaprojectDir)
        result.assertTestClassesExecuted('org.gradle.PersonTest')

        // Check jar exists
        javaprojectDir.file('build/libs/customizedLayout.jar').assertIsFile()

        // Check contents of Jar
        TestFile jarContents = dist.testDir.file('jar')
        javaprojectDir.file('build/libs/customizedLayout.jar').unzipTo(jarContents)
        jarContents.assertHasDescendants(
                'META-INF/MANIFEST.MF',
                'org/gradle/Person.class'
        )
    }
}