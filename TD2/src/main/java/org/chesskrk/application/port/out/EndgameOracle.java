package org.chesskrk.application.port.out;

import org.chesskrk.domain.Position;

/** Interface que tout évaluateur implémente. Un évaluateur indique si une position est gagnée, nulle ou perdue pour le camp au trait, et en combien de demi-coups le mat arrive. */
public interface EndgameOracle {
    Evaluation evaluate(Position position);
}
