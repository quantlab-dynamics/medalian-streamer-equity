package com.market.feed.util;

import com.market.feed.MarketFeedApplication;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.NoBracketingException;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ImpliedVolatility {


    private static final Logger logger = LoggerFactory.getLogger(ImpliedVolatility.class);

    /**
     *
     * @param marketPrice
     * @param S underlying asset price
     * @param K strike price
     * @param T time to expiration in years
     * @param r  risk-free interest rate
     * @param isCall
     * q: annualized continuous dividend rate
     * @return implied volatility
     */

    public double calculateImpliedVolatility(double marketPrice, double S, double K, double T, double r, boolean isCall) {
        try{
            if (marketPrice <=0.0){
                return 0.0;
            }
            if (T == 0) {
                // Directly calculate intrinsic value for expired options
                return isCall ? Math.max(S - K, 0) : Math.max(K - S, 0);
            }
            BrentSolver solver = new BrentSolver();
            UnivariateFunction function = vol -> BlackScholesOptionPrice(S, K, T, r, vol, isCall) - marketPrice;
            double result =solver.solve(10000, function, 0.01, 500)*100;
            return result;
        }catch (Exception e){
//            e.printStackTrace();
            logger.error("ImpliedVolatility calculateImpliedVolatility" + e);
            return 16;
        }
    }


    private double BlackScholesOptionPrice(double S, double K, double T, double r, double vol, boolean isCall) {
        // Calculate d1 and d2
        double d1 = (Math.log(S / K) + (r + vol * vol / 2.0) * T) / (vol * Math.sqrt(T));
        double d2 = d1 - vol * Math.sqrt(T);

        if (isCall) {
            return S * normalCdf(d1) - K * Math.exp(-r * T) * normalCdf(d2);
        } else {
            return K * Math.exp(-r * T) * normalCdf(-d2) - S * normalCdf(-d1);
        }
    }

    // Normal CDF function
    private double normalCdf(double x) {
        return 0.5 * (1.0 + org.apache.commons.math3.special.Erf.erf(x / Math.sqrt(2)));
    }


}
