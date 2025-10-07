package com.market.feed.util;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class NewImpliedVolatility {

    private static final Logger logger = LoggerFactory.getLogger(ImpliedVolatility.class);

    private static final double FALLBACK_IV = 0.25; // default fallback if IV can't be solved

    /**
     * Calculates implied volatility using Black-Scholes and Brent's method.
     *
     * @param marketPrice  Observed market option price
     * @param S            Spot/Future price of the underlying
     * @param K            Strike price
     * @param T            Time to expiration in years
     * @param r            Risk-free interest rate (annualized)
     * @param isCall       true for Call, false for Put
     * @return             Implied volatility (decimal form), or fallback if not solvable
     */
    public double calculateImpliedVolatility(double marketPrice, double S, double K, double T, double r, boolean isCall) {
        try {
            if (marketPrice <= 0.0 || T <= 0.0) {
                // logger.warn("Invalid input: marketPrice={}, T={}", marketPrice, T);
                return FALLBACK_IV;
            }

            double lowerVol = 0.01;
            double upperVol = 5.0;

            double fLow = BlackScholesOptionPrice(S, K, T, r, lowerVol, isCall) - marketPrice;
            double fHigh = BlackScholesOptionPrice(S, K, T, r, upperVol, isCall) - marketPrice;

            // Check if a root exists in the range
            if (fLow * fHigh > 0) {
//                logger.warn("IV solve failed: function values at endpoints do not bracket a root. "
//                        + "Returning fallback IV. Range=[{} - {}], values=[{} - {}]", lowerVol, upperVol, fLow, fHigh);
                return FALLBACK_IV;
            }

            BrentSolver solver = new BrentSolver();
            UnivariateFunction bsFunction = vol -> BlackScholesOptionPrice(S, K, T, r, vol, isCall) - marketPrice;

            double impliedVol = solver.solve(10000, bsFunction, lowerVol, upperVol);
            return impliedVol; // returned as decimal (e.g., 0.22 for 22%)

        } catch (Exception e) {
            logger.error("Implied volatility calculation failed: {}", e.getMessage(), e);
            return FALLBACK_IV;
        }
    }

    /**
     * Black-Scholes option pricing formula.
     *
     * @param S      Spot/Future price
     * @param K      Strike price
     * @param T      Time to expiry (in years)
     * @param r      Risk-free interest rate
     * @param vol    Volatility (decimal)
     * @param isCall true for Call, false for Put
     * @return       Option price
     */
    private double BlackScholesOptionPrice(double S, double K, double T, double r, double vol, boolean isCall) {
        double d1 = (Math.log(S / K) + (r + 0.5 * vol * vol) * T) / (vol * Math.sqrt(T));
        double d2 = d1 - vol * Math.sqrt(T);

        NormalDistribution normal = new NormalDistribution();
        if (isCall) {
            return S * normal.cumulativeProbability(d1) - K * Math.exp(-r * T) * normal.cumulativeProbability(d2);
        } else {
            return K * Math.exp(-r * T) * normal.cumulativeProbability(-d2) - S * normal.cumulativeProbability(-d1);
        }
    }
}
