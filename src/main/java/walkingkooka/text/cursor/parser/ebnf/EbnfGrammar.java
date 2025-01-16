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

import walkingkooka.reflect.PublicStaticHelper;

/**
 * Contains constants used to define the EBNF grammar.
 */
public final class EbnfGrammar implements PublicStaticHelper {

    /**
     * <pre>
     * rhs , "|" , rhs
     * </pre>
     * To avoid left recursion problems the first rhs is replaced as RHS2
     */
    public final static String ALTERNATIVE = "|";

    /**
     * <pre>
     * lhs , "=" , rhs , ";" ;
     * </pre>
     */
    public final static String ASSIGN = "=";

    public final static String COMMENT_OPEN = "(*";

    public final static String COMMENT_CLOSE = "*)";

    /**
     * <pre>
     * | rhs , "," , rhs ;
     * </pre>
     * To avoid left recursion problems the first rhs is replaced as RHS2
     */
    public final static String CONCATENATION = ",";

    /**
     * <pre>
     * "-" , rhs
     * </pre>
     */
    public final static String EXCEPTION = "-";

    /**
     * <pre>
     * "(" , rhs , ")"
     * </pre>
     */
    public final static String GROUP_OPEN = "(";

    /**
     * <pre>
     * "(" , rhs , ")"
     * </pre>
     */
    public final static String GROUP_CLOSE = ")";

    /**
     * <pre>
     * "[" , rhs , "]"
     * </pre>
     */
    public final static String OPTIONAL_OPEN = "[";

    /**
     * <pre>
     * "[" , rhs , "]"
     * </pre>
     */
    public final static String OPTIONAL_CLOSE = "]";

    /**
     * <pre>
     * range = terminal, '..', terminal
     * </pre>
     */
    public final static String RANGE = "..";

    /**
     * <pre>
     * "{" , rhs , "}"
     * </pre>
     */
    public final static String REPEATITION_OPEN = "{";

    /**
     * <pre>
     * "{" , rhs , "}"
     * </pre>
     */
    public final static String REPEATITION_CLOSE = "}";

    /**
     * <pre>
     * lhs , "=" , rhs , ";" ;
     * </pre>
     */
    public final static String TERMINATION = ";";

    /**
     * Stop creation
     */
    private EbnfGrammar() {
        throw new UnsupportedOperationException();
    }
}
