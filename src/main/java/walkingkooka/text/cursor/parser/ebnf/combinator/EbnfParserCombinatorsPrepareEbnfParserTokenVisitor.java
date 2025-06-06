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
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.ebnf.AlternativeEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.ConcatenationEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserTokenVisitor;
import walkingkooka.text.cursor.parser.ebnf.ExceptionEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.GroupEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.IdentifierEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.OptionalEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RangeEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RepeatedEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.RuleEbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.TerminalEbnfParserToken;
import walkingkooka.visit.Visiting;

import java.util.Objects;

/**
 * Transforms all {@link EbnfParserToken} and gathers all {@link EbnfParserCombinatorsProxy} for each token and grammar name definitions.
 */
final class EbnfParserCombinatorsPrepareEbnfParserTokenVisitor<C extends ParserContext> extends EbnfParserTokenVisitor {

    static <C extends ParserContext> EbnfParserCombinatorsPrepareEbnfParserTokenVisitor<C> with(final EbnfParserCombinatorContext<C> context) {
        Objects.requireNonNull(context, "context");

        return new EbnfParserCombinatorsPrepareEbnfParserTokenVisitor<>(context);
    }

    // @VisibleForTesting
    EbnfParserCombinatorsPrepareEbnfParserTokenVisitor(final EbnfParserCombinatorContext<C> context) {
        super();
        this.context = context;

        this.ancestors = Stacks.jdk();
    }

    // DONT! push/pop a proxy or set this.parent for grammar tokens

    // RULE ............................................................................................................

    @Override
    protected Visiting startVisit(final RuleEbnfParserToken rule) {
        this.context.addRule(rule);

        this.pushParentAndCreateProxy(rule);

        rule.assignment()
                .accept(this);

        return Visiting.SKIP;
    }

    @Override
    protected void endVisit(final RuleEbnfParserToken token) {
        this.popParent();
    }


    // ALT .............................................................................................................

    @Override
    protected Visiting startVisit(final AlternativeEbnfParserToken token) {
        return this.pushParentAndCreateProxy(token)
                .toVisiting();
    }

    @Override
    protected void endVisit(final AlternativeEbnfParserToken token) {
        this.popParent();
    }

    // CONCAT ..........................................................................................................

    @Override
    protected Visiting startVisit(final ConcatenationEbnfParserToken token) {
        return this.pushParentAndCreateProxy(token)
                .toVisiting();
    }

    @Override
    protected void endVisit(final ConcatenationEbnfParserToken token) {
        this.popParent();
    }

    // EXCEPTION ........................................................................................................

    @Override
    protected Visiting startVisit(final ExceptionEbnfParserToken token) {
        return this.pushParentAndCreateProxy(token)
                .toVisiting();
    }

    @Override
    protected void endVisit(final ExceptionEbnfParserToken token) {
        this.popParent();
    }

    // GROUP ...........................................................................................................

    @Override
    protected Visiting startVisit(final GroupEbnfParserToken token) {
        return this.pushParentAndCreateProxy(token)
                .toVisiting();
    }

    @Override
    protected void endVisit(final GroupEbnfParserToken token) {
        this.popParent();
    }

    // OPT .............................................................................................................

    @Override
    protected Visiting startVisit(final OptionalEbnfParserToken token) {
        return this.pushParentAndCreateProxy(token)
                .toVisiting();
    }

    @Override
    protected void endVisit(final OptionalEbnfParserToken token) {
        this.popParent();
    }

    // RANGE ...........................................................................................................

    @Override
    protected Visiting startVisit(final RangeEbnfParserToken token) {
        return this.pushParentAndCreateProxy(token)
                .toVisiting();
    }

    @Override
    protected void endVisit(final RangeEbnfParserToken token) {
        this.popParent();
    }

    // REPEAT ........................................................................................................

    @Override
    protected Visiting startVisit(final RepeatedEbnfParserToken token) {
        return this.pushParentAndCreateProxy(token)
                .toVisiting();
    }

    @Override
    protected void endVisit(final RepeatedEbnfParserToken token) {
        this.popParent();
    }

    // IDENTIFIER .......................................................................................................

    @Override
    protected void visit(final IdentifierEbnfParserToken token) {
        if(this.proxy(token).created) {
            this.context.addIdentifier(token);
        }
    }

    // TERMINAL ........................................................................................................

    @Override
    protected void visit(final TerminalEbnfParserToken token) {
        this.proxy(token)
                .proxy
                .setParser(
                        this.context.transformer.terminal(
                                token,
                                Parsers.string(
                                        token.value(),
                                        CaseSensitivity.SENSITIVE
                                )
                        )
                );
    }

    // helpers..........................................................................................................

    private EbnfParserCombinatorsProxyGet<C> proxy(final EbnfParserToken token) {
        final EbnfParserCombinatorsProxyGet<C> got = this.context.proxy(token);

        final EbnfParserCombinatorsProxy<C> oldParent = this.parent;
        if (null != oldParent) {
            oldParent.children.add(
                    got.proxy
            );
        }
        return got;
    }

    private EbnfParserCombinatorsProxyGet<C> pushParentAndCreateProxy(final EbnfParserToken token) {
        // save/push the current parent which will be popped in all #endVisit
        {
            final EbnfParserCombinatorsProxy<C> parent = this.parent;
            if (null != parent) {
                this.ancestors.push(parent);
            }
        }

        final EbnfParserCombinatorsProxyGet<C> got = this.proxy(token);

        this.parent = got.proxy;

        return got;
    }

    private void popParent() {
        EbnfParserCombinatorsProxy<C> parent = null;

        if (this.ancestors.isNotEmpty()) {
            parent = this.ancestors.peek();
            this.ancestors.pop();
        }

        this.parent = parent;
    }

    private final EbnfParserCombinatorContext<C> context;

    /**
     * The current parent which changes during visiting.
     */
    private EbnfParserCombinatorsProxy<C> parent;

    /**
     * Used by all #endVisit methods to restore the parent proxy. When visiting is complete this should be empty and ignored.
     */
    private final Stack<EbnfParserCombinatorsProxy<C>> ancestors;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .label("context")
                .value(this.context)
                .label("parent")
                .value(this.parent)
                .build();
    }
}