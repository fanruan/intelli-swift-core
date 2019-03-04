package com.fr.swift.cloud.analysis;

/**
 * Created by lyon on 2018/12/22.
 */
public interface Grader {

    long grade(long in);

    class Memory implements Grader {
        static int[] memories = {1, 5, 10, 20, 30, 50};

        @Override
        public long grade(long memory) {
            double grade = 100 / (memories.length * 1.0);
            int i = 0;
            while (i < memories.length && memories[i] < memory) {
                i++;
            }
            return Math.round(grade * i);
        }
    }

    class Time implements Grader {
        static int timeConsumeThreshold = 2;
        static int[] seconds = {2, 5, 10, 20, 30, 60};

        @Override
        public long grade(long consume) {
            double grade = 100 / (seconds.length * 1.0);
            int i = 0;
            while (i < seconds.length && seconds[i] < consume) {
                i++;
            }
            return Math.round(grade * i);
        }
    }

    class Rank implements Grader {
        static double[] topPercents = {.01, 0.05, 0.1, 0.2, 0.5};
        private int total;

        public Rank(int total) {
            this.total = total;
        }

        @Override
        public long grade(long rank) {
            double grade = 100 / (topPercents.length * 1.0);
            double p = rank * 1.0 / total;
            int i = 0;
            while (i < topPercents.length && topPercents[i] < p) {
                i++;
            }
            return Math.round(grade * (topPercents.length - i));
        }
    }
}
