"""
Tamil Unicode -> AU-KBC-style romanization

Purpose
-------
Bridges a native Tamil Unicode word (as displayed to the user) into the
romanized key-space that the TamilWordnet / AU-KBC graph actually stores
words under (the "paTittaan" style used throughout Rajendran's work):

    - retroflex consonants get a CAPITAL letter        (ட -> T, ண -> N, ல -> L, ற -> R)
    - long vowels are written as a DOUBLED letter       (ா -> aa, ீ -> ii, ூ -> uu, ஏ -> ee, ஓ -> oo)
    - short vowels / plain consonants stay lowercase, single letter

This is NOT a general-purpose transliteration engine (that's what open-tamil's
txt2roman already does, and why it produced a mismatched key space). This is
a small, closed lookup table over Tamil's fixed ~247-character inventory,
tuned to match the specific convention your graph was built with.

IMPORTANT: The exact letter choices below (e.g. for ழ, ன, ஞ) are the AU-KBC/
Rajendran convention as commonly documented, but transliteration schemes vary
subtly between projects/papers. Before wiring this into the pipeline, run
`self_test()` below against 10-20 REAL labels pulled from your `twn` table
(e.g. SELECT DISTINCT label FROM twn LIMIT 20;) and adjust the tables if any
letter doesn't match what's actually stored. That's a 10-minute calibration
step that will save you from silently-wrong lookups later.
"""

import re

# ---------------------------------------------------------------------------
# 1. Independent vowels (used when a vowel appears on its own, not attached
#    to a consonant)
# ---------------------------------------------------------------------------
INDEPENDENT_VOWELS = {
    "அ": "a",  "ஆ": "aa", "இ": "i",  "ஈ": "ii",
    "உ": "u",  "ஊ": "uu", "எ": "e",  "ஏ": "ee",
    "ஐ": "ai", "ஒ": "o",  "ஓ": "oo", "ஔ": "au",
    "ஃ": "h",   # aaytham
}

# ---------------------------------------------------------------------------
# 2. Vowel signs (matras) attached to a consonant. "" (empty) represents the
#    inherent 'a' that a bare consonant letter carries.
# ---------------------------------------------------------------------------
VOWEL_SIGNS = {
    "":  "a",    # inherent vowel
    "\u0BBE": "aa",  # ா
    "\u0BBF": "i",   # ி
    "\u0BC0": "ii",  # ீ
    "\u0BC1": "u",   # ு
    "\u0BC2": "uu",  # ூ
    "\u0BC6": "e",   # ெ
    "\u0BC7": "ee",  # ே
    "\u0BC8": "ai",  # ை
    "\u0BCA": "o",   # ொ
    "\u0BCB": "oo",  # ோ
    "\u0BCC": "au",  # ௌ
}

PULLI = "\u0BCD"  # ் virama / pulli -> pure consonant, no vowel

# ---------------------------------------------------------------------------
# 3. Consonants. Retroflex / "hard" consonants get a CAPITAL base letter per
#    the AU-KBC convention; everything else is lowercase.
# ---------------------------------------------------------------------------
CONSONANTS = {
    "க": "k",  "ங": "ng",
    "ச": "c",  "ஜ": "j",  "ஞ": "nj",
    "ட": "T",  "ண": "N",
    "த": "t",  "ந": "nd",  # dental na -> "nd" (distinguishes it from ன below;
                            # confirmed against real twn.label data, e.g.
                            # "ndaaLin" from நாளின், "nduuRRaaNTin" from நூற்றாண்டின்)
    "ப": "p",  "ம": "m",
    "ய": "y",  "ர": "r",  "ல": "l",
    "வ": "v",
    "ழ": "zh",
    "ள": "L",
    "ற": "R",
    "ன": "n",
    # grantha / loan consonants sometimes seen in Tamil Unicode text
    "ஶ": "sh", "ஷ": "sh", "ஸ": "s", "ஹ": "h", "க்ஷ": "ksh",
}

TAMIL_DIGITS = {
    "௦": "0", "௧": "1", "௨": "2", "௩": "3", "௪": "4",
    "௫": "5", "௬": "6", "௭": "7", "௮": "8", "௯": "9",
}

_TAMIL_CHAR_RE = re.compile(r"[\u0B80-\u0BFF]")


def _is_tamil(ch: str) -> bool:
    return bool(_TAMIL_CHAR_RE.match(ch))


def transliterate(word: str) -> str:
    """
    Convert a Tamil Unicode string to the AU-KBC-style romanization used
    as the graph's lookup key. Non-Tamil characters (spaces, punctuation,
    Latin/digits already in the string) pass through unchanged.
    """
    out = []
    i = 0
    n = len(word)

    while i < n:
        ch = word[i]

        if ch in TAMIL_DIGITS:
            out.append(TAMIL_DIGITS[ch])
            i += 1
            continue

        if ch in CONSONANTS:
            base = CONSONANTS[ch]
            i += 1

            # Check for explicit pulli (pure consonant, no vowel)
            if i < n and word[i] == PULLI:
                out.append(base)
                i += 1
                continue

            # Check for an attached vowel sign
            if i < n and word[i] in VOWEL_SIGNS:
                vowel = VOWEL_SIGNS[word[i]]
                out.append(base + vowel)
                i += 1
                continue

            # No pulli, no vowel sign -> inherent 'a'
            out.append(base + "a")
            continue

        if ch in INDEPENDENT_VOWELS:
            out.append(INDEPENDENT_VOWELS[ch])
            i += 1
            continue

        if not _is_tamil(ch):
            # pass through spaces, latin letters, punctuation, digits as-is
            out.append(ch)
            i += 1
            continue

        # Any other Tamil codepoint we didn't map explicitly (rare marks,
        # etc.) - skip silently rather than corrupt the key, but this is a
        # spot worth logging in production.
        i += 1

    return "".join(out)



def self_test():
    """
    Sanity checks using words referenced earlier in this project's
    conversation. Replace/extend these with real rows pulled straight from
    `twn.label` before trusting this in the pipeline.
    """
    samples = {
        "மரம்": "maram",
        "மரங்களுக்கு": "marangkaLukku",
        "ஆசான்": "aacaan",
        "நாளின்": "ndaaLin",
        "உங்கள்_புதிய_வார்த்தை": "anything_here",   # <-- add your new words like this
    }
    for tamil, expected_hint in samples.items():
        got = transliterate(tamil)
        print(f"{tamil!r:20} -> {got!r:25} (reference guess: {expected_hint!r})")

if __name__ == "__main__":
    self_test()