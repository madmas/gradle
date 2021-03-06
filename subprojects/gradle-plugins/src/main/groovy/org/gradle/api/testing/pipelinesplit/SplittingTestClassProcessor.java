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

package org.gradle.api.testing.pipelinesplit;

import org.gradle.api.internal.tasks.testing.TestResultProcessor;
import org.gradle.api.specs.Spec;
import org.gradle.api.testing.TestClassProcessor;
import org.gradle.api.testing.fabric.TestClassRunInfo;

import java.util.Map;

/**
 * @author Tom Eyckmans
 */
public class SplittingTestClassProcessor implements TestClassProcessor {
    private final Map<? extends Spec<TestClassRunInfo>, ? extends TestClassProcessor> pipelineMatchers;

    public SplittingTestClassProcessor(Map<? extends Spec<TestClassRunInfo>, ? extends TestClassProcessor> pipelineMatchers) {
        this.pipelineMatchers = pipelineMatchers;
    }

    public void startProcessing(TestResultProcessor resultProcessor) {
        for (TestClassProcessor processor : pipelineMatchers.values()) {
            processor.startProcessing(resultProcessor);
        }
    }

    public void endProcessing() {
        for (TestClassProcessor processor : pipelineMatchers.values()) {
            processor.endProcessing();
        }
    }

    public void processTestClass(TestClassRunInfo testClass) {
        for (Map.Entry<? extends Spec<TestClassRunInfo>, ? extends TestClassProcessor> entry : pipelineMatchers.entrySet()) {
            Spec<TestClassRunInfo> currentMatcher = entry.getKey();
            if (currentMatcher.isSatisfiedBy(testClass)) {
                TestClassProcessor processor = entry.getValue();
                processor.processTestClass(testClass);
                return;
            }
        }

        throw new IllegalArgumentException(String.format("No processor is available for test class %s.", testClass));
    }
}
