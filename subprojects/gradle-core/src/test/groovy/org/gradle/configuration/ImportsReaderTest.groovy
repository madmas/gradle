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
 
package org.gradle.configuration

import org.gradle.util.TemporaryFolder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import org.gradle.StartParameter
import org.gradle.groovy.scripts.ScriptSource

/**
 * @author Hans Dockter
 */
class ImportsReaderTest {
    static final String NL = System.properties['line.separator']
    
    static final String TEST_DEFAULT_IMPORTS = "import a.b.*$NL"
    static final String TEST_PROJECT_IMPORTS = "import c.d.*$NL"

    ImportsReader testObj
    File testDir
    File testDefaultImportsFile
    File testProjectImportsFile
    @Rule public TemporaryFolder tmpDir = new TemporaryFolder();

    @Before public void setUp()  {
        testDir = tmpDir.dir
        (testDefaultImportsFile = new File(testDir, 'defaultImports')).write(TEST_DEFAULT_IMPORTS)
        (testProjectImportsFile = new File(testDir, StartParameter.IMPORTS_FILE_NAME)).write(TEST_PROJECT_IMPORTS)
        testObj = new ImportsReader(testDefaultImportsFile)
    }

    @Test public void testInit() {
        assertEquals(testDefaultImportsFile, testObj.defaultImportsFile)
    }

    @Test public void testReadImports() {
        String result = testObj.getImports(testDir)
        assertEquals(TEST_DEFAULT_IMPORTS + TEST_PROJECT_IMPORTS, result)
    }

    @Test public void testReadImportsWithNullDefaultImportsFile() {
        testObj.defaultImportsFile = null
        String result = testObj.getImports(testDir)
        assertEquals(TEST_PROJECT_IMPORTS, result)
    }

    @Test public void testReadImportsWithNonExistingProjectImportsFile() {
        testProjectImportsFile.delete()
        String result = testObj.getImports(testDir)
        assertEquals(TEST_DEFAULT_IMPORTS, result)
    }

    @Test public void testReadImportsWithNonExistingProjectImportsAndNullDefaultsImportsFile() {
        testObj.defaultImportsFile = null
        testProjectImportsFile.delete()
        String result = testObj.getImports(testDir)
        assertEquals('', result)
    }

    @Test public void testCreatesScriptSource() {
        ScriptSource source = [:] as ScriptSource
        ScriptSource importsSource = testObj.withImports(source, testDir)
        assertThat(importsSource, instanceOf(ImportsScriptSource.class))
        assertThat(importsSource.source, sameInstance(source))
        assertThat(importsSource.importsReader, sameInstance(testObj))
        assertThat(importsSource.rootDir, equalTo(testDir))
    }
}
