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
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AlternativeConcatenationParentEbnfParserTokenTestCase<T extends ParentEbnfParserToken> extends ParentEbnfParserTokenTestCase2<T> {

    AlternativeConcatenationParentEbnfParserTokenTestCase() {
        super();
    }

    @Test
    public final void testOnlyOneTokenIgnoringCommentsSymbolsWhitespaceFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken(this.text(), this.identifier("first"), this.comment2()));
    }

    @Override final List<ParserToken> tokens() {
        return Lists.of(this.identifier1(), this.identifier2());
    }

    @Override
    public T createDifferentToken() {
        return this.createToken("diff1" + separatorChar() + "diff2", this.identifier("diff1"), this.identifier("diff2"));
    }

    abstract char separatorChar();
}
