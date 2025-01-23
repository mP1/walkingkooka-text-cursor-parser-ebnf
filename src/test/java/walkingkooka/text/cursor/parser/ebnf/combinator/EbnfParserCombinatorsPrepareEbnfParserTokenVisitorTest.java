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

package walkingkooka.text.cursor.parser.ebnf.combinator;

import walkingkooka.Cast;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserContext;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserTokenVisitorTesting;

public final class EbnfParserCombinatorsPrepareEbnfParserTokenVisitorTest implements EbnfParserTokenVisitorTesting<EbnfParserCombinatorsPrepareEbnfParserTokenVisitor<EbnfParserContext>, EbnfParserToken> {

    @Override
    public EbnfParserCombinatorsPrepareEbnfParserTokenVisitor<EbnfParserContext> createVisitor() {
        return new EbnfParserCombinatorsPrepareEbnfParserTokenVisitor<EbnfParserContext>(
                EbnfParserCombinatorContext.with(
                        (n) -> {
                            throw new UnsupportedOperationException();
                        },
                        new FakeEbnfParserCombinatorGrammarTransformer<>()
                )
        );
    }

    // class............................................................................................................

    @Override
    public Class<EbnfParserCombinatorsPrepareEbnfParserTokenVisitor<EbnfParserContext>> type() {
        return Cast.to(EbnfParserCombinatorsPrepareEbnfParserTokenVisitor.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return EbnfParserCombinators.class.getSimpleName();
    }
}
