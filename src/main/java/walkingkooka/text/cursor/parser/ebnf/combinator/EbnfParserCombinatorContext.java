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

import walkingkooka.Context;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRangeParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfRuleParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfTerminalParserToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

final class EbnfParserCombinatorContext<C extends ParserContext> implements Context {

    static <C extends ParserContext> EbnfParserCombinatorContext<C> with(final Function<EbnfIdentifierName, Optional<Parser<C>>> identifierToParser,
                                                                         final EbnfParserCombinatorSyntaxTreeTransformer<C> transformer) {
        return new EbnfParserCombinatorContext<>(
                Objects.requireNonNull(identifierToParser, "identifierToParser"),
                Objects.requireNonNull(transformer, "transformer")
        );
    }

    private EbnfParserCombinatorContext(final Function<EbnfIdentifierName, Optional<Parser<C>>> identifierToParser,
                                        final EbnfParserCombinatorSyntaxTreeTransformer<C> transformer) {
        super();
        this.providedIdentifierToParser = identifierToParser;
        this.transformer = transformer;

        this.nameToToken = Maps.sorted();
        this.tokenToProxy = Maps.hash();
    }

    /**
     * A user provided function that provides {@link EbnfIdentifierName} to external parsers.
     */
    final Function<EbnfIdentifierName, Optional<Parser<C>>> providedIdentifierToParser;

    /**
     * A user provided transformer which transforms {@link EbnfParserToken} and parsers.
     */
    final EbnfParserCombinatorSyntaxTreeTransformer<C> transformer;

    /**
     * Records a rule, detecting duplicate identifiers.
     */
    EbnfParserCombinatorsProxyGet<C> addRule(final EbnfRuleParserToken rule) {
        final EbnfIdentifierName identifier = rule.identifier()
                .value();
        if (this.providedIdentifierToParser.apply(identifier).isPresent()) {
            throw new EbnfParserCombinatorDuplicateRuleException(
                    "Rule " + identifier + " duplicated in provided parsers",
                    rule
            );
        }

        final EbnfParserCombinatorsProxyGet<C> get = this.add(
                identifier,
                rule
        );

        final EbnfParserCombinatorsProxy<C> duplicate = this.ruleIdentifierNameToProxy.put(
                identifier,
                get.proxy
        );
        if(null != duplicate) {
            throw new EbnfParserCombinatorDuplicateRuleException("Duplicate rule definition " + identifier, rule);
        }

        return get;
    }

    /**
     * Assumes the {@link EbnfIdentifierName} belongs to a {@link EbnfRuleParserToken} and tries to return a {@link Parser}.
     * This is useful for resolving {@link EbnfIdentifierParserToken} to rules and its parser.
     */
    Parser<C> ruleParser(final EbnfIdentifierName ruleName) {
        final EbnfParserCombinatorsProxy<C> proxy = ruleIdentifierNameToProxy.get(ruleName);
        if(null == proxy) {
            throw new IllegalArgumentException("Missing rule for " + ruleName);
        }

        return proxy.parser()
                .orElseThrow(()-> new IllegalStateException("Rule '" + ruleName + "' parser not available"));
    }

    /**
     * {@link EbnfRuleParserToken#identifier()} to {@link EbnfParserCombinatorsProxy}.
     */
    private final Map<EbnfIdentifierName, EbnfParserCombinatorsProxy<C>> ruleIdentifierNameToProxy = Maps.sorted();

    /**
     * This should be called once for each {@link EbnfIdentifierParserToken} that appears within a grammar by
     * {@link EbnfParserCombinatorsPrepareEbnfParserTokenVisitor#visit(EbnfIdentifierParserToken)}. The visit method
     * has a guard as identifiers may appear multiple times within a grammar.
     */
    EbnfParserCombinatorsProxyGet<C> addIdentifier(final EbnfIdentifierParserToken identifierParserToken) {
        final EbnfIdentifierName identifier = identifierParserToken.value();

        final EbnfParserCombinatorsProxyGet<C> got = this.add(
                identifier,
                identifierParserToken
        );

        final EbnfParserCombinatorsProxy<C> proxy = got.proxy;

        final EbnfIdentifierName identifierName = identifierParserToken.value();

        // GWT JRE missing Optional#ifPresentOrElse
        final Optional<Parser<C>> parser = this.providedIdentifierToParser.apply(identifierName);
        if (parser.isPresent()) {
            parser.ifPresent(proxy::setParser);
        } else {
            this.identifierToProxyWithoutParser.put(
                    identifierName,
                    proxy
            );
        }

        return got;
    }

    Optional<Parser<C>> tryIdentifierParser(final EbnfIdentifierParserToken token) {
        Optional<Parser<C>> parser = Optional.empty();

        final EbnfIdentifierName name = token.value();

        EbnfParserCombinatorsProxy<C> ruleProxy = this.ruleIdentifierNameToProxy.get(name);
        if(null != ruleProxy) {
            parser = ruleProxy.parser();
        }

        return parser;
    }

    /**
     * A Map of proxies for a {@link EbnfIdentifierName} missing a parser.
     */
    private final Map<EbnfIdentifierName, EbnfParserCombinatorsProxy<C>> identifierToProxyWithoutParser = Maps.sorted();

    /**
     * Adds and returns a proxy for the given {@link EbnfParserToken}.
     */
    private EbnfParserCombinatorsProxyGet<C> add(final EbnfIdentifierName name,
                                                 final EbnfParserToken token) {
        this.nameToToken.put(
                name,
                token
        );

        return this.proxy(token);
    }

    /**
     * This method is used to resolve identifiers to {@link EbnfTerminalParserToken} or {@link Parser}.
     */
    Optional<EbnfParserToken> tryFindNonIdentifierToken(final EbnfIdentifierName name) {
        final Map<EbnfIdentifierName, EbnfParserCombinatorsProxy<C>> ruleIdentifierNameToProxy = this.ruleIdentifierNameToProxy;

        EbnfParserToken notNameToken;
        EbnfIdentifierName tempName = name;

        do {
            final EbnfParserCombinatorsProxy<C> ruleProxy = ruleIdentifierNameToProxy.get(tempName);
            if(null == ruleProxy) {
                throw new IllegalArgumentException("Missing rule " + tempName);
            }
            tempName = null;

            notNameToken = ruleProxy.token.cast(EbnfRuleParserToken.class)
                    .assignment();

            if(notNameToken.isIdentifier()) {
                tempName = notNameToken.cast(EbnfIdentifierParserToken.class)
                        .value();
                notNameToken = null;
            }

        }while(null != tempName);


        return Optional.ofNullable(notNameToken);
    }

    /**
     * A {@link Map} of names to tokens, such as names to rule definitions.
     * This will be used to detect duplicates rule definitions.
     */
    final Map<EbnfIdentifierName, EbnfParserToken> nameToToken;

    /**
     * Creates a {@link EbnfParserCombinatorsProxy}, using and update the cache of {@link #tokenToProxy}.
     */
    EbnfParserCombinatorsProxyGet<C> proxy(final EbnfParserToken token) {
        if(token.isGrammar()) {
            throw new IllegalArgumentException("Proxy for grammar tokens are not supported=" + token);
        }
        boolean created;

        EbnfParserCombinatorsProxy<C> proxy;

        final Map<EbnfParserToken, EbnfParserCombinatorsProxy<C>> tokenToProxy = this.tokenToProxy;
        EbnfParserCombinatorsProxy<C> old = tokenToProxy.get(token);
        if (null != old) {
            proxy = this.tokenToProxy.get(token);
            created = false; // not created
        } else {
            // create a proxy for the new token
            proxy = EbnfParserCombinatorsProxy.with(
                    token,
                    this
            );
            tokenToProxy.put(
                    token,
                    proxy
            );
            this.missingParserCount++;

            // maybe create a proxy if an EbnfIdentifierParserToken
            created = true;
        }

        return EbnfParserCombinatorsProxyGet.with(
                proxy,
                created
        );
    }

    /**
     * A {@link Map} of tokens to a proxy, that is lazily populated
     */
    final Map<EbnfParserToken, EbnfParserCombinatorsProxy<C>> tokenToProxy;

    /**
     * Tries multiple times to create parsers for each and every token.
     */
    void tryCreatingParsers(final boolean ignoreCycles) {
        int before = this.missingParserCount;

        for(;;){
            for(final EbnfParserCombinatorsProxy<C> proxy : this.tokenToProxy.values()) {
                this.clear(ignoreCycles);

                if(null == proxy.parser) {
                    EbnfParserCombinatorsTransformEbnfParserTokenVisitor.transform(
                            proxy.token,
                            proxy,
                            this
                    );
                }
            }

            int after = this.missingParserCount;
            if(0 == after || after == before) {
                break; // give up no new parsers created in the last pass
            }

            before = after;
        }

    }

    void insertProxyParsersIfEbnfIdentifierParserToken() {
        for(final Entry<EbnfParserToken, EbnfParserCombinatorsProxy<C>> tokenAndProxy : this.tokenToProxy.entrySet()) {
            final EbnfParserCombinatorsProxy<C> proxy = tokenAndProxy.getValue();
            if(null != proxy.parser) {
                continue;
            }

            final EbnfParserToken token = tokenAndProxy.getKey();
            if(token.isIdentifier()) {
                final EbnfParserCombinatorProxyParser<C> proxyParser = EbnfParserCombinatorProxyParser.with(
                        token.cast(EbnfIdentifierParserToken.class)
                );
                tokenAndProxy.getValue()
                        .setParser(proxyParser);

                this.proxyParsers.add(proxyParser);
            }
        }
    }

    void missingParserCreated(final EbnfParserToken token) {
        if(token.isIdentifier()) {
            this.identifierToProxyWithoutParser.remove(
                token.cast(EbnfIdentifierParserToken.class).value()
            );
        }
        this.missingParserCount--;
    }

    /**
     * This value increases each time a {@link #tokenToProxy} is removed, and is used to track change counts.
     */
    private int missingParserCount = 0;

    /**
     * Set the {@link EbnfParserCombinatorsProxy#parser} using the identifier. This should happen late in the process.
     */
    void fixIdentifierToProxyWithoutParser() {
        for(final Entry<EbnfIdentifierName, EbnfParserCombinatorsProxy<C>> identifierNameAndProxy : this.identifierToProxyWithoutParser.entrySet()) {
            final EbnfParserCombinatorsProxy<C> proxy = identifierNameAndProxy.getValue();
            final EbnfIdentifierName name = identifierNameAndProxy.getKey();

            if(null != proxy.parser) {
                throw new IllegalStateException("Parser present for " + name + " while fixing outstanding identifier references");
            }
            proxy.setParser(
                    this.ruleParser(name)
            );
        }
    }

    void fixProxyParsers() {
        for(final EbnfParserCombinatorProxyParser<C> proxyParser : this.proxyParsers) {
            final EbnfIdentifierParserToken identifierParserToken = proxyParser.identifier;
            final EbnfIdentifierName name = identifierParserToken.value();

            final Parser<C> parser = EbnfParserCombinatorOptionalParser.unwrapIfNecessary(
                    this.ruleParser(name)
            );

            // dont need to re-wrap if was optional
            proxyParser.setParser(
                    this.transformer.identifier(
                            identifierParserToken,
                            parser
                    )
            );
        }
    }

    private final List<EbnfParserCombinatorProxyParser<C>> proxyParsers = new ArrayList<>();

    /**
     * Resolves a given {@link EbnfIdentifierName} into text, which is used to resolve the begin and end text
     * before passing them as arguments to {@link EbnfParserCombinatorSyntaxTreeTransformer#range(EbnfRangeParserToken, String, String).}
     */
    Optional<String> terminal(final EbnfRangeParserToken range,
                              final boolean begin) {
        String text = null;

        final EbnfParserToken rangeBeginOrEnd = begin ?
                range.begin() :
                range.end();

        EbnfParserToken token = rangeBeginOrEnd;
        if (rangeBeginOrEnd.isIdentifier()) {
            token = this.tryFindNonIdentifierToken(
                    rangeBeginOrEnd.cast(EbnfIdentifierParserToken.class)
                            .value()
            ).orElse(null);
        }

        if (null != token ) {
            if( token.isTerminal()) {
                // the raw value as #text will include the double quotes etc, so use #value
                text = token.cast(EbnfTerminalParserToken.class)
                        .value();
            } else {
                throw new IllegalArgumentException(
                        "Invalid range " +
                                (begin? "begin" : "end") +
                                ", expected identifier or terminal but got " + this.label(rangeBeginOrEnd) +
                                "=" +
                                rangeBeginOrEnd
                );
            }
        }

        return Optional.ofNullable(text);
    }

    // nameToParser.....................................................................................................

    /**
     * Builds a {@link Function} that takes a {@link EbnfIdentifierName} and returns a {@link Parser} or empty if absent or unknown.
     */
    Function<EbnfIdentifierName, Optional<Parser<C>>> nameToParser() {
        final Map<EbnfIdentifierName, Parser<C>> nameToParser = Maps.sorted();

        for(final EbnfParserCombinatorsProxy<C> proxy : this.tokenToProxy.values()) {
            if(null == proxy.parser) {
                throw new IllegalStateException("Missing parser for " + proxy.token);
            }

            final EbnfParserToken token = proxy.token;
            EbnfIdentifierName name = null;

            if(token.isIdentifier()) {
                name = token.cast(EbnfIdentifierParserToken.class).value();
            }
            if(token.isRule()) {
                name = token.cast(EbnfRuleParserToken.class)
                        .identifier()
                        .value();
            }

            if(null != name) {
                nameToParser.put(
                        name,
                        EbnfParserCombinatorOptionalParser.unwrapIfNecessary(
                                proxy.parser
                        )
                );
            }
        }

        return (n) -> {
            Optional<Parser<C>> parser = this.providedIdentifierToParser.apply(n);

            if(false == parser.isPresent()) {
                parser = Optional.ofNullable(
                        nameToParser.get(n)
                );
            }

            return parser;
        };
    }

    // cycle detection..................................................................................................

    /**
     * Resets this context, useful when trying to detect cycles within a token.
     */
    void clear(final boolean ignoreCycles) {
        this.seen = ignoreCycles ?
                null :
                Sets.hash();
    }

    // returns true if inside a cycle
    boolean add(final EbnfParserCombinatorsProxy<?> proxy) {
        final Set<EbnfParserCombinatorsProxy<?>> seen = this.seen;

        return null != seen && false == seen.add(proxy);
    }

    /**
     * Will be none when cycles should be ignored.
     */
    private Set<EbnfParserCombinatorsProxy<?>> seen;

    // helpers..........................................................................................................

    /**
     * Computes the label for the given {@link EbnfParserToken} into a human friendly form for messages.
     */
    private String label(final EbnfParserToken token) {
        return token.getClass()
                .getSimpleName()
                .replace("Ebnf", "")
                .replace(ParserToken.class.getSimpleName(), "");
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .enable(ToStringBuilderOption.SKIP_IF_DEFAULT_VALUE)
                .label("nameToToken")
                .value(this.nameToToken)
                .label("tokenToProxy")
                .value(this.tokenToProxy)
                .label("ruleIdentifierNameToProxy")
                .value(this.ruleIdentifierNameToProxy)
                .label("identifierToProxyWithoutParser")
                .value(this.identifierToProxyWithoutParser)
                .label("providedIdentifierToProxy")
                .value(this.providedIdentifierToParser)
                .label("missingParserCount")
                .value(this.missingParserCount)
                .label("transformer")
                .value(this.transformer)
                .label("seen")
                .value(this.seen)
                .build();
    }
}
