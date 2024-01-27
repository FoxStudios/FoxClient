package net.foxes4life.foxclient.util.transforms;

public enum Easing {
    Linear,
    Out,
    In,
    InQuad,
    OutQuad,
    InOutQuad,
    InCubic,
    OutCubic,
    InOutCubic,
    InQuart,
    OutQuart,
    InOutQuart,
    InQuint,
    OutQuint,
    InOutQuint,
    InSine,
    OutSine,
    InOutSine,
    InExpo,
    OutExpo,
    InOutExpo,
    InCirc,
    OutCirc,
    InOutCirc,
    InElastic,
    OutElastic,
    OutElasticHalf,
    OutElasticQuarter,
    InOutElastic,
    InBack,
    OutBack,
    InOutBack,
    InBounce,
    OutBounce,
    InOutBounce,
    OutPow10;

    private static final double elastic_const = 2 * Math.PI / .3;
    private static final double elastic_const2 = .3 / 4;
    private static final double back_const = 1.70158;
    private static final double back_const2 = back_const * 1.525;
    private static final double bounce_const = 1 / 2.75;
    private static final double expo_offset = Math.pow(2, -10);
    private static final double elastic_offset_full = Math.pow(2, -11);
    private static final double elastic_offset_half = Math.pow(2, -10) * Math.sin((.5 - elastic_const2) * elastic_const);
    private static final double elastic_offset_quarter = Math.pow(2, -10) * Math.sin((.25 - elastic_const2) * elastic_const);
    private static final double in_out_elastic_offset = Math.pow(2, -10) * Math.sin((1 - elastic_const2 * 1.5) * elastic_const / 1.5);

    public static double applyEasing(Easing easing, double time)
    {
        switch (easing) {
            default -> {
                return time;
            }
            case In, InQuad -> {
                return time * time;
            }
            case Out, OutQuad -> {
                return time * (2 - time);
            }
            case InOutQuad -> {
                if (time < .5)
                    return time * time * 2;

                return --time * time * -2 + 1;
            }
            case InCubic -> {
                return time * time * time;
            }
            case OutCubic -> {
                return --time * time * time + 1;
            }
            case InOutCubic -> {
                if (time < .5)
                    return time * time * time * 4;

                return --time * time * time * 4 + 1;
            }
            case InQuart -> {
                return time * time * time * time;
            }
            case OutQuart -> {
                return 1 - --time * time * time * time;
            }
            case InOutQuart -> {
                if (time < .5)
                    return time * time * time * time * 8;
                
                return --time * time * time * time * -8 + 1;
            }
            case InQuint -> {
                return time * time * time * time * time;
            }
            case OutQuint -> {
                return --time * time * time * time * time + 1;
            }
            case InOutQuint -> {
                if (time < .5)
                    return time * time * time * time * time * 16;
                
                return --time * time * time * time * time * 16 + 1;
            }
            case InSine -> {
                return 1 - Math.cos(time * Math.PI * .5);
            }
            case OutSine -> {
                return Math.sin(time * Math.PI * .5);
            }
            case InOutSine -> {
                return .5 - .5 * Math.cos(Math.PI * time);
            }
            case InExpo -> {
                return Math.pow(2, 10 * (time - 1)) + expo_offset * (time - 1);
            }
            case OutExpo -> {
                return -Math.pow(2, -10 * time) + 1 + expo_offset * time;
            }
            case InOutExpo -> {
                if (time < .5)
                    return .5 * (Math.pow(2, 20 * time - 10) + expo_offset * (2 * time - 1));
                
                return 1 - .5 * (Math.pow(2, -20 * time + 10) + expo_offset * (-2 * time + 1));
            }
            case InCirc -> {
                return 1 - Math.sqrt(1 - time * time);
            }
            case OutCirc -> {
                return Math.sqrt(1 - --time * time);
            }
            case InOutCirc -> {
                if ((time *= 2) < 1)
                    return .5 - .5 * Math.sqrt(1 - time * time);
                
                return .5 * Math.sqrt(1 - (time -= 2) * time) + .5;
            }
            case InElastic -> {
                return -Math.pow(2, -10 + 10 * time) * Math.sin((1 - elastic_const2 - time) * elastic_const) + elastic_offset_full * (1 - time);
            }
            case OutElastic -> {
                return Math.pow(2, -10 * time) * Math.sin((time - elastic_const2) * elastic_const) + 1 - elastic_offset_full * time;
            }
            case OutElasticHalf -> {
                return Math.pow(2, -10 * time) * Math.sin((.5 * time - elastic_const2) * elastic_const) + 1 - elastic_offset_half * time;
            }
            case OutElasticQuarter -> {
                return Math.pow(2, -10 * time) * Math.sin((.25 * time - elastic_const2) * elastic_const) + 1 - elastic_offset_quarter * time;
            }
            case InOutElastic -> {
                if ((time *= 2) < 1)
                    return -.5 * (Math.pow(2, -10 + 10 * time) * Math.sin((1 - elastic_const2 * 1.5 - time) * elastic_const / 1.5) - in_out_elastic_offset * (1 - time));
                
                return .5 * (Math.pow(2, -10 * --time) * Math.sin((time - elastic_const2 * 1.5) * elastic_const / 1.5) - in_out_elastic_offset * time) + 1;
            }
            case InBack -> {
                return time * time * ((back_const + 1) * time - back_const);
            }
            case OutBack -> {
                return --time * time * ((back_const + 1) * time + back_const) + 1;
            }
            case InOutBack -> {
                if ((time *= 2) < 1)
                    return .5 * time * time * ((back_const2 + 1) * time - back_const2);

                return .5 * ((time -= 2) * time * ((back_const2 + 1) * time + back_const2) + 2);
            }
            case InBounce -> {
                time = 1 - time;
                if (time < bounce_const)
                    return 1 - 7.5625 * time * time;
                if (time < 2 * bounce_const)
                    return 1 - (7.5625 * (time -= 1.5 * bounce_const) * time + .75);
                if (time < 2.5 * bounce_const)
                    return 1 - (7.5625 * (time -= 2.25 * bounce_const) * time + .9375);

                return 1 - (7.5625 * (time -= 2.625 * bounce_const) * time + .984375);
            }
            case OutBounce -> {
                if (time < bounce_const)
                    return 7.5625 * time * time;
                if (time < 2 * bounce_const)
                    return 7.5625 * (time -= 1.5 * bounce_const) * time + .75;
                if (time < 2.5 * bounce_const)
                    return 7.5625 * (time -= 2.25 * bounce_const) * time + .9375;

                return 7.5625 * (time -= 2.625 * bounce_const) * time + .984375;
            }
            case InOutBounce -> {
                if (time < .5)
                    return .5 - .5 * applyEasing(OutBounce, 1 - time * 2);

                return applyEasing(OutBounce, (time - .5) * 2) * .5 + .5;
            }
            case OutPow10 -> {
                return --time * Math.pow(time, 10) + 1;
            }
        }
    }
}
