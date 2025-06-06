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
import walkingkooka.visit.Visiting;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public class RepeatedEbnfParserTokenTest extends GroupOptionalRepeatParentEbnfParserTokenTestCase<RepeatedEbnfParserToken> {

    @Test
    public void testAccept() {
        final StringBuilder b = new StringBuilder();
        final List<ParserToken> visited = Lists.array();

        final RepeatedEbnfParserToken repeated = this.createToken();
        final IdentifierEbnfParserToken identifier1 = this.identifier1();

        new FakeEbnfParserTokenVisitor() {
            @Override
            protected Visiting startVisit(final ParserToken t) {
                b.append("1");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ParserToken t) {
                b.append("2");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final EbnfParserToken t) {
                b.append("3");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final EbnfParserToken t) {
                b.append("4");
                visited.add(t);
            }

            @Override
            protected Visiting startVisit(final RepeatedEbnfParserToken t) {
                assertSame(repeated, t);
                b.append("5");
                visited.add(t);
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final RepeatedEbnfParserToken t) {
                assertSame(repeated, t);
                b.append("6");
                visited.add(t);
            }

            @Override
            protected void visit(final IdentifierEbnfParserToken t) {
                b.append("7");
                visited.add(t);
            }
        }.accept(repeated);
        this.checkEquals("13513742642", b.toString());
        this.checkEquals(Lists.<Object>of(repeated, repeated, repeated,
                        identifier1, identifier1, identifier1, identifier1, identifier1,
                        repeated, repeated, repeated),
                visited,
                "visited");
    }

    @Override
    RepeatedEbnfParserToken createToken(final String text, final List<ParserToken> tokens) {
        return RepeatedEbnfParserToken.with(tokens, text);
    }

    @Override
    String openChar() {
        return "(";
    }

    @Override
    String closeChar() {
        return ")";
    }

    @Override
    public Class<RepeatedEbnfParserToken> type() {
        return RepeatedEbnfParserToken.class;
    }
}
