fn sqrt(n: double) -> double {
    let l: double = 0.0;
    let r: double = n;
    let mid: double;
    let mid2: double;
    while r-l > 0.000001 {
        mid = (l+r) / 2.0;
        mid2 = mid*mid;
        if (mid2 == n) {
            return mid;
        }
        if (mid2 < n) {
            l = mid;
        }
        else {
            r = mid;
        }
    }
    return mid;
}

fn mod(i: int, j: int) -> int {
    let k: int = (i/j) as double as int;
    return (i - j*k) as double as int;
}

fn judge_mod(i: int, j: int, sq: int) -> int {
   if j > sq {
       return 0;
   } else if mod(i, j) == 0 {
       return 0;
   } else {
       return 1;
   }
}

fn main() -> void {
    let N: int;
    let i: int;
    let j: int;
    let sq: int;
    let sqd: double;
    N = getint();
    i = 2;
    while i <= N {
        if mod(i, 2) != 0 {
            sqd = sqrt(i as double);
            sq = sqrt(i as double) as int;
            if (sqd as int != sq) {
                putint(-1);
                putln();
                return;
            }
            j = 2;
            while judge_mod(i, j, sq) {
                j = j + 1;
            }
            if (j > sq) {
                putint(i);
                putln();
            }
        }
        i = i + 1;
    }
}