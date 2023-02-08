package test;

import com.google.gwt.junit.client.GWTTestCase;

import walkingkooka.collect.list.Lists;
import walkingkooka.j2cl.locale.LocaleAware;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserContexts;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

import java.util.Optional;

@LocaleAware
public class TestGwtTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "test.Test";
    }

    public void testAssertEquals() {
        assertEquals(
                1,
                1
        );
    }

    public void testEbnfGrammarParser() {
        final String grammar = "TEST1=\"abc\";";

        final TextCursor grammarFile = TextCursors.charSequence(grammar);
        final Optional<ParserToken> parsed = EbnfParserToken.grammarParser()
                .orFailIfCursorNotEmpty(ParserReporters.basic())
                .parse(
                        grammarFile,
                        EbnfParserContexts.basic()
                );

        final EbnfParserToken token = EbnfParserToken.grammar(
                Lists.of(
                        EbnfParserToken.rule(
                                Lists.<ParserToken>of(
                                        EbnfParserToken.identifier(
                                                EbnfIdentifierName.with("TEST1"),
                                                "TEST1"
                                        ),
                                        EbnfParserToken.symbol("=", "="),
                                        EbnfParserToken.terminal("abc", "\"abc\""),
                                        EbnfParserToken.symbol(";", ";")
                                ),
                                grammar
                        )
                ),
                grammar
        );

        assertEquals(
                Optional.of(token),
                parsed
        );
    }
}
