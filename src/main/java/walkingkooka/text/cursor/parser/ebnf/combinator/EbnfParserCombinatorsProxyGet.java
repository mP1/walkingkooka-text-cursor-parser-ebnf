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

import walkingkooka.ToStringBuilder;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.visit.Visiting;

/**
 * The result returned by {@link EbnfParserCombinatorContext#proxy}.
 */
final class EbnfParserCombinatorsProxyGet<C extends ParserContext> {

    static <C extends ParserContext> EbnfParserCombinatorsProxyGet<C> with(final EbnfParserCombinatorsProxy<C> proxy,
                                                                           final boolean created) {
        return new EbnfParserCombinatorsProxyGet<>(
                proxy,
                created
        );
    }

    private EbnfParserCombinatorsProxyGet(final EbnfParserCombinatorsProxy<C> proxy,
                                          final boolean created) {
        this.proxy = proxy;
        this.created = created;
    }

    final EbnfParserCombinatorsProxy<C> proxy;

    /**
     * True when the proxy was created, if a cached proxy then false
     */
    final boolean created;

    Visiting toVisiting() {
        return this.created ?
                Visiting.CONTINUE :
                Visiting.SKIP;
    }

    public String toString() {
        return ToStringBuilder.empty()
                .label("created")
                .value(this.created)
                .value(this.proxy)
                .build();
    }
}
