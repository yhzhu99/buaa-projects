fn calcPi() -> double {
    let i: int = 1;
    let rtv: double = 0.0;
    let flag: int = -1;
    while ((1.0 / i as double) > 1.0E-6) {
        rtv = rtv + flag as double / i as double;
        i = i + 2;
        flag = -flag;
    }
    return rtv * 4.0;
}

fn calcE() -> double {
    let rtv: double = 1.0;
    let i: int = 1;
    let j: double = 1.0;
    while (i < 1000) {
        j = j * i as double;
        rtv = rtv + 1.0 / j;
        i = i + 1;
    }
    return rtv;
}

fn main() -> void {
    putdouble(calcPi());
    putln();
    putdouble(calcE());
}