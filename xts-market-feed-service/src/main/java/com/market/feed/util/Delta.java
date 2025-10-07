package com.market.feed.util;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
public class Delta {

    private static final Logger logger = LoggerFactory.getLogger(Delta.class);;
    private double N(double d1) {
        NormalDistribution normalDistribution = new NormalDistribution();
        return normalDistribution.cumulativeProbability(d1);
    }

    private double calculateD1(double future, double strike, double riskFreeInterest, double sigma, double timeToExpiry) {
        double sigmaSquared = sigma * sigma;
        double numerator = Math.log(future / strike) + (riskFreeInterest + sigmaSquared / 2.0) * timeToExpiry;
        double denominator = sigma * Math.sqrt(timeToExpiry);

        return numerator / denominator;
    }

    /**
     *
     * @param future future price of the underling
     * @param strike strike price
     * @param timeToExpiry time to expiration in years
     * @param iv implied volatility
     * @param riskFreeInterest annual risk-free interest rate
     * @param optionType option type call or put
     * @return delta value
     */

    public double calculateDelta(double future, int strike, double timeToExpiry, double iv, double riskFreeInterest, char optionType) {
        if (future <= 0.0){
            return 0.0;
        }
        double d1 =calculateD1(future, strike,riskFreeInterest,iv, timeToExpiry);
//        logger.info("d1: " + d1);
//        logger.info("Nd1: " + N(d1));
        double de = N(d1);
        if (optionType =='p'){
            return  N(d1)-1.0;
        }else {
            return N(d1);
        }
    }
}

