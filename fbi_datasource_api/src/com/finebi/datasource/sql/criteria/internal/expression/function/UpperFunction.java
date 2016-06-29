/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression.function;

import com.finebi.datasource.api.criteria.Expression;
import java.io.Serializable;

/**
 * Models the ANSI SQL <tt>UPPER</tt> function.
 *
 * @author Steve Ebersole
 */
public class UpperFunction
		extends ParameterizedFunctionExpression<String>
		implements Serializable {
	public static final String NAME = "upper";

	public UpperFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> string) {
		super( criteriaBuilder, String.class, NAME, string );
	}

	@Override
	protected boolean isStandardJpaFunction() {
		return true;
	}
}
