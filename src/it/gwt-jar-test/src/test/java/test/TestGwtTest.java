package test;

import com.google.gwt.junit.client.GWTTestCase;

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ebnf.EbnfIdentifierName;
import walkingkooka.text.cursor.parser.ebnf.EbnfParserToken;

@walkingkooka.j2cl.locale.LocaleAware
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

    public void testEbnfParserTokenParse() {
        final String grammar = "TEST1=\"abc\";";

        this.checkEquals(
                EbnfParserToken.grammar(
                        Lists.of(
                                EbnfParserToken.rule(
                                        Lists.<ParserToken>of( // type param required by gwtc
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
                ),
                EbnfParserToken.parse(grammar)
        );
    }

    private void checkEquals(final Object expected,
                             final Object actual) {
        assertEquals(
                expected,
                actual
        );
    }
}
