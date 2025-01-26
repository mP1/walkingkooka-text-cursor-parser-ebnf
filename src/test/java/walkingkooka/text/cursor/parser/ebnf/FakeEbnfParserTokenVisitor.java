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

import walkingkooka.test.Fake;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

abstract class FakeEbnfParserTokenVisitor extends EbnfParserTokenVisitor implements Fake {

    @Override
    protected Visiting startVisit(final GrammarEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final GrammarEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final AlternativeEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final AlternativeEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final ConcatenationEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final ConcatenationEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final ExceptionEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final ExceptionEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final GroupEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final GroupEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final OptionalEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final OptionalEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final RangeEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final RangeEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final RepeatedEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final RepeatedEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected Visiting startVisit(final RuleEbnfParserToken token) {
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final RuleEbnfParserToken token) {
        super.endVisit(token);
    }

    @Override
    protected void visit(final CommentEbnfParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final IdentifierEbnfParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SymbolEbnfParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final TerminalEbnfParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final WhitespaceEbnfParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final EbnfParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final EbnfParserToken token) {
        throw new UnsupportedOperationException();
    }
}
