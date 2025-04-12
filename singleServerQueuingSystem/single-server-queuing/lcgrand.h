#ifndef LCGRAND_H
#define LCGRAND_H

#ifdef __cplusplus
extern "C" {
#endif

/* Constants for the PMMLCG algorithm based on Marse and Roberts (1983) */
#define MODLUS 2147483647L
#define MULT1  24112L
#define MULT2  26143L

/*
 * The following array contains the initial seeds for the 100 streams.
 * The first value (1) is for stream 0.
 */
static long zrng[] = {
    1L,
    1973272912L, 281629770L, 20006270L, 1280689831L, 2096730329L, 1933576050L,
    913566091L, 246780520L, 1363774876L, 604901985L, 1511192140L, 1259851944L,
    824064364L, 150493284L, 242708531L, 75253171L, 1964472944L, 1202299975L,
    233217322L, 1911216000L, 726370533L, 403498145L, 993232223L, 1103205531L,
    762430696L, 1922803170L, 1385516923L, 76271663L, 413682397L, 726466604L,
    336157058L, 1432650381L, 1120463904L, 595778810L, 877722890L, 1046574445L,
    68911991L, 2088367019L, 748545416L, 622401386L, 2122378830L, 640690903L,
    1774806513L, 2132545692L, 2079249579L, 78130110L, 852776735L, 1187867272L,
    1351423507L, 1645973084L, 1997049139L, 922510944L, 2045512870L, 898585771L,
    243649545L, 1004818771L, 773686062L, 403188473L, 372279877L, 1901633463L,
    498067494L, 2087759558L, 493157915L, 597104727L, 1530940798L, 1814496276L,
    536444882L, 1663153658L, 855503735L, 67784357L, 1432404475L, 619691088L,
    119025595L, 880802310L, 176192644L, 1116780070L, 277854671L, 1366580350L,
    1142483975L, 2026948561L, 1053920743L, 786262391L, 1792203830L, 1494667770L,
    1923011392L, 1433700034L, 1244184613L, 1147297105L, 539712780L, 1545929719L,
    190641742L, 1645390429L, 264907697L, 620389253L, 1502074852L, 927711160L,
    364849192L, 2049576050L, 638580085L, 547070247L
};

/*
 * Generates and returns a random floating-point number in the interval (0,1)
 * using the specified stream.
 */
static inline float lcgrand(int stream)
{
    long zi, lowprd, hi31;

    zi = zrng[stream];

    lowprd = (zi & 65535L) * MULT1;
    hi31   = (zi >> 16) * MULT1 + (lowprd >> 16);
    zi     = ((lowprd & 65535L) - MODLUS) +
             ((hi31 & 32767L) << 16) + (hi31 >> 15);
    if (zi < 0)
        zi += MODLUS;

    lowprd = (zi & 65535L) * MULT2;
    hi31   = (zi >> 16) * MULT2 + (lowprd >> 16);
    zi     = ((lowprd & 65535L) - MODLUS) +
             ((hi31 & 32767L) << 16) + (hi31 >> 15);
    if (zi < 0)
        zi += MODLUS;

    zrng[stream] = zi;
    return ((zi >> 7) | 1) / 16777216.0f;
}

/*
 * Sets the seed for the given stream.
 */
static inline void lcgrandst(long zset, int stream)
{
    zrng[stream] = zset;
}

/*
 * Returns the current seed for the given stream.
 */
static inline long lcgrandgt(int stream)
{
    return zrng[stream];
}

#ifdef __cplusplus
}
#endif

#endif /* LCGRAND_H */
