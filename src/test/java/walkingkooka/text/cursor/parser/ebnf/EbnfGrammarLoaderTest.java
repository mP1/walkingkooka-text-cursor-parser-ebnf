/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.text.cursor.parser.ebnf;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class EbnfGrammarLoaderTest implements ClassTesting2<EbnfGrammarLoader> {

    @Test
    public void testLoad() {
        final EbnfGrammarLoader loader = EbnfGrammarLoader.with(EbnfGrammarLoaderTest.class.getSimpleName() + "/testLoad.txt", this.getClass());
        assertSame(loader.grammar(), loader.grammar());
    }

    @Test
    public void testLoadFails() {
        final EbnfGrammarLoader loader = EbnfGrammarLoader.with(EbnfGrammarLoaderTest.class.getSimpleName() + "/testFail.txt", this.getClass());
        assertThrows(RuntimeException.class, () -> loader.grammar());
        assertThrows(RuntimeException.class, () -> loader.grammar());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<EbnfGrammarLoader> type() {
        return EbnfGrammarLoader.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
