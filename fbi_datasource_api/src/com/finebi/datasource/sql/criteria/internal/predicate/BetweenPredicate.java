/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import java.io.Serializable;

/**
 * Models a <tt>BETWEEN</tt> {@link com.finebi.datasource.api.criteria.Predicate}.
 *
 * @author Steve Ebersole
 */
public class BetweenPredicate<Y>
		extends AbstractSimplePredicate
		implements Serializable {
	private final Expression<? extends Y> expression;
	private final Expression<? extends Y> lowerBound;
	private final Expression<? extends Y> upperBound;

	public BetweenPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<? extends Y> expression,
			Y lowerBound,
			Y upperBound) {
		this(
				criteriaBuilder,
				expression,
				criteriaBuilder.literal( lowerBound ),
				criteriaBuilder.literal( upperBound )
		);
	}

	public BetweenPredicate(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<? extends Y> expression,
			Expression<? extends Y> lowerBound,
			Expression<? extends Y> upperBound) {
		super( criteriaBuilder );
		this.expression = expression;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public Expression<? extends Y> getExpression() {
		return expression;
	}

	public Expression<? extends Y> getLowerBound() {
		return lowerBound;
	}

	public Expression<? extends Y> getUpperBound() {
		return upperBound;
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {
		Helper.possibleParameter( getExpression(), registry );
		Helper.possibleParameter( getLowerBound(), registry );
		Helper.possibleParameter( getUpperBound(), registry );
	}

	@Override
	public String render(boolean isNegated, RenderingContext renderingContext) {
		final String operator = isNegated ? " not between " : " between ";
		return ( (Renderable) getExpression() ).render( renderingContext )
				+ operator
				+ ( (Renderable) getLowerBound() ).render( renderingContext )
				+ " and "
				+ ( (Renderable) getUpperBound() ).render( renderingContext );
	}
}
