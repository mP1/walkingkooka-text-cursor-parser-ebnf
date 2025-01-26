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

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokenVisitor;
import walkingkooka.visit.Visiting;

public abstract class EbnfParserTokenVisitor extends ParserTokenVisitor {

    // GrammarEbnfParserToken....................................................................................

    protected Visiting startVisit(final GrammarEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GrammarEbnfParserToken token) {
        // nop
    }

    // EbnfParentParserTokens.........................................................................................

    // AlternativeEbnfParserToken ....................................................................................

    protected Visiting startVisit(final AlternativeEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final AlternativeEbnfParserToken token) {
        // nop
    }

    // ConcatenationEbnfParserToken....................................................................................

    protected Visiting startVisit(final ConcatenationEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ConcatenationEbnfParserToken token) {
        // nop
    }

    // ExceptionEbnfParserToken....................................................................................

    protected Visiting startVisit(final ExceptionEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ExceptionEbnfParserToken token) {
        // nop
    }

    // GroupEbnfParserToken....................................................................................

    protected Visiting startVisit(final GroupEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final GroupEbnfParserToken token) {
        // nop
    }

    // OptionalEbnfParserToken....................................................................................

    protected Visiting startVisit(final OptionalEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final OptionalEbnfParserToken token) {
        // nop
    }

    // RangeEbnfParserToken....................................................................................

    protected Visiting startVisit(final RangeEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final RangeEbnfParserToken token) {
        // nop
    }

    // RepeatedEbnfParserToken....................................................................................

    protected Visiting startVisit(final RepeatedEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final RepeatedEbnfParserToken token) {
        // nop
    }

    // RuleEbnfParserToken....................................................................................

    protected Visiting startVisit(final RuleEbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final RuleEbnfParserToken token) {
        // nop
    }

    // LeafEbnfParserToken ....................................................................................

    final void acceptComment(final CommentEbnfParserToken token) {
        if (Visiting.CONTINUE == this.startVisit(token)) {
            this.visit(token);
        }
    }

    protected void visit(final CommentEbnfParserToken token) {
        // nop
    }

    final void acceptIdentifier(final IdentifierEbnfParserToken token) {
        if (Visiting.CONTINUE == this.startVisit(token)) {
            this.visit(token);
        }
        this.endVisit(token);
    }

    protected void visit(final IdentifierEbnfParserToken token) {
        // nop
    }

    protected void visit(final SymbolEbnfParserToken token) {
        // nop
    }

    protected void visit(final TerminalEbnfParserToken token) {
        // nop
    }

    protected void visit(final WhitespaceEbnfParserToken token) {
        // nop
    }

    // ParserToken.......................................................................

    @Override
    protected Visiting startVisit(final ParserToken token) {
        return Visiting.CONTINUE;
    }

    @Override
    protected void endVisit(final ParserToken token) {
        // nop
    }

    // EbnfParserToken.......................................................................

    protected Visiting startVisit(final EbnfParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final EbnfParserToken token) {
        // nop
    }
}
